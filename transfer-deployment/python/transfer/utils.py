import os

def list_databases(command, debug=False):
    """ gets list of databases """
    if debug:
        return os.popen('echo "%s"' % command).read()
    else:
        return os.popen('%s -l' % command).read()

def load_sqlfile(command, sql_file, debug=False):
    """ loads sql data from a file into a database """
    if debug:
        return os.popen('cat "%s"' % sql_file).read()
    else:
        return os.popen('%s -f %s' % (command, sql_file)).read()

def load_sqlstr(command, sql, debug=False):
    """ loads sql data from a string into a database """
    if debug:
        return os.popen('echo "%s"' % sql).read()
    else:
        return os.popen('echo "%s" | %s' % (sql, command)).read()

def unzip(file, directory, debug=False):
    """ unzips a file into a directory """
    if debug:
        return "unzipping %s into %s" % (file, directory)
    else:
        return os.popen('unzip "%s" -d %s' % (file, directory))

def chmod(mode, file, debug=False): 
    """ changes the permissions of a file to mode """
    if debug:
        return "changing mode of %s to %s" % (file, mode)
    else:
        return os.popen('chmod %s "%s"' % (mode, file))
    
def mv(srcfile, destfile, debug=False):
    """ moves srcfile to destfile """
    if debug:
        return "moving %s to %s" % (srcfile, destfile)
    else:
        return os.popen('mv "%s" "%s"' % (srcfile, destfile))
