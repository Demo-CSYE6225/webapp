sudo chmod 777 /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar
sudo systemctl stop tomcat9
java -jar /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar stop
sleep 10
sudo kill -9 $(sudo lsof -t -i:8080)
sleep 10
java -jar /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar > /dev/null 2> /dev/null < /dev/null &