sudo yum upgrade -y
sudo yum update -y
sudo yum install -y java-17.0.2-openjdk
sudo yum install -y tomcat
sudo yum install -y maven
rpm --import https://repo.mysql.com/RPM-GPG-KEY-mysql-2022
sudo wget https://dev.mysql.com/get/mysql57-community-release-el7-11.noarch.rpm
sudo yum localinstall mysql57-community-release-el7-11.noarch.rpm -y
sudo yum install mysql-community-server -y
sudo systemctl start mysqld.service
pwd=`sudo grep 'temporary password' /var/log/mysqld.log | rev | cut -d':' -f 1 | rev | xargs`
mysql -uroot -p$pwd --connect-expired-password -e "Alter user 'root'@'localhost' IDENTIFIED WITH mysql_native_password BY 'Njs862459063!'"
sudo service mysqld restart
mysql -uroot -pNjs862459063! -e "CREATE DATABASE IF NOT EXISTS database4"

sudo yum install ruby -y
sudo yum install wget
cd /home/ec2-user
wget https://aws-codedeploy-us-west-2.s3.us-west-2.amazonaws.com/latest/install
chmod +x ./install
sudo ./install auto
sudo service codedeploy-agent status
sudo service codedeploy-agent start