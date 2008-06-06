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
        return "unzipping %s into %s\n" % (file, directory)
    else:
        return os.popen('unzip -o "%s" -d "%s"' % (file, directory)).__str__()

def chmod(mode, file, debug=False): 
    """ changes the permissions of a file to mode """
    if debug:
        return "changing mode of %s to %s\n" % (file, mode)
    else:
        return os.popen('chmod %s "%s"' % (mode, file)).__str__()
    
def mv(srcfile, destfile, debug=False):
    """ moves srcfile to destfile """
    if debug:
        return "moving %s to %s\n" % (srcfile, destfile)
    else:
        return os.popen('mv "%s" "%s"' % (srcfile, destfile)).__str__()

def strtofile(str, file, debug=False):
    """ creates a file from a string """
    if debug:
        return "making '%s' out of '%s'\n" % (file, str)
    else:
        f = open(file, 'w')
        f.write(str)
        f.close()
        return "created %s" % (file)

def driver(str, debug=False):
    """ invokes a command-line driver """
    if debug:
        return "invoking %s\n" % str
    else:
        return os.popen(str).__str__()
        
def prefix_database_in_file(file, original_db_name, db_name):
    """ prepends db_prefix to database names """
    pattern = r'DATABASE %s' % original_db_name
    replacement = r'DATABASE %s' % db_name
    return re.sub(pattern, replacement, file)

def prefix_roles_in_file(file, roles, role_prefix):
    """ prepends role_prefix to role names """
    pattern = r' (%s)' % "|".join(roles.values())
    replacement = r' %s\1' % role_prefix
    return re.sub(pattern, replacement, file)

def replace_passwds_in_file(file, passwds):
    """ replaces passwords in sql dumps with values from config """
    def getrepl(match):
        return passwds.get(match.group(1))
    pattern = r'(\w+)_passwd'
    return re.sub(pattern, getrepl, file)

def localize_datasources_props(file, db_server, db_port, db_name, db_prefix, role_prefix, passwds, debug=False):
    """ search and replace db names, role names, and passwds in datasources.properties """
    # kludgy way to get, e.g.,  "packagemodeler" from "package_modeler32"
    if db_name.__len__():
        key_name = re.compile(r'[\d_]').sub('', db_name)  
    else:
        # set key_name and db_name to "match anything" regex 
        # for props files with multiple databases
        key_name = db_name = ".+"
    f = open(file, 'r')
    contents = f.read()
    f.close()
    # strip carriage returns
    contents = re.compile(r'\r').sub('', contents)
    contents = re.sub(r'(%s.connection.url=.+//)(\w+):(\d+)/(%s)' % (key_name, db_name), r'\1%s:%s/%s\4' % (db_server, db_port, db_prefix), contents)
    for username in re.findall(r'^%s.connection.username=(\w+)$' % key_name, contents, re.M):
        contents = re.sub(r'(%s.connection.username=)(%s)' % (key_name, username), r'\1%s\2' % (role_prefix), contents)
        contents = re.sub(r'(%s.connection.password=)%s' % (key_name, username), r'\1%s' % (passwds[username]), contents)
    if debug:
        return "localized datasources.properties file: %s" % (contents)
    else:
        f = open(file, 'w')
        f.write(contents)
        f.close()
        return "localized datasources.properties file" 

def deploy_process_def(driver, process_def):
    """ deploys process definition """
    result = os.popen("%s deploy -file %s" % (driver, process_def)).__str__()
    return "Deploying process definition\n====================\n%s" % (result)
