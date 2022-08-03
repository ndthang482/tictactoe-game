BASE_URL=/home/linhnv/firebase_server

cd $BASE_URL/logs/bot

FILE_NAME=$(ls -r | grep binh_bot | head -n 1)

cat $FILE_NAME | sed 's/\ /b/g' | sed 's/G:/\ /g' | sed 's/b-b/\ /g' | sed 's/_/\ /g' | awk '{print $2,"\t",$3,"\t",$4,"\t",$5,"\t",$6,"\t",$7}'  > $BASE_URL/report_binh.txt


cd $BASE_URL

echo "get report success"

