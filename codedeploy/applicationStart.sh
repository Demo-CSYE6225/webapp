sudo chmod 777 /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar
sudo chmod 777 -R /home/ubuntu
sudo chown -R ubuntu:ubuntu /home/ubuntu/*
sudo systemctl stop tomcat9
sudo systemctl start amazon-cloudwatch-agent
sleep 10
sudo kill -9 $(sudo lsof -t -i:8080)
sleep 10
java -jar /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar
sleep 10