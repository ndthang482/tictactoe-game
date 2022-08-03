#!/bin/bash

DOCKER=$(which docker)

#find server container

CONTAINERID=`$DOCKER ps -aqf "label=firebase"`

$DOCKER exec -it $CONTAINERID ./scripts/stop.sh