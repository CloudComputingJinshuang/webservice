#!/bin/bash
# sudo systemctl stop tomcat
#!/bin/bash
cd /tmp
java -jar demo1-0.0.1-SNAPSHOT.jar &      # Send it to the background
MyPID=$!                        # Record PID
echo $MyPID                     # Print to terminal
# Do stuff
kill $MyPID                     # kill PID (can happen later in script as well)
sudo rm -rf /tmp/demo1-0.0.1-SNAPSHOT.jar