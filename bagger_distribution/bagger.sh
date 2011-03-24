#!/bin/bash

function error_msg(){
 echo 
 echo
 echo "PLEASE CHECK IF JAVA 1.6 IS INSTALLED OR IT IS IN THE SYSTEM PATH"
 echo
 echo "TO SET JAVA FOR THE BAGGER APPLICATION PLEASE LOOK AT SECTION '5' IN THE INCLUDED README.TXT FILE"
 echo
 echo "Press any key to continue . . ."
 read -p "$*"
 exit 1
}

java.exe -jar bagger-2.1.1.jar -Xms512m -classpath bagger-2.1.1.jar 2>/dev/null || error_msg