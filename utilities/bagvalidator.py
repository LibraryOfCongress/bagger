#!/usr/bin/env python

"""Examine a manifest and a BagIt bag, identifying any files in 
   the payload found in one but not in the other.  Ignores tag files.
   Does not consider checksums. 

   Requires Python 2.3 or newer.
"""


import sys, os
from sets import Set

def normalize_relative_path(path):
    """Fixup manifest relative-paths for comparison:  
         - convert backslashes to forward slashes
         - strip off a leading "." on the path
    """
    path = path.replace('\\', '/')
    if path.startswith("./"):
        path = path[2:]
    return path

def build_manifest_entries(manifest):
    """Builds a dictionary of manifest entries, from filename -> hash.
    """
    entries = {}
    
    for entry in manifest_entries(manifest):
        hash, path = entry
        
        if entries.has_key(path):
            print "*** Duplicate manifest entry: %s" % path
        else:
            entries[path] = hash
            
    return entries

def manifest_entries(manifest):
    """Generator, returning pairs of (checksum, filename) 
       found in a manifest file."""
    # manifest lines are "CHECKSUM FILENAME"
    for line in manifest:
        line = line.strip()
        if line == '' or line.startswith("#"): 
            continue 
        # tolerate whitespace in filenames by limiting the splitting
        entry = line.strip().split(None, 1)
        yield (entry[0], normalize_relative_path(entry[1]))


def files_in_manifest(manifest):
    """Generator returning the relative paths found in a manifest file."""
    for entry in manifest_entries(manifest):
        yield entry[1]


def files_in_directory(directory_name):
    """Generator returning the relative paths found in a directory."""
    for dirpath, dirnames, filenames in os.walk(directory_name):
        # ignore "tag" files (in the base directory of the bag)
        if dirpath == ".": continue
        for f in filenames:
            yield normalize_relative_path(os.path.join(dirpath, f))


def reconcile(manifestname, directoryname):
    """Given a manifest name, returns a tuple of two lists: 
           - the files named in the manifest but not found in the current dir
           - the files found in the current dir not named in the manifest
    """
    manifest = open(manifestname, "r")
    manifest_entries = build_manifest_entries(manifest)
    manifest_contents = Set(manifest_entries.keys())
    discovered_files  = Set(files_in_directory(directoryname))

    return (list(manifest_contents - discovered_files), 
            list(discovered_files - manifest_contents))


if __name__ == "__main__":
    if len(sys.argv) not in (2, 3):  
        print "Syntax: %s manifest_name [directory_name]" % sys.argv[0]
        sys.exit(1)

    if len(sys.argv) == 2:
        sys.argv.extend(".")
    missing, extra = reconcile(sys.argv[1], 
                               sys.argv[2])

    import pprint
    
    if len(missing) > 0:
        print "*** Missing:"
        pprint.pprint(missing)

    if len(extra) > 0:
        print "*** Extra:"
        pprint.pprint(extra)
