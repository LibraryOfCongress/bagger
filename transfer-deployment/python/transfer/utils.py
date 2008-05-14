import os

def list_databases(command, debug=False):
    if debug:
        return os.popen('echo "%s"' % command).read()
    else:
        return os.popen('%s -l' % command).read()

def load_sqlfile(command, sql_file, debug=False):
    if debug:
        return os.popen('cat "%s"' % sql_file).read()
    else:
        return os.popen('%s -f %s' % (command, sql_file)).read()

def load_sqlstr(command, sql, debug=False):
    if debug:
        return os.popen('echo "%s"' % sql).read()
    else:
        return os.popen('echo "%s" | %s' % (sql, command)).read()

