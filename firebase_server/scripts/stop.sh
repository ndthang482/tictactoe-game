#!/bin/bash
#export JAVA_HOME=/opt/jdk1.8.0_221/jre
# game firebase_server home directory: current dir
# import local firebase_server configuration
source conf/config.sh

# import function from start script
source bin/gameserver.sh 

# do stop
stopserver $@
