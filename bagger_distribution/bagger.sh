#!/bin/bash

function error_msg(){
 echo 
 echo
 echo "PLEASE CHECK IF JAVA 1.6 IS INSTALLED OR IT IS IN THE SYSTEM PATH"
 echo
 echo "TO SET JAVA FOR THE BAGGER APPLICATION PLEASE LOOK AT SECTION '5' IN THE INCLUDED README.TXT FILE"
 echo
 read -p "Press any key to continue . . ." -n1 -s
 exit 1
}

java -jar bagger-2.1.2.jar -Xms512m -classpath spring-beans-2.5.1.jar;bagger-2.1.2.jar 2>/dev/null || error_msg
