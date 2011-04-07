#!/bin/bash

# Check out Bagger 2.1.1 from Transfer RDC repositiory
svn export https://rdc.lctl.gov/svn/bagger/tags/2.1.1 bagger-2.1.1_src

# Zip up the checkout folder and not include the htdocs and scripts folders
zip -r bagger-2.1.1_src.zip bagger-2.1.1_src -x "bagger-2.1.1_src/htdocs/*" "bagger-2.1.1_src/scripts/*"
