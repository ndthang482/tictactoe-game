#!/bin/bash

# set environment
#export JAVA_HOME=/opt/jdk1.8.0_221/jre

# game firebase_server home directory: current dir
export SERVER_HOME=/home/firebase_server
/bin/cp -Rf /home/firebase_server/Update/* /home/firebase_server/game/deploy/
# import local firebase_server configuration
source ${SERVER_HOME}/conf/config.sh

# import function from start script
source ${SERVER_HOME}/bin/gameserver.sh 

# do start
restartserver $@ 
