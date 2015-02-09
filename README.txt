Bagger README.txt
=================

1. Introduction
   ============

The Bagger application was created for the U.S. Library of Congress as a tool to produce a package of data files according to the BagIt specification (http://tools.ietf.org/html/draft-kunze-bagit-05).

The Bagger application is a graphical user interface to the BagIt specification. The latest Bagger release is available on GitHub (currently https://github.com/LibraryOfCongress/bagger/releases/tag/v2.2)

Bagger differs from BIL by providing graphical user interface for file and data manipulation features as well as a visual view of the bag contents, bag state and options.  In addition Bagger provides a project profile capability.  Users can create customized bag-info.txt data with project specific properties that the user defines.

These project profiles can be edited manually and shared with other users.

2. License
   =======

License and other related information are listed in the LICENSE.txt file included with Bagger.

3. Project Profile
   ===============

Bag metadata is stored in a 'bag-info.txt' file, as defined in the BagIt specification.  When using Bagger to manage bags for a project or collection,
it can be helpful to have a template of bag-info.txt fields and values that are filled out similarly for each bag in that project or collection.
Profiles let users define a collection of bag metadata fields and default field values to be used with each bag in a consistent way. 
Users can select a project profile when creating a bag, and that profile will determine the initial fields and values in the bag-info.txt file, and the profile used is identified by the "Profile Name" field. 

User can create custom project profiles using a simple JSON-based format. When the bagger application is first started the bagger folder gets created in the user's home folder and contains some default profiles.
Profile files should be named <profile name>-profile.json and stored in the bagger's home directory: <user-home-dir>/bagger.
On Windows, it is C:\"Documents and Settings"\<user>\bagger. On Unix-like operating system, it is ~/bagger.  Also when the bagger application is started it creates a few default profiles in the above bagger folder, which can be used as a guide to create custom profiles.  

Also when using a new Bagger version please remove the bagger folder created by the previous Bagger version in the user's home folder.  
This will insure that the new/updated profiles are created in the bagger folder after the new bagger version is started.

To support the use of profiles for bag-info.txt editing in the Bagger and in the various Transfer webapps, the following describes a JSON serialization of a profile:

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
* "valueList": field value or a list of field values that are stored in a drop down list, which is displayed in the Bag-Info tab form in Bagger

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
The items in the profile file (i.e. JSON file) are listed in the Bag-Info tab of Bagger.


4. Bagger Build Process
   ====================

To build the Bagger application jar file, execute the following steps from the top level folder of the distribution:

     gradle distZip


5. Running Bagger on Windows
   =========================
To start the Bagger application, double-click on the bagger.bat file in the bin folder.


6. Running Bagger in Linux/Ubuntu
   ==============================

To start the Bagger application, execute the bagger shell script in the bin folder (i.e. ./bagger).

7. Exceptions
    ==========

There are a few common causes for the bagger application to fail which are:

i)   Incorrect version of the Java Run Time Environment or if no System Path is set for Java.
     The fix is to use the correct Java Runtime Environment (i.e. 1.6.xx in Windows and OpenJDK 6 in Linux/Ubuntu)

ii)  The bagger folder in the user's home folder contains profile files using older JSON format.
     The fix is to delete the old profiles in the bagger folder and rerun the bagger application.
