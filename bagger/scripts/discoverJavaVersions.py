#!/usr/bin/env python
from os import getenv, path
from subprocess import Popen, PIPE
from re import search
from argparse import ArgumentParser

parser = ArgumentParser(description="A script to discover what version of java is installed in your path and set in JAVA_HOME environment variable", prog="discoverJavaVersions")
parser.add_argument("-v", "--verbose", action="store_true", dest="verbose", help="print out details about versions found")

options = parser.parse_args()

indent = "    "

def search_for_java_version(string_input):
  match = search("java version \"[0-9\._]*\"", string_input)
  if match is not None:
    return match.group(0).split('"')[1]
  else:
    return "Not found"

def parse_sub_version(string_input):
  if string_input is "Not found":
    return -1
  return int(string_input.split(".")[1])

def find_home_of_java_path():
  process = Popen(["which", "java"], stdout=PIPE, stderr=PIPE)
  out,err=process.communicate()
  p=path.realpath(out.rstrip())
  return p[:-9]

java_home = getenv("JAVA_HOME")

process = Popen([java_home + "/bin/java", "-version"], stdout=PIPE, stderr=PIPE)
out,err=process.communicate()
java_home_version = search_for_java_version(err) 

process = Popen(["java", "-version"], stdout=PIPE, stderr=PIPE)
out,err=process.communicate()
java_path_version = search_for_java_version(err)

#print out information
if options.verbose:
  print indent, "============================================"
  print indent, "JAVA_HOME is set to version", java_home_version
  print indent, "Java on your path is set to version", java_path_version
  print indent, "============================================\n"

java_home_sub_version = parse_sub_version(java_home_version)
java_path_sub_version = parse_sub_version(java_path_version)
if java_home_sub_version > 6:
  print indent, "Your JAVA_HOME environment variable points to a version of java that is 7 or greater, and therefore does not need to be changed\n"
elif java_home_sub_version < 7 and java_path_sub_version > 6:
  command = "    export JAVA_HOME='" + find_home_of_java_path() + "'"
  print indent, "Your JAVA_HOME is set to the wrong installed version of Java on your system!\n"
  print indent, "To point it to the correct version run:\n", command
  print "\n", indent, "To make this permanent add the above line to your ~/.profile or ~/.bashrc file\n"
else:
  print indent, "Both JAVA_HOME and Java on your path are too old to run Bagger. Please upgrade to at least Java version 1.7\n"
