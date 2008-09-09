import os
import re
from time import sleep

def check_os():
    try:
        platform = os.uname()[0]
    except AttributeError:
        platform = os.name
    # "Linux" for linux
    # "SunOS" for solaris
    # "nt" for windows
    return platform

def start_tomcat(cmd, debug=False):
    # platform = check_os()
    if debug:
        return "starting tomcat: %s" % (cmd)
    else:
        output = os.popen4(cmd)[1].read()
        sleep(5)
        return output

def stop_tomcat(cmd, debug=False):
    # platform = check_os()
    if debug:
        return "stopping tomcat: %s" % (cmd)
    else:
        output = os.popen4(cmd)[1].read()
        sleep(5)
        return output

def list_databases(command, debug=False):
    """ gets list of databases """
    if debug:
        return os.popen4('echo "%s"' % command)[1].read()
    else:
        return os.popen4('%s -l' % command)[1].read()

def load_sqlfile(command, sql_file, debug=False):
    """ loads sql data from a file into a database """
    if debug:
        return os.popen4('cat "%s"' % sql_file)[1].read()
    else:
        output = os.popen4('%s -f %s' % (command, sql_file))[1].read()
        m = re.compile(r'^ERROR: (.+)', re.M).search(output)
        if m:
            raise RuntimeError("Error loading '%s': %s" % (sql_file, m.groups(1)))
        else:
            return output

def load_sqlstr(command, sql, debug=False):
    """ loads sql data from a string into a database """
    if debug:
        return os.popen4('echo "%s"' % sql)[1].read()
    else:
        return os.popen4('echo "%s" | %s' % (sql, command))[1].read()
        
def unzip(file, directory, debug=False):
    """ unzips a file into a directory """
    if debug:
        return "unzipping %s into %s\n" % (file, directory)
    else:
        output = os.popen4('unzip -o "%s" -d "%s"' % (file, directory))[1].read()
        m = re.compile(r'cannot find', re.M).search(output)
        if m:
            raise RuntimeError("Problem with zipfile %s" % (file))
        else:
            return output

def chmod(mode, file, debug=False): 
    """ changes the permissions of a file to mode """
    if debug:
        return "changing mode of %s to %s\n" % (file, mode)
    else:
        return os.popen4('chmod %s "%s"' % (mode, file))[1].read()

def chown(user, group, file, recursive=False, debug=False): 
    """ changes the ownership of a file to user:group """
    if debug:
        return "changing owner of %s to %s:%s\n" % (file, user, group)
    else:
        flag = ""
        if recursive:
            flag = " -R"
        return os.popen4('chown%s %s:%s "%s"' % (flag, user, group, file))[1].read()

def ln(from_file, to_file, debug=False):
    """ creates a link to a file """
    if debug:
        return "linking %s to %s" % (from_file, to_file)
    else:
        return os.popen4("ln -s %s %s" % (from_file, to_file))[1].read()

def mkdir(path, debug=False):
    """ makes directory path """
    if debug:
        return "making %s directory" % (path)
    else:
        return os.makedirs(path).__str__()
    
def rmdir(path, debug=False):
    """ removes directory path """
    if debug:
        return "removing %s directory" % (path)
    else:
        try:
            return os.removedirs(path).__str__()
        except OSError:
            return os.popen4("rm -rf '%s'" % (path))[1].read()
    
def mv(srcfile, destfile, debug=False):
    """ moves srcfile to destfile """
    if debug:
        return "moving %s to %s\n" % (srcfile, destfile)
    else:
        return os.popen4('mv "%s" "%s"' % (srcfile, destfile))[1].read()

def cp(srcfile, destfile, debug=False):
    """ copies srcfile to destfile """
    if debug:
        return "copying %s to %s\n" % (srcfile, destfile)
    else:
        return os.popen4('cp "%s" "%s"' % (srcfile, destfile))[1].read()

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
        return os.popen4(str)[1].read()
        
def prefix_database_in_file(file, original_db_name, db_name):
    """ prepends db_prefix to database names """
    pattern = r'DATABASE(( IF EXISTS)?) %s' % original_db_name
    replacement = r'DATABASE\1 %s' % db_name
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

def setup_driver_init(init_script, driver_script, init_location, user, run_number):
    """ localize init script and move to /etc/init.d """
    source_init_script = "%s/%s" % (init_location, init_script)
    driver = "%s/%s" % (init_location, driver_script)
    f = open(source_init_script, 'r')
    contents = f.read()
    f.close()
    contents = re.compile(r'^EXEC=.+$', re.M).sub(r'EXEC=%s' % (driver), contents)
    contents = re.compile(r'^JAVA_HOME=.+$', re.M).sub(r'JAVA_HOME=%s' % (os.environ['JAVA_HOME']), contents)
    contents = re.compile(r'^USER=.+$', re.M).sub(r'USER=%s' % (user), contents)
    f = open(source_init_script, 'w')
    f.write(contents)
    f.close()
    initd_script= "/etc/init.d/%s" % (init_script)
    cp(source_init_script, initd_script)
    os_name = os.uname()[0]
    #if sun
    if os_name == 'SunOS':
        perms = 744
        group = "sys"
        run_level=3
    #if linux
    elif os_name == 'Linux':
        perms = 755
        group = "root"
        run_level=2
    #else error
    else:
        raise RuntimeError("Unknown OS %s" % (os_name))
    chmod(perms, initd_script)
    chown("root", group, initd_script)
    ln(initd_script, "/etc/rc%s.d/S%s%s" % (run_level, run_number, init_script))
    return

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

def append_props(props, file, debug=False):
    """ appends properties to a properties file """
    str = "\n"
    for prop in props.keys():
        str = "%s%s=%s\n" % (str, prop, props.get(prop))
    if not debug:    
        f = open(file, 'a')
        f.write(str)
        f.close()        
    return "appending %s to %s\n" % (str, file)


def deploy_process_def(driver, process_def):
    """ deploys process definition """
    return os.popen4("%s deploy -file %s" % (driver, process_def))[1].read()

def restart_container(command):
    """ restarts service container """
    os.popen4("%s restart &" % command)
    return
    
def check_java_home():
    """ checks JAVA_HOME to make sure references correct version """
    if not os.path.isfile("%s/bin/java" % (os.environ['JAVA_HOME'])):
        return False
    stdin, stdout = os.popen4("java -version")
    version_line = stdout.next()
    return version_line.startswith("java version \"1.6.")
    