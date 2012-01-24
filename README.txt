Bagger README.txt

1. Introduction
   ============

The Bagger application was created for the U.S. Library of Congress as a tool to produce a package of data files according to the BagIt specification (http://tools.ietf.org/html/draft-kunze-bagit-05).
The Bagger application is a graphical user interface to the BIL (BagIt Library, http://sourceforge.net/projects/loc-xferutils) command line driver which is an implementation of tools conforming to the BagIt specification.  

Bagger differs from BIL by providing graphical user interface for file and data manipulation features as well as a visual view of the bag contents, bag state and options.
In addition Bagger provides a project profile capability.  Users can create customized bag-info.txt data with project specific properties that the user defines.
These project profiles can be edited manually and shared with other users.

2. License
   =======

License and other releated information are listed in the LICENSE.txt and NOTICE.txt files included the bagger_distribution folder.

3. Project Profile
   ===============

Bag metadata is stored in a 'bag-info.txt' file, as defined in the BagIt specification.  When using Bagger to manage bags for a project or collection,
it can be helpful to have a template of bag-info.txt fields and values that are filled out similarly for each bag in that project or collection.
Profiles let users define a collection of bag metadata fields and default field values to be used with each bag in a consistent way. 
Users can select a project profile when creating a bag, and that profile will determine the initial fields and values in the bag-info.txt file, and the profile used is identified by the "Profile Name" field. 

User can create custom project profiles using a simple JSON-based format. When the bagger application is first started the bagger folder gets created in the user's home folder and contains some default profiles.
Profile files should be named <profile name>-profile.json and stored in the bagger's home directory: <user-home-dir>/bagger.
On Windows, it is C:\"Documents and Settings"\<user>\bagger. On unix-like operating system, it is ~/bagger.  Also when the bagger application is started it creates a few default profiles in the above bagger folder, which can be used as a guide to create custom profiles.

To support the use of profiles for bag-info.txt editing in the Bagger and in the various Transfer webapps, the following describes a  JSON serialization of a profile:

{
   "<field name>" : {
                         "fieldRequired" : <true/false, where false is default if not present>,                
                         "requiredValue" : "<some value>",
                         "defaultValue"  : "<some value>",
                         "valueList"     : ["<some value>",<list of other values...>]
                     },
   <repeat for other fields...>
}

The meanings of some field properties are explained here:

* "fieldRequired": true/false, where false is default if not present                
* "requiredValue": some value if fieldRequired is true
* "defaultValue": default value
* "valueList": some value or a list of values and is stored in a drop down list of field values in the Bag-Info tab form in Bagger

The Project Profile format is subject to change in the future releases.


Here is a sample profile (please ignore the comments (//) when creating a JSON profile, it is only for explaining the fields):

{
   //Source-organization is required and may have any value
   "Source-organization" : {
                             "fieldRequired" : true
                           },

   //Organization-address is not required and may have any value
   "Organization-address" : {},

   //Contact-name is not required and default is Justin Littman
   "Contact-name" : {
                      "defaultValue" : "Justin Littman"
                    },

   //Content-type is not required, but if a value is provided it must be selected from list
   "Content-type" : {
                      "valueList" :["audio","textual","web capture"]
                    },

   //Content-process is required, has a default value of born digital, and must be selected from list of field values in the Bag-Info tab form in Bagger
   "Content-process" : {
                         "fieldRequired" : true,
                         "defaultValue" : "born digital",
                         "valueList" : ["born digital","conversion","web capture"]
                        }
}

The file should be named <profile name>-profile.json. For example, wdl-profile.json.


4. Bagger Build Process
   ====================

To build the Bagger application Maven 2.2.1+ and Java 1.6+ are required.


i)   To build the Bagger application jar file, execute the following steps from the top level folder of the distribution:

     cd bagger-maven
     mvn clean install
     cd ../bagger_distribution
     cp bagger_package/target/bagger-package-2.1.2-SNAPSHOT.jar bagger-2.1.2.jar

The built Bagger application jar file gets copied to the bagger_distribution folder, where it could be executed from the bagger.bat (i.e. Windows) or bagger.sh (i.e. Linux/Ubuntu) scripts.
The resulting bagger-2.1.2.jar copied to the bagger_distribution folder is the bagger application.
For more information on how to configure the bagger bat/shell script please read the README.txt file in the bagger_distribution folder.

ii) Create a Signed Bagger executable jar

If the bagger application is started by Java Web Start (i.e. from a web server container) then the bagger jar created in step ii) needs to be signed as follows (i.e. using jarsigner) :

     jarsigner -keystore bagger.ks -storepass bagger-dist -keypass bagger-dist -signedjar bagger-2.1.2-signed.jar bagger-2.1.2.jar rdc

The signed bagger jar created can be placed in a web server container and executed by Java Web Start.
When exectuting the above command, the bagger.ks (keystore files) and the original bagger jar file (i.e. bagger-2.1.2.jar) need to be in the same folder.
The bagger.ks keystore file (i.e. keystore file could be named anything) or any other keystore does not exist, it can be created as follows (i.e. using keytool) : 

     keytool -genkeypair -dname "cn=Bagger, ou=DIST, o=Bagger Distribution, c=US" -alias dist -keypass bagger-dist -keystore bagger.ks -storepass bagger-dist

Note:  The above commands were executed in a Unix/Linux environment and with minor changes can be executed in a Windows environment.

5.   Maven POM.XML file location
     ===========================
  
     A Maven POM.XML file is used to build each Bagger module.
    
     The POM.XML file for each is located at the following locations:

i)   Bagger-maven module - At bagger-maven/pom.xml

ii)  Bagger-business module - At bagger-business/pom.xml

iii) Bagger-common module - At bagger-common/pom.xml

iv)  Bagger-core module - At bagger-core/pom.xml

v)   Bagger-gui module - At bagger-gui/pom.xml

vi)  Bagger-package module - At bagger-package/pom.xml


6. Running Bagger on Windows
   =========================

You need to have Java SE Runtime Environment 6 or later version installed on the Windows system. 
After unpacking the zip file, find the directory bagger_distribution. To start the Bagger application, double-click on the bagger.bat file in the bagger_distribution folder.
To create a shortcut on your desktop, select the bagger.bat file and select the right mouse button.  Select Send to->Desktop, creates the shortcut.
The Bagger application starts with a splash banner page.  


7. Running Bagger in Linux/Ubuntu
   ==============================

You need to have Java SE Runtime Environment 6, but not above 1.6.0.22 (i.e. do not use 1.6.0.23+) installed on the Linux/Ubuntu system. 
After unpacking the zip file, find the directory bagger_distribution. To start the Bagger application, execute the bagger.sh file in the bagger_distribution folder (i.e. ./bagger.sh). 
The Bagger application starts with a splash banner page.

8. Setting JAVA_HOME
   =================

The Bagger Application needs to access the Java Runtime Environment (i.e. Java Runtime Environment 6) on the user's machine.  For Linux/Ubuntu sytems the Java Runtime Environment cannot be above 1.6.0.22 (i.e. do not use 1.6.0.23+).
There exists a known rendering issue with the Java Runtime Environment version above 1.6.0.22 when used in Linux/Ubuntu systems (i.e. sun.awt.X11.XException).  
If Java Runtime 6 is not installed or it is not set in the System Path, then alternatively the JAVA_HOME environmnet variable needs to be set in the 
bagger.bat (i.e. Windows) or bagger.sh (Linux/Ubuntu) files prvovided in the bagger_distribution folder as follows:

i) WINDOWS (File Path has space)
   -----------------------------

SET JAVA_HOME="C:\Program Files\Java\jre6\bin"
%JAVA_HOME%\java.exe -jar bagger-2.1.2.jar -Xms512m -classpath bagger-2.1.2.jar

ii) WINDOWS (File Path with no spaces)
    ----------------------------------

SET JAVA_HOME=C:\jre6\bin
%JAVA_HOME%\java.exe -jar bagger-2.1.2.jar -Xms512m -classpath bagger-2.1.2.jar

iii) Linux/Ubuntu
     ------------

JAVA_HOME = /usr/java/jre/bin
$JAVA_HOME/java.exe -jar bagger-2.1.2.jar -Xms512m -classpath bagger-2.1.2.jar

Note: The above steps are just examples and could be avoided if the Java Runtime Environment 6 is set in the System Path, where the path or name of the Java Runtime Environment folder could be different.

9.  Exceptions
    ==========

There are a few common causes for the bagger application to fail which are:

i)   Incorrect version of the Java Run Time Environment or if no System Path is set for Java.
     The fix is to use the correct Java Runtime Environment (i.e. 1.6+ in Windows and 1.6.0.22 or below in Linux/Ubuntu)
ii)  The bagger folder in the user's home folder contains profile files using older JSON format.
     The fix is to delete the old profiles in the bagger folder and rerun the bagger application.
iii) In a Linux/Ubuntu system the Java Runtime Environment throws a sun.awt.X11.XException rendering exception (i.e. a known issue) when the Java Runtime Environment version is above 1.6.0.22.
     The fix is to not use the Java Runtime environment above 1.6.0.22 on a Linux/Ubuntu system (i.e. do not use 1.6.0.23+).