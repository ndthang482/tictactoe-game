#i!/bin/bash
CP=

	for j in /home/firebase_server/bot/lib/*.jar; do
	  CP=$CP:$j
	done

CP=$CP:/home/firebase_server/bot/conf/

ISRUNNING=`cat /home/firebase_server/bot/server.pid`
kill -9 $ISRUNNING

CMDLINE=$@
/opt/jdk1.8.0_221/bin/java -classpath $CP com.cubeia.firebase.bot.LocalBotServer ${CMDLINE} >> "/home/firebase_server/bot/logs/stdout.txt" & 
SERVERPID=$!
echo $SERVERPID>/home/firebase_server/bot/server.pid
echo "Bot Server Started!"
