sudo systemctl stop tomcat9

sudo kill -9 $(sudo lsof -t -i:8080)

sudo rm -r /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar