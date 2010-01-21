mvn clean package -o
mvn jar:sign -Dalias=rdc -Dkeystore=bagger.ks -Dstorepass=bagger-rdc -Dkeypass=bagger-rdc -o
