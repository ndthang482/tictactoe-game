#!/bin/bash

DOCKER=$(which docker)

#find server container

CONTAINERID=`$DOCKER ps -aqf "label=firebase"`

$DOCKER exec  $CONTAINERID ./scripts/start.sh
