#!/bin/bash
CP=

	for j in /home/firebase_server/bot/lib/*.jar; do
	  CP=$CP:$j
	done

CP=$CP:/home/firebase_server/bot/conf/

ISRUNNING=`cat /home/firebase_server/bot/server.pid`
kill -9 $ISRUNNING
