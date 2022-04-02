#!/bin/bash
# sudo systemctl stop tomcat
cd /tmp
java -jar demo1-0.0.1-SNAPSHOT.jar stop
sudo rm -rf /tmp/demo1-0.0.1-SNAPSHOT.jar