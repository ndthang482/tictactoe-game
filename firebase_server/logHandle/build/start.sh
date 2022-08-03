#!/bin/bash

export SERVER_HOME
SERVER_HOME=$(pwd)

source "${SERVER_HOME}/bin/config.sh"
source "${SERVER_HOME}/bin/bin.sh"

startserver "$@"