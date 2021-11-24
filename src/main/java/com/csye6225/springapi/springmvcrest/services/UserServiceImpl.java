package com.csye6225.springapi.springmvcrest.services;

import com.amazonaws.AmazonServiceException;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.csye6225.springapi.springmvcrest.Security.Crypt;
import com.csye6225.springapi.springmvcrest.domain.Profile;
import com.csye6225.springapi.springmvcrest.domain.User;
import com.csye6225.springapi.springmvcrest.model.UserInfo;
import com.csye6225.springapi.springmvcrest.repositories.ImageRepository;
import com.csye6225.springapi.springmvcrest.repositories.UserRepository;
import com.timgroup.statsd.StatsDClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/v1/user")
public class UserServiceImpl {

    @Autowired
    private final UserRepository userRepository;

    @Autowired
    private final ImageRepository imageRepository;

    @Autowired
    private StatsDClient statsd;

    private Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);
    @Autowired
    private AmazonS3 s3;
//    final AmazonS3 s3 = AmazonS3ClientBuilder.standard().withRegion(Regions.DEFAULT_REGION).build();

    @Value("${bucketName}")
    private String bucket;
//    private String bucket="csye6225a4.prod.domain.tld";
    private String bucketURL="https://s3.console.aws.amazon.com/s3/buckets/csye6225.prod.domain.tld?region=us-east-1&tab=objects";


    Crypt crypt = new Crypt();

    public UserServiceImpl(UserRepository userRepository, ImageRepository imageRepository) {
        this.userRepository = userRepository;
        this.imageRepository = imageRepository;
    }

    public String[] authenticate_user(List header){
        long start = System.currentTimeMillis();
        String authorization = header.get(0).toString();
        String base64Credentials = authorization.substring("Basic".length()).trim();
        byte[] credDecoded = Base64.getDecoder().decode(base64Credentials);
        String credentials = new String(credDecoded, StandardCharsets.UTF_8);
        final String[] values = credentials.split(":", 2);
        User userdata =  userRepository.findByUsername(values[0]);
        long end = System.currentTimeMillis();
        long timeElapsed = end - start;
        logger.info("Time taken to authenticate user database is " + timeElapsed + "ms");
        statsd.recordExecutionTime("AuthenticateUserDBTime",timeElapsed);
        if(userdata!=null && crypt.checkPassword(values[1],userdata.getPassword()))
            return values;
        else
            return null;
    }

    @GetMapping("/self")
    public ResponseEntity<User> GetUserInfo( @RequestHeader("authorization") List header) {
        try{
            statsd.incrementCounter("GetUserInfoApi");
            long start = System.currentTimeMillis();
            String[] result = authenticate_user(header);
            if(result !=null) {
                logger.info("Calling find user database call");
                long startdb = System.currentTimeMillis();
                 User response = userRepository.findByUsername(result[0]);
                long enddb = System.currentTimeMillis();
                long timeElapseddb = enddb - startdb;
                logger.info("Time taken by get user database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("getUserDBTime",timeElapseddb);
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                logger.info("Time taken by get user api call is " + timeElapsed + "ms");
                statsd.recordExecutionTime("getUserAPITime",timeElapsed);
                logger.info("**********User details fetched successfully !**********");
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
            logger.error("Invalid credentials");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

        }
        catch (Exception e){
            logger.info("**********Exception!**********");
            logger.error(e.toString());
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/self")
    public ResponseEntity<User> UpdateUserInfo(@RequestBody UserInfo user,@RequestHeader("authorization") List header) {
        try {
            statsd.incrementCounter("UpdateUserApi");
            long start = System.currentTimeMillis();
            String[] result = authenticate_user(header);
            if (result != null) {
                logger.info("Calling find user database call");
                long startdb = System.currentTimeMillis();
                User userData = userRepository.findByUsername(user.getUsername());
                long enddb = System.currentTimeMillis();
                long timeElapseddb = enddb - startdb;
                logger.info("Time taken by get user database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("getUserDBTime",timeElapseddb);
                if (userData != null) {
                    userData.setFirst_name(user.getFirst_name());
                    userData.setLast_name(user.getLast_name());
                    userData.setPassword(crypt.hashPassword(user.getPassword()));
                    userData.setAccount_updated(java.time.Clock.systemUTC().instant().toString());
                    logger.info("Calling save user database call");
                    startdb = System.currentTimeMillis();
                    userRepository.save(userData);
                    enddb = System.currentTimeMillis();
                    timeElapseddb = enddb - startdb;
                    logger.info("Time taken by save user database call is " + timeElapseddb + "ms");
                    statsd.recordExecutionTime("saveUserDBTime",timeElapseddb);
                    logger.info("**********User details save successfully !**********");
                }
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                logger.info("Time taken by save user api call is " + timeElapsed + "ms");
                statsd.recordExecutionTime("saveUserAPITime",timeElapsed);
                if (userData != null)
                    return new ResponseEntity<>(HttpStatus.OK);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            logger.error("Invalid credentials");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            logger.info("**********Exception!**********");
            logger.error(e.toString());
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PostMapping
    public ResponseEntity<User> CreateUser(@RequestBody UserInfo user) {
        try{
            statsd.incrementCounter("CreateUserApi");
            long start = System.currentTimeMillis();
            String current_date = java.time.Clock.systemUTC().instant().toString();
            logger.info("Calling find user database call");
            long startdb = System.currentTimeMillis();
            User findUser = userRepository.findByUsername(user.getUsername());
            long enddb = System.currentTimeMillis();
            long timeElapseddb = enddb - startdb;
            logger.info("Time taken by get user database call is " + timeElapseddb + "ms");
            statsd.recordExecutionTime("getUserDBTime",timeElapseddb);

            HttpStatus status = HttpStatus.BAD_REQUEST;
            if(findUser == null){
                User userData = new User(user.getFirst_name(),user.getLast_name(),user.getUsername(),
                        crypt.hashPassword(user.getPassword()),current_date,current_date);
                logger.info("Calling save user database call");
                startdb = System.currentTimeMillis();
                userRepository.save( userData);
                enddb = System.currentTimeMillis();
                timeElapseddb = enddb - startdb;
                logger.info("Time taken by save user database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("saveUserDBTime",timeElapseddb);
                logger.info("**********Created New User**********");
                status = HttpStatus.CREATED;
            }
            long end = System.currentTimeMillis();
            long timeElapsed = end - start;
            logger.info("Time taken by save user api call is " + timeElapsed + "ms");
            statsd.recordExecutionTime("createUserAPITime",timeElapsed);
            logger.info("**********User details save successfully !**********");
            return new ResponseEntity<>(status);

        } catch (Exception e){
            logger.info("**********Exception!**********");
            logger.error(e.toString());
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/self/pic")
    public ResponseEntity<Profile> GetProfileInfo(@RequestHeader("authorization") List header) {
        try{
            statsd.incrementCounter("GetProfileInfoApi");
            long start = System.currentTimeMillis();
            String[] result = authenticate_user(header);
            HttpStatus status;
            if(result !=null){

                User userData = userRepository.findByUsername(result[0]);
                String userID = userData.getId();
                logger.info("Calling find profile database call");
                long startdb = System.currentTimeMillis();
                Profile image = imageRepository.findByUserid(userID);
                long enddb = System.currentTimeMillis();
                long timeElapseddb = enddb - startdb;
                logger.info("Time taken by get profile database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("getProfileInfoDBTime",timeElapseddb);
                if(image != null)
                    status = HttpStatus.OK;
                else status = HttpStatus.NOT_FOUND;
                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                logger.info("Time taken by get profile api call is " + timeElapsed + "ms");
                statsd.recordExecutionTime("getProfileInfoAPITime",timeElapsed);
                logger.info("**********Profile details fetched successfully !**********");
                return new ResponseEntity<>(image,status);
            }
            logger.error("Invalid credentials");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            logger.info("**********Exception!**********");
            logger.error(e.toString());
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/self/pic")
    public ResponseEntity<Profile> DeleteProfileInfo(@RequestHeader("authorization") List header) {
        try{
            statsd.incrementCounter("DeleteProfileInfoApi");
            long start = System.currentTimeMillis();
            String[] result = authenticate_user(header);

            if(result !=null){
                logger.info("Calling find user and profile database call");
                long startdb = System.currentTimeMillis();
                User userData = userRepository.findByUsername(result[0]);
                String userID = userData.getId();
                Profile imageData = imageRepository.findByUserid(userID);
                long enddb = System.currentTimeMillis();
                long timeElapseddb = enddb - startdb;
                logger.info("Time taken by get user and profile database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("getUserProfileInfoDBTime",timeElapseddb);
                HttpStatus status;
                if (imageData != null) {
                    logger.info("Calling AWS S3");
                    startdb = System.currentTimeMillis();
                    s3.deleteObject(bucket, imageData.getUrl());
                    enddb = System.currentTimeMillis();
                    timeElapseddb = enddb - startdb;
                    logger.info("Time taken by S3 delete is " + timeElapseddb + "ms");
                    statsd.recordExecutionTime("DeleteS3Time",timeElapseddb);

                    logger.info("Calling delete profile database call");
                    startdb = System.currentTimeMillis();
                    imageRepository.deleteByUserid(userID);
                    enddb = System.currentTimeMillis();
                    timeElapseddb = enddb - startdb;
                    logger.info("Time taken by delete profile database call is " + timeElapseddb + "ms");
                    statsd.recordExecutionTime("deleteProfileInfoDBTime",timeElapseddb);
                    status = HttpStatus.OK;
                    logger.info("**********Profile details deleted successfully !**********");
                }
                else status = HttpStatus.NOT_FOUND;

                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                logger.info("Time taken by delete profile api call is " + timeElapsed + "ms");
                statsd.recordExecutionTime("deleteProfileInfoAPITime",timeElapsed);

                return new ResponseEntity<>(status);
            }
            logger.error("Invalid credentials");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        catch (Exception e){
            logger.info("**********Exception!**********");
            logger.error(e.toString());
            return new ResponseEntity<>(null,HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
// test

    @PostMapping("/self/pic")
    public ResponseEntity<Profile> UpdateProfileInfo(@RequestBody byte[] byteFile ,@RequestHeader("authorization") List header) throws IOException {
        statsd.incrementCounter("UpdateProfileInfoApi");
        long start = System.currentTimeMillis();
        String fileUrl = "";
        String fileName = new Date().getTime()+"-image.jpeg";
        // String fileName = multipartFile.getOriginalFilename();
        File file = new File(fileName);
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(byteFile);
            fos.close();
        String current_date = java.time.Clock.systemUTC().instant().toString();
        try {
            // File file = convertMultiPartToFile(multipartFile);
//            fileUrl = bucketURL+"/"+bucket+"/"+fileName;
            String[] result = authenticate_user(header);
            User userData = userRepository.findByUsername(result[0]);
            String userID = userData.getId();
            fileUrl = userID+"/"+fileName;

            if (result != null) {
                logger.info("Calling find profile database call");
                long startdb = System.currentTimeMillis();
                Profile imageData = imageRepository.findByUserid(userID);
                long enddb = System.currentTimeMillis();
                long timeElapseddb = enddb - startdb;
                logger.info("Time taken by get profile database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("getProfileInfoDBTime",timeElapseddb);

                if (imageData != null) {
                    logger.info("Calling AWS S3 delete");
                    startdb = System.currentTimeMillis();
                    s3.deleteObject(bucket, imageData.getUrl());
                    enddb = System.currentTimeMillis();
                    timeElapseddb = enddb - startdb;
                    logger.info("Time taken by S3 delete is " + timeElapseddb + "ms");
                    statsd.recordExecutionTime("DeleteS3Time",timeElapseddb);

                    imageData.setUpload_date(current_date);
                    imageData.setFile_name(fileName);
                    imageData.setUrl(fileUrl);
                } else {
                    imageData = new Profile(fileName, fileUrl, current_date, userID);
                }

                logger.info("Calling AWS S3 put");
                startdb = System.currentTimeMillis();
                s3.putObject(bucket,userID+"/"+fileName,file );
                enddb = System.currentTimeMillis();
                timeElapseddb = enddb - startdb;
                logger.info("Time taken by S3 put is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("PutS3Time",timeElapseddb);

                logger.info("Calling save profile database call");
                startdb = System.currentTimeMillis();
                imageRepository.save(imageData);
                enddb = System.currentTimeMillis();
                timeElapseddb = enddb - startdb;
                logger.info("Time taken by save profile database call is " + timeElapseddb + "ms");
                statsd.recordExecutionTime("saveProfileDBTime",timeElapseddb);
                logger.info("**********User details save successfully !**********");

                long end = System.currentTimeMillis();
                long timeElapsed = end - start;
                logger.info("Time taken by update profile api call is " + timeElapsed + "ms");
                statsd.recordExecutionTime("updateProfileInfoAPITime",timeElapsed);

                return new ResponseEntity<>(HttpStatus.CREATED);
            }
            logger.error("Invalid credentials");
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
        logger.info("**********Exception!**********");
        logger.error(e.toString());
         Profile profile = new Profile(e.toString(),"",current_date,"");
        return new ResponseEntity<>(profile,HttpStatus.BAD_REQUEST);
        }

    }
}

