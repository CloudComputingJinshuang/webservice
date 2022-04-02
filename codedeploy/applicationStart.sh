#!/bin/bash

# sudo systemctl start tomcat
crontab -l | { cat; echo "@reboot (cd /tmp && java -jar demo1-0.0.1-SNAPSHOT.jar --spring.config.location=file:///tmp/jdbc.properties)"; } | crontab -
cd /tmp
java -jar demo1-0.0.1-SNAPSHOT.jar --spring.config.location=file:///tmp/jdbc.properties