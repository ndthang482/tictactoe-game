#!bin/bash

doLog()
{
    GAME="$1"
    TODAY=`date +%Y-%m-%d`

    KEY_DATE="${GAME}_DATE"
    temp_date=${!KEY_DATE}


    if [ -z "$temp_date" ]
    then
        echo "$KEY_DATE=$TODAY" | sudo tee --append $CONFIG
        DATE=$TODAY
    else 
        DATE=$temp_date
    fi
    
    KEY_INDEX="${GAME}_INDEX"
    temp_index=${!KEY_INDEX}
    if [ -z "$temp_index" ]
    then
        echo "$KEY_INDEX=0" | sudo tee --append $CONFIG
        INDEX=0
    else 
        INDEX=$temp_index
    fi
    
    KEY_START_LINE="${GAME}_START_LINE"
    temp_start_line=${!KEY_START_LINE}
    if [ -z "$temp_start_line" ];
    then
        echo "$KEY_START_LINE=1" | sudo tee --append $CONFIG
        START_LINE=1
    else
        START_LINE=$temp_start_line
    fi

    KEY_MAX_LINE="${GAME}_MAX_LINE"
    temp_max_line=${!KEY_MAX_LINE}
    if [ -z "$temp_max_line" ]
    then
        echo "$KEY_MAX_LINE=2000" | sudo tee --append $CONFIG
        MAX_LINE=2000
    else
        MAX_LINE=$temp_max_line
    fi
   

    END_LINE=$(( $START_LINE + $MAX_LINE - 1 ));


    if [[ "$DATE" > "$TODAY" ]];
    then
        echo 'up to date';
        exit 1
    fi

    fileexist=true
    FILE="$LOG_PATH$GAME-$DATE.log"


    if [ ! -f "$FILE" ]
    then
        fileexist=false
        #echo "$FILE not found."
        #exit 1
    fi

    if [ "$fileexist" = true ] ; then

        totalline="$(cat $FILE | wc -l)"

        if [[ $totalline -lt $END_LINE ]];
        then
            END_LINE=$totalline
        fi
	RESULT_PATH="../result/"
        
        if [ ! -d "$RESULT_PATH$GAME" ]
          then mkdir "$RESULT_PATH$GAME"
        fi

        sed -n "$START_LINE,$END_LINE p" "$FILE" | awk -F "#" '{a[$1" "$3" "$5]+=$6;if($8 == "true")  c[$1" "$3" "$5]++; if($6 > 0) {if($8 == "true") cw[$1" "$3" "$5]++; sw[$1" "$3" "$5]+=$6 } else {if($8 == "true") cl[$1" "$3" "$5]++;sl[$1" "$3" "$5]+=$6} } END { for (i in a) print i, a[i], c[i] == "" ? 0 : c[i], sw[i] == "" ? 0 : sw[i], sl[i] == "" ? 0 : sl[i], cw[i] == "" ? 0 : cw[i],cl[i] == "" ? 0 : cl[i]}' >> "${RESULT_PATH}/${GAME}/${DATE}-${INDEX}.log" 

        if [[ $totalline -eq $END_LINE ]] && [[ "$DATE" < "$TODAY" ]];
        then
            DATE=`date -d "$DATE +1 day" "+%Y-%m-%d"`
            END_LINE=0
            INDEX=0
        else
            INDEX=$(( $INDEX + 1 ))
        fi

    else

        if [[ "$DATE" < "$TODAY" ]];
        then
            DATE=`date -d "$DATE +1 day" "+%Y-%m-%d"`
        fi
        
        INDEX=0

        END_LINE=0
    fi


    if [ $? -eq 0 ]; then
        set_config $KEY_DATE $DATE
        set_config $KEY_START_LINE $(( $END_LINE + 1 ))
        set_config $KEY_INDEX $INDEX
    fi

}
