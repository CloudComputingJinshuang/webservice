#!/bin/bash

# sudo systemctl start tomcat
# cd /tmp
# java -jar demo1-0.0.1-SNAPSHOT.jar --spring.config.location=file:///tmp/jdbc.properties
# crontab -l | { cat; echo "@reboot (cd /tmp && java -jar demo1-0.0.1-SNAPSHOT.jar --spring.config.location=file:///tmp/jdbc.properties)"; } | crontab -
# # cd /tmp
# # java -jar demo1-0.0.1-SNAPSHOT.jar --spring.config.location=file:///tmp/jdbc.properties



cd /tmp
pwd
ls -al
str=$"\n"
nohup java -jar /tmp/demo1-0.0.1-SNAPSHOT.jar --spring.config.location=jdbc.properties > /dev/null 2> /dev/null < /dev/null & echo $! > ./pid.file &
sstr=$(echo -e $str)
echo $sstr