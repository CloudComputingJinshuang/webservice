#!/bin/bash
# sudo systemctl stop tomcat
#!/bin/bash
# cd /tmp
# java -jar demo1-0.0.1-SNAPSHOT.jar &      # Send it to the background
# MyPID=$!                        # Record PID
# echo $MyPID                     # Print to terminal
# # Do stuff
# kill $MyPID                     # kill PID (can happen later in script as well)

# pkill -f 'java -jar'
# sudo rm -rf /tmp/demo1-0.0.1-SNAPSHOT.jar

sleep 120
cd /tmp
pwd
ls -al
if sudo kill "$(cat ./pid.file)"; then
  echo Killed Application
else
  echo no webapp found
fi