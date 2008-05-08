import os

def list_databases(command):
    return os.popen('%s -l' % (command)).read()

def load_sqlfile(command, sql_file):
    return os.popen('%s -f %s' % (command, sql_file)).read()

def load_sqlstr(command, sql):
    return os.popen("echo '%s' | %s" % (sql, command)).read()

