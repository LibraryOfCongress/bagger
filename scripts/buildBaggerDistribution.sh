#!/bin/bash

# Check out Bagger 2.1.3 from Transfer RDC repositiory
#svn export https://rdc.lctl.gov/svn/bagger/tags/2.1.3 bagger-2.1.3

# Check out tag 2.1.3 from Bagger Git repository


# Build Bagger jar
cd bagger-2.1.3/bagger-maven
mvn clean install

# Copy Bagger jar to bagger_distribution folder and rename it to bagger-2.1.3.jar
cd ../bagger_distribution
cp bagger-package/target/bagger-package-2.1.3-SNAPSHOT.jar bagger-2.1.3.jar

# Create a copy of the bagger_distribution folder (i.e. bagger-2.1.3)
cd ..
cp -r bagger_distribution bagger-2.1.3
zip -r bagger-2.1.3.zip bagger-2.1.3
