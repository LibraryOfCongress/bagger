#!/usr/bin/env python

"""Examine a manifest and a BagIt bag, identifying any files in 
   the payload found in one but not in the other.  Ignores tag files.
   Does not consider checksums. 

   Requires Python 2.3 or newer.
   
   $Id$
"""


import sys, os, glob
from sets import Set

class Bag:
    """A representation of a bag."""
    def __init__(self, dir):
        self.dir = dir
        self.tags = {}
        self.entries = {}
        self.algs = []
    
    def open(self):
        """Opens the bag and loads the tag files and the manifests."""
        
        # Open the bagit.txt file, and load any tags from it, including
        # the required version and encoding.
        bagit_file_path = os.path.join(self.dir, "bagit.txt")
        
        if not os.path.isfile(bagit_file_path):
            raise BagError("No bagit.txt found.  Are you sure this is a bag?")

        self.load_tag_file(bagit_file_path)
        self.version = self.tags["BagIt-Version"]
        self.encoding = self.tags["Tag-File-Character-Encoding"]
        
        # Open and load the package-info.txt, if it's there.
        if os.path.isfile(os.path.join(self.dir, "package-info.txt")):
            self.load_tag_file(os.path.join(self.dir, "package-info.txt"))

        for manifest_file in self.manifest_files():
            alg = os.path.basename(manifest_file).replace("manifest-", "").replace(".txt", "")
            self.algs.append(alg)

            manifest_file = open(manifest_file, "r")
            
            try:
                for line in manifest_file:
                    line = line.strip()
                    if line == "" or line.startswith("#"):
                        continue
                        
                    entry = line.split(None, 1)
                    
                    # Format is FILENAME *CHECKSUM
                    if len(entry) != 2:
                        print "*** Invalid %s manifest entry: %s" % (alg, line)
                        continue
                    
                    hash = entry[0]
                    path = os.path.normpath(entry[1].lstrip("*"))
                    
                    if self.entries.has_key(path):
                        if self.entries[path].has_key(alg):
                            print "*** Duplicate %s manifest entry: %s" % (alg, path)

                        self.entries[path][alg] = hash
                    else:
                        self.entries[path] = {}
                        self.entries[path][alg] = hash
            finally:
                manifest_file.close()
            
    def manifest_files(self):
        for file in glob.glob(os.path.join(self.dir, "manifest-*.txt")):
            yield file
            
    def load_tag_file(self, tag_file_name):
        tag_file = open(tag_file_name, "r")
        
        try:
            for tag_name, tag_value in parse_tags(tag_file):
                self.tags[tag_name] = tag_value
        finally:
            tag_file.close()

    def compare_manifests_with_fs(self):
        files_on_fs = Set(self.payload_files())
        files_in_manifest = Set(self.entries.keys())
        
        return (list(files_in_manifest - files_on_fs),
             list(files_on_fs - files_in_manifest))
             
    def compare_fetch_with_fs(self):
        files_on_fs = Set(self.payload_files())
        files_in_fetch = Set(self.files_to_be_fetched())
        
        return (list(files_in_fetch - files_on_fs),
                list(files_on_fs - files_in_fetch))

    def payload_files(self):
        payload_dir = os.path.join(self.dir, "data")
        
        for dirpath, dirnames, filenames in os.walk(payload_dir):
            for f in filenames:
                rel_path = os.path.join(dirpath, os.path.normpath(f.replace('\\', '/')))
                rel_path = rel_path.replace(self.dir + os.path.sep, "", 1)
                yield rel_path
                
    def fetch_entries(self):
        fetch_file_path = os.path.join(self.dir, "fetch.txt")
        
        if os.path.isfile(fetch_file_path):
            fetch_file = open(fetch_file_path, "r")
            
            try:
                for line in fetch_file:
                    parts = line.strip().split(None, 3)
                    yield (parts[0], parts[1], parts[2])
            finally:
                fetch_file.close()
            
    def files_to_be_fetched(self):
        for url, size, path in self.fetch_entries():
            yield path
            
    def urls_to_be_fetched(self):
        for url, size, path in self.fetch_entries():
            yield url
        
class BagError(Exception):

    def __init__(self, message):
        self.message = message
    
    def __str__(self):
        return repr(self.message)
        
def parse_tags(file):
    for line in file:
        parts = line.strip().split(':', 1)
        tag_name = parts[0].strip()
        tag_value = parts[1].strip()
        yield (tag_name, tag_value)

if __name__ == "__main__":
    if len(sys.argv) not in (1, 2):  
        print "Syntax: %s [bagdir]" % sys.argv[0]
        print "  bagdir: The directory containing the bag.  Defaults: ."
        sys.exit(1)

    if len(sys.argv) == 1:
        sys.argv.extend(".")
    
    bag_dir = os.path.normpath(sys.argv[1])
    
    bag = Bag(bag_dir)
    bag.open()
    
    missing, extra = bag.compare_manifests_with_fs()
    # already_fetched, to_fetch = bag.compare_fetch_with_fs()
    
    if len(missing) > 0:
        print "*** Found %s missing files:" % len(missing)
        for file in missing:
            print file
            
    if len(extra) > 0:
        print "*** Found %s extra files:" % len(extra)
        for file in extra:
            print file

    #if len(already_fetched) > 0:
    #    print "*** Found %s files in fetch.txt which are already here: " % len(already_fetched)
    #    for file in already_fetched:
    #        print file