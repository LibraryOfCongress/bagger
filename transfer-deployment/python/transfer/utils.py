import os
import re

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

def strtofile(str, file, debug=False):
    """ creates a file from a string """
    if debug:
        return "making '%s' out of '%s'" % (file, str)
    else:
        f = open(file, 'w')
        f.write(str)
        f.close()
        return "created %s" % (file)

def prefix_database_in_file(file, original_db_name, db_name):
    """ prepends db_prefix to database names """
    pattern = r'DATABASE %s' % original_db_name
    replacement = r'DATABASE %s' % db_name
    return re.sub(pattern, replacement, file)

def prefix_roles_in_file(file, roles, role_prefix):
    """ prepends role_prefix to role names """
    pattern = r'(%s)' % "|".join(roles.values())
    replacement = r'%s\1' % role_prefix
    return re.sub(pattern, replacement, file)

def replace_passwds_in_file(file, passwds):
    """ replaces passwords in sql dumps with values from config """
    def getrepl(match):
        return passwds.get(match.group(1))
    pattern = r'(\w+)_passwd'
    return re.sub(pattern, getrepl, file)

