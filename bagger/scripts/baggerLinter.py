#!/usr/bin/env python

"""
  Removes directories and files that match provided regexes
  
  Examples:
    * remove all entries under the .foo and .bar directories
    %(prog)s data/\.foo.* data/\.bar.*
    * remove vim temp file(s)
    %(prog)s data/.*\.swp
"""

import argparse
import fnmatch
import hashlib
import os
import re
import shutil
import sys

class MyFormatter(argparse.ArgumentDefaultsHelpFormatter, argparse.RawTextHelpFormatter):
    pass

def getHashAlgorithm(file):
    hash_algorithm = os.path.splitext(file)[0].split('-')[1]
    return hash_algorithm

def createModifiedManifest(manifest, regexes):
    hash_algorithm = getHashAlgorithm(manifest)
    original_manifest_file = open(manifest, 'r')
    modified_manifest_name = 'manifest-' + hash_algorithm + '.txt.tmp'
    modified_manifest_file = open(modified_manifest_name, 'w')
    modified = False
    for regex in regexes:
        matcher = re.compile(regex)
        for line in original_manifest_file:
            file_entry = line.split(' ')[1]
            if matcher.match(file_entry):
                print("Removing %s because it matches regex %s" % (line.rstrip(), regex))
                modified = True
            else:
                modified_manifest_file.write(line)
                
    original_manifest_file.close()
    modified_manifest_file.close()
    
    return modified_manifest_name, modified

def calculateHash(file):
    hash_algorithm = getHashAlgorithm(file)
    file_handle = open(file, 'r')
    hasher = hashlib.new(hash_algorithm)
    buf = file_handle.read()
    file_handle.close()
    hasher.update(buf)
    hash = hasher.hexdigest()
    
    return hash

def updateTagmanifest(updated_manifest_hash):
    for file in os.listdir('.'):
        if fnmatch.fnmatch(file, 'tagmanifest-*.txt'):
            hash_algorithm = getHashAlgorithm(file)
            original_tagmanifest = open(file)
            modified_tagmanifest_name = "tagmanifest-" + hash_algorithm + ".txt.tmp"
            modified_tagmanifest = open(modified_tagmanifest_name, "w")
            for line in original_tagmanifest:
                if "manifest" in line:
                    modified_tagmanifest.write(updated_manifest_hash + " " + file + os.linesep)
                    found_file_to_update = True
                else:
                    modified_tagmanifest.write(line)
                    
            original_tagmanifest.close()
            modified_tagmanifest.close()
            shutil.move(modified_tagmanifest_name, file)

def main():
    parser = argparse.ArgumentParser(description=__doc__.strip(), formatter_class=MyFormatter)
    parser.add_argument('regexes', help='Python regexes to match against in bag manifest.', nargs='+')
    parser.add_argument("-d", "--dryrun", help="Don't actually modify bag.", action="store_true")
    args = parser.parse_args()
    
    found_manifest_file = False
    for file in os.listdir('.'):
        if fnmatch.fnmatch(file, 'manifest-*.txt'):
            found_manifest_file = True
            new_manifest_file, differs = createModifiedManifest(file, args.regexes)
            if differs and not args.dryrun:
                shutil.move(new_manifest_file, file)
                hash = calculateHash(file)
                updateTagmanifest(hash)
            else:
                os.remove(new_manifest_file)
    
    if not found_manifest_file:
        print("Could not find any manifest file. Are you in the bag directory?")
    
if __name__ == "__main__":
    main()