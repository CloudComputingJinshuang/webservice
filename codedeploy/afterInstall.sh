#!/bin/bash
# sudo systemctl stop tomcat
sudo rm -rf /tmp/demo1-0.0.1-SNAPSHOT
# sudo chown -R tomcat:tomcat /tmp/demo1-0.0.1-SNAPSHOT.jar

# cleanup log file
sudo rm -rf /tmp/logs/catalina*
sudo rm -rf /tmp/logs/*.log
sudo rm -rf /tmp/logs/*.txt

sudo /opt/aws/amazon-cloudwatch-agent/bin/amazon-cloudwatch-agent-ctl \
    -a fetch-config \
    -m ec2 \
    -c file:/tmp/cloudwatch-config.json \
    -s