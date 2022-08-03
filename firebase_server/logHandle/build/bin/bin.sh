#!/bin/bash


BINARY=${JAVA_HOME}/bin/java

LOG_DIRECTORY="logs"

LOG_ARCHIVE_DIRECTORY="logs/archived"

LOGFILE="logs/stdout.txt"

export LOGGENERATIONS=10

if [ ! -d "$LOG_DIRECTORY" ]; then
    mkdir $LOG_DIRECTORY
fi

# Find process by name
findserverinstance()
{       
    INSTANCEPID=`ps ax|grep [j]ava|grep $SERVERINSTANCENAME|tail -1|awk '{print $1}'`
    if [ -z $INSTANCEPID ]; then
            echo "0"
    else    
            echo $INSTANCEPID
    fi
}

findpid()
{
        SERVERPID=`ps ax|awk '{print $1}'|grep -x $1`

        if [ -z $SERVERPID ]; then
                echo "0"
        else
                echo $SERVERPID
        fi
}

isrunning()
{
    cd $SERVER_HOME

    if [ -e bin/server.pid ]; then
            # yes, check if process is still running
            CURRENTPID=`cat bin/server.pid`
            PIDFOUND=`findpid $CURRENTPID`
            if [ $PIDFOUND == "true" ]; then
                    echo $PIDFOUND
                    return
            fi
    fi

    INSTANCEPID=`findserverinstance`
    echo $INSTANCEPID
}

stamplog()
{
	TODAY=`date "+%Y-%m-%d %T"`
	echo "=====================================================">>$2
	echo "$TODAY - $SERVERINSTANCENAME $1">>$2
	echo "=====================================================">>$2
}

move_log() {

	if [ ! -d $LOG_ARCHIVE_DIRECTORY ]; then
		mkdir -p $LOG_ARCHIVE_DIRECTORY
	fi
	
	if [ -e ${SERVER_HOME}/logs/stdout.txt ] ; then
		rotate_archive "$LOG_ARCHIVE_DIRECTORY/stdout" $LOGGENERATIONS
		DESTINATION="$LOG_ARCHIVE_DIRECTORY/stdout"
		
                echo "Moving stdout log file to log archive: $DESTINATION"

		mv ${SERVER_HOME}/logs/stdout.txt $DESTINATION
	fi


	if [ -e ${SERVER_HOME}/logs/stderr.txt ] ; then
		rotate_archive "$LOG_ARCHIVE_DIRECTORY/stderr" $LOGGENERATIONS
		DESTINATION="$LOG_ARCHIVE_DIRECTORY/stderr"
		echo "Moving stderr log file to log archive: $DESTINATION"
		mv ${SERVER_HOME}/logs/stderr.txt $DESTINATION
	fi
}

rotate_archive()
{
	LIST=$(ls -r $1*)
	COUNT="$2"
	for i in $LIST; do
        	TMP=$(ls $i | cut -d "." -f 2)
        	if [ $TMP = $1 ]; then
                	NEW=$TMP.0
                	mv $i $NEW
        	elif [ $TMP -gt $COUNT ]; then
                	rm $i
        	else
                	BASE=$(ls $i | cut -d "." -f 1)
                	NEW=$BASE.$(($TMP+1))
                	mv $i $NEW
        	fi
        	touch $1
	done
}

waitforstartup()
{
        # Wait for server to create log file
        sleep 3
        
        echo -n "Waiting for $SERVERINSTANCENAME to start"

        COUNT=90

        until [ $COUNT == 0 ]
        do
                PIDFOUND=`findpid $1`

                if [ $PIDFOUND == "0" ];  then
                    echo " *** Error starting $SERVERINSTANCENAME"
                    echo " *** Please check logs for error messages"
                    return
                fi

                STARTED=`cat $LOGFILE|grep "Started Application"|tail -1|awk '{print $1}'`

                if [ ! -z $STARTED ]; then 
                    echo
                    return
                fi

                sleep 1
                COUNT=`expr $COUNT - 1`
                echo -n .
        done
        echo
        echo " *** $SERVERINSTANCENAME seems to take a long time to start"
        echo " *** Please check server status manually"
        exit 0
}

function startserver(){
    
    cd $SERVER_HOME
    

    ISRUNNING=`isrunning`

    if [ $ISRUNNING != "0" ]; then
        echo "Error: A server instance is already running with PID: $ISRUNNING"
        echo "Unable to start another instance"
        exit 1
    fi

    move_log 
    stamplog "Starting" "logs/stdout.txt"
    stamplog "Starting" "logs/stderr.txt"

    echo "Starting ${SERVERINSTANCENAME}"
    
    ${BINARY} -cp .:./config:./lib/* -Djava.security.egd=file:/dev/urandom -D${SERVERINSTANCENAME} $START_CLASS >> "${LOG_DIRECTORY}/stdout.txt" 2>> "${LOG_DIRECTORY}/stderr.txt" &
    
    SERVERPID=$!

    echo $SERVERPID>bin/server.pid

    waitforstartup $SERVERPID
    
    echo "$SERVERINSTANCENAME started"

    #startjstat $SERVERPID
}

function stopserver()
{
    cd $SERVER_HOME
    ISRUNNING=`isrunning`
    
    if [ $ISRUNNING == "0" ]; then
        echo "$SERVERINSTANCENAME is not running"
        exit 1
    fi

    killserver $ISRUNNING
    stamplog "Stopped" "logs/stdout.txt"

    if [ -e bin/server.pid ]; then
            rm -f bin/server.pid
    fi
    echo "$SERVERINSTANCENAME stopped"
}

killserver()
{
	echo -n "Waiting for $SERVERINSTANCENAME to stop"
	kill $1
	
	COUNT=90

  	until [ $COUNT == 0 ]
  	do
		PIDFOUND=`findpid $1`

		if [ $PIDFOUND == "0" ];  then
			echo
			return 
		fi


		sleep 1
    		COUNT=`expr $COUNT - 1`
    		echo -n .
  	done
	echo
        echo "Timeout killing $SERVERINSTANCENAME"
        echo "Trying hard kill"
	kill -9 $1
	COUNT=90

  	until [ $COUNT == 0 ]
  	do
		PIDFOUND=`findpid $1`

		if [ $PIDFOUND == "0" ];  then
			echo
			return
		fi


		sleep 1
    		COUNT=`expr $COUNT - 1`
		echo -n .
  	done
	echo
        echo " *** Error: Failed to stop $SERVERINSTANCENAME"
	exit 0
}
