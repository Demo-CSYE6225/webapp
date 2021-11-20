sudo systemctl stop tomcat9
sudo chmod 777 /home/ubuntu/cloudwatch-agent.json

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl -a fetch-config -m ec2 -c file:/home/ubuntu/cloudwatch-agent.json -s
sudo chmod 777 amazon-cloudwatch-agent
sudo systemctl start amazon-cloudwatch-agent

sudo chmod 777 /home/ubuntu/spring-mvc-rest-0.0.1-SNAPSHOT.jar
