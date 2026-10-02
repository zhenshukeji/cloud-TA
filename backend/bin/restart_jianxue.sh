git pull origin dev
mvn clean package
kill $(lsof -i:8106|awk '{print $2}' |sort|uniq|awk '{if($0!="PID") print ""$0" " }')
nohup java -Xmx512m -Xms512m -XX:MetaspaceSize=512m -XX:CMSInitiatingOccupancyFraction=70 -XX:+UseCMSCompactAtFullCollection -XX:CMSFullGCsBeforeCompaction=5 -XX:+UnlockCommercialFeatures -XX:+FlightRecorder -verbose:gc -Xloggc:./logs/gc.log -XX:ErrorFile=./logs/hs_err_pid.log  -jar target/jianxue.jar &
tail -n 500 -f nohup.out
