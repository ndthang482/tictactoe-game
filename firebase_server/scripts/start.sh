#!/bin/bash

# set environment
#export JAVA_HOME= ../jdk1.8.0_221/jre
# game firebase_server home directory: current dir

/bin/cp -Rf Update/* game/deploy/
# import local firebase_server configuration
source conf/config.sh

# import function from start script
source bin/gameserver.sh 

# do start
startserver $@ 
