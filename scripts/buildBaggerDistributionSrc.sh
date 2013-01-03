#!/bin/bash

# Check out Bagger 2.1.3 from Transfer RDC repositiory
#svn export https://rdc.lctl.gov/svn/bagger/tags/2.1.3 bagger-2.1.3_src

# Check out tag 2.1.3 from Bagger Git repository

# Zip up the checkout folder and not include the htdocs and scripts folders
zip -r bagger-2.1.3_src.zip bagger-2.1.3_src -x "bagger-2.1.3_src/htdocs/*" "bagger-2.1.3_src/scripts/*"
