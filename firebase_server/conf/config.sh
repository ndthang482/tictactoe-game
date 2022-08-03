#!/bin/bash

#######################
# BASIC CONFIGURATION #
#######################

# java home, uncomment and change for non-system default
 export JAVA_HOME=/usr/

# for starting from a non-default directory
# export FIREBASE_HOME=/usr/local/firebase

# verbose flag
# export SILENT="YES"



##########################
# ADVANCED CONFIGURATION #
##########################

# max java heap memory
export MAX_MEMORY=2000M

# java GC setting
export GC_ARGUMENTS="-XX:+HeapDumpOnOutOfMemoryError -XX:+UseParallelGC"

# java debugging arguments, uncomment for remote debugging
# export DEBUG_OPTS="-Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=8000"

# host IP address, used for JMX bindimg 101.99.3.162
export HOST_IP=35.240.165.227

# host name, used for identification
# export HOST_NAME=alpha1

# uncomment to disable epoll
# export DISABLE_EPOLL="YES"

# jmx port, defaults to 8999
# export JMX_PORT=8999

# max startup/shutdown wait in seconds
# export MAX_WAIT=90
