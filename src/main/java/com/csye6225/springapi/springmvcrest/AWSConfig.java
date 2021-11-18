package com.csye6225.springapi.springmvcrest;//package com.csye6225.springapi.springmvcrest;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//
//import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.AmazonS3ClientBuilder;
//
//@Configuration
//public class AWSConfig {
//    @Bean
//    public static AmazonS3Client amazonS3Client() {
//
//        System.out.println("Starting s3 client");
//        return (AmazonS3Client) AmazonS3ClientBuilder.standard()
//                .withCredentials(new DefaultAWSCredentialsProviderChain())
//                .build();
//    }
//}

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AWSConfig {
//
//    @Value("${cloud.aws.credentials.access-key}")
//    private String accessKey;
//
//    @Value("${cloud.aws.credentials.secret-key}")
//    private String accessSecret;
//    @Value("${cloud.aws.region.static}")
//    private String region;

    @Bean
    public AmazonS3Client s3Client() {
//        AWSCredentials credentials = new BasicAWSCredentials(accessKey, accessSecret);
//        return AmazonS3ClientBuilder.standard()
//                .withCredentials(new AWSStaticCredentialsProvider(credentials))
//                .withRegion(region).build();
                return (AmazonS3Client) AmazonS3ClientBuilder.standard()
                .withCredentials(new DefaultAWSCredentialsProviderChain())
                .build();
    }

}