sudo yum upgrade -y
sudo yum update -y
sudo yum install -y java-17-openjdk
sudo yum install -y tomcat
sudo yum install -y maven
sudo yum install ruby -y
sudo yum install wget
cd /home/ec2-user
wget https://aws-codedeploy-us-west-2.s3.us-west-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo service codedeploy-agent status
sudo service codedeploy-agent start
sudo yum install amazon-cloudwatch-agent