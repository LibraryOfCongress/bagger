# Bagger
[![License](https://img.shields.io/badge/License-Public--Domain-blue.svg)](https://github.com/LibraryOfCongress/bagger/blob/master/LICENSE.txt)

## Contract opportunities to maintain Bagger

The Library of Congress is pleased to offer a support and maintenance contract for Bagger. Thanks to the complexity of federal contracting, the SOURCES SOUGHT NOTICE (SSN) and SOW are only available through [GSA e-Buy](https://www.ebuy.gsa.gov/ebuy/) via RFQ ID: RFQ1591677. If you are registered under the IT Professional Services category, you should be able to find it. There isn’t a direct link we can share unfortunately.
 
Selected from the SOW:

> The contractor shall provide troubleshooting support, routine corrective and adaptive maintenance, feature development and documentation for the Bagger open source software. The Library of Congress Bagger GitHub repository will serve as the canonical resources for all Bagger issues, updates and documentation.
>
> The contractor shall provide support for Bagger for One (1) Base Period and Option Year 1 and Option Year 2, if exercised.”
 
We cannot answer questions about the contract or SNN directly due to federal contracting rules but urge you to send questions in via the procedure outlined in GSA e-Buy by Wednesday October 12, 2022 at 17:00 Eastern Time! All questions and responses will be posted publicly. SSN responses are due no later than Wednesday October 26, 2022 at 17:00 Eastern Time.  

## Introduction
The Bagger application was created for the U.S. Library of Congress as a tool to produce a package of data files according to the BagIt specification (http://tools.ietf.org/html/draft-kunze-bagit).

The Bagger application is a graphical user interface to the BagIt specification. The latest Bagger release is available on GitHub at https://github.com/LibraryOfCongress/bagger/releases/latest

Bagger differs from the [java Bagit Library](https://github.com/LibraryOfCongress/bagit-java) by providing graphical user interface for file and data manipulation features as well as a visual view of the bag contents, bag state and options.  In addition Bagger provides a project profile capability.  Users can create customized bag-info.txt data with project specific properties that the user defines.

These project profiles can be edited manually and shared with other users.

## Support
1. The Digital Curation Google Group (https://groups.google.com/d/forum/digital-curation) is an open discussion list that reaches many of the contributors to and users of this open-source project
2. If you have found a bug please create a new issue on [the issues page](https://github.com/LibraryOfCongress/bagger/issues/new)
4. If you would like to contribute, please submit a [pull request](https://help.github.com/articles/creating-a-pull-request/)

## Installing
1. Install Java from https://java.com
2. Download the latest release from https://github.com/LibraryOfCongress/bagger/releases/latest
3. Unzip to a location. This will be known as \<BAGGER_INSTALL_DIRECTORY> for the rest of the instructions

## Running Bagger on Windows
1. navigate to `<BAGGER_INSTALL_DIRECTORY>\bin`
2. double-click on the bagger.bat file

## Running Bagger in Mac OS X/Linux/Ubuntu
1. Navigate to `<BAGGER_INSTALL_DIRECTORY>/bin`
2. double-click the file named bagger

## License
License and other related information are listed in the LICENSE.txt file included with Bagger.

### Project Profile
Bag metadata is stored in a 'bag-info.txt' file, as defined in the BagIt specification.  When using Bagger to manage bags for a project or collection, it can be helpful to have a template of bag-info.txt fields and values that are filled out similarly for each bag in that project or collection.
Profiles let users define a collection of bag metadata fields and default field values to be used with each bag in a consistent way.
Users can select a project profile when creating a bag, and that profile will determine the initial fields and values in the bag-info.txt file, and the profile used is identified by the "Profile Name" field.

#### Creating custom project profiles
User can create custom project profiles using a simple JSON-based format. When the bagger application is first started the bagger folder gets created in the user's home folder and contains some default profiles.
Profile files should be named \<profile name>-profile.json and stored in the bagger's home directory: <user-home-dir>/bagger.

On Windows, it is `C:\Documents and Settings\<user>\bagger`. On Unix-like operating system, it is ~/bagger.  Also when the bagger application is started it creates a few default profiles in the above bagger folder, which can be used as a guide to create custom profiles.
Since [pull request #12](https://github.com/LibraryOfCongress/bagger/pull/12) you can now change where bagger looks for profiles by setting the system property `BAGGER_PROFILES_HOME`. This can be set using environment variable BAGGER_OPTS like this in bash:
``` bash
export BAGGER_OPTS="-DBAGGER_PROFILES_HOME=/tmp"
```

Also when using a new Bagger version please remove the bagger folder created by the previous Bagger version in the user's home folder.  
This will insure that the new/updated profiles are created in the bagger folder after the new bagger version is started.

#### Profile format
To support the use of profiles for bag-info.txt editing in the Bagger and in the various Transfer webapps, the following describes a JSON serialization of a profile:

``` python
{
   "<field name>" : {
                         "fieldRequired" : <true/false, where false is default if not present>,                
                         "requiredValue" : "<some value>",
                         "defaultValue"  : "<some value>",
                         "valueList"     : ["<some value>",<list of other values...>]
                     },
   <repeat for other fields...>
}
```

The meanings of some field properties are explained here:

* "fieldRequired": true/false, where false is default if not present                
* "requiredValue": some value if fieldRequired is true
* "defaultValue": default value
* "valueList": field value or a list of field values that are stored in a drop down list, which is displayed in the Bag-Info tab form in Bagger

The Project Profile format is subject to change in the future releases.

#### Ordering of fields
Since version 2.5 you can now enforce the ordering in the display of the fields. You **MUST** use the keyword `ordered`. An example:
```python
{
	"ordered": [{
		"Send-To-Name": {
			"requiredValue": "John Doe"
		}
	}, {
		"Send-To-Phone": {
			"requiredValue": "+0.000.000.0000"
		}
	}, {
		"Send-To-Email": {
			"requiredValue": "jdoe@foo.com"
		}
	}]
}
```

For a full example see [ordered-other-project-profile.json](bagger-business/src/main/resources/gov/loc/repository/bagger/profiles/ordered-other-project-profile.json)

#### Sample profile
Here is a sample profile (please ignore the comments (//) when creating a JSON profile, it is only for explaining the fields):

```
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
```

The file should be named `<profile name>-profile.json`. For example, wdl-profile.json.
The items in the profile file (i.e. JSON file) are listed in the Bag-Info tab of Bagger.


## Bagger Build Process
Bagger uses uses [Gradle](https://gradle.org/) for its build system. Check out their great [documentation](https://docs.gradle.org/current/userguide/userguide_single.html) to learn more.

To build the Bagger application, execute the following steps from the top level folder of the distribution:
``` bash
gradle distZip
```
After running successfully the bagger application will be zipped and located in bagger/build/distributions/bagger.zip. Simply unzip to install anywhere.

## Exceptions
There are a few common causes for the bagger application to fail which are:

i)   Incorrect version of the Java Run Time Environment or if no System Path is set for Java.
     The fix is to use the correct Java Runtime Environment (i.e. 1.7.xx in Windows and OpenJDK 7 in Linux/Ubuntu)

ii)  The bagger folder in the user's home folder contains profile files using older JSON format.
     The fix is to delete the old profiles in the bagger folder and rerun the bagger application.
