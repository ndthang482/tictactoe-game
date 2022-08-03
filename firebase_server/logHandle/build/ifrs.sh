#!bin/bash

export SERVER_HOME=/home/firebase_server/logHandle/build

games=( "thirteen_poker" "shake" "shan_plus" "shows" "slot" "tien_len" "tom_cua_ca" "goldenshan" "baccarat" "blackjack")


CONFIG="${SERVER_HOME}/test.cfg"

source $CONFIG

source "${SERVER_HOME}/handle.sh"

function set_config(){
    sudo sed -i "s/^\($1\s*=\s*\).*\$/\1$2/" $CONFIG
}



for i in "${games[@]}"
do
	doLog $i	
done

