#!/bin/bash

sudo systemctl stop tomcat

sudo rm -rf /tmp/demo1-0.0.1-SNAPSHOT

sudo chown tomcat:tomcat /tmp/demo1-0.0.1-SNAPSHOT.jar

# cleanup log files
sudo rm -rf /tmp/logs/catalina*
sudo rm -rf /tmp/logs/*.log
sudo rm -rf /tmp/logs/*.txt