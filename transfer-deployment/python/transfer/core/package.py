import os
import re
from transfer import utils

class PackageModeler():

    def __init__(self, config):
        self.db_name = "package_modeler"
        self.reader_role = "package_modeler_reader_role"
        self.writer_role = "package_modeler_data_writer_role"
        self.transfer_reader = "transfer_reader_user"
        self.transfer_writer = "transfer_data_writer_user"
        self.psql = config['PSQL']
        self.db_prefix = config['DB_PREFIX'] + "_" if config['DB_PREFIX'] else ""
        self.role_prefix = config['ROLE_PREFIX'] + "_" if config['ROLE_PREFIX'] else ""
        self.transfer_reader_passwd = config['TRANSFER_READER_PASSWD']
        self.transfer_writer_passwd = config['TRANSFER_WRITER_PASSWD']
        self.create_sql_file = config['PM_CORE_SQL_FILES']['create']
        self.roles_sql_file = config['PM_CORE_SQL_FILES']['roles']
        self.perms_sql_file = config['PM_CORE_SQL_FILES']['perms']
        self.deploy_sql_file = config['PM_CORE_SQL_FILES']['deploy']
        os.environ['PGUSER'] = config['PGUSER']
        os.environ['PGHOST'] = config['PGHOST']
        os.environ['PGPORT'] = config['PGPORT']
        os.environ['PGPASSWORD'] = config['PGPASSWORD']
        return None

    def create_database(self):
        """ create package modeler database """
        os.environ['PGDATABASE'] = "postgres"
        if utils.list_databases(self.psql).find(self.db_name) != -1:
            return "ERROR:  *** The %s database exists!" % (self.db_name)
        sql = self.__prefix_database(file(self.create_sql_file).read())
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Creating Databases - %s" % (result)

    def populate_roles(self):
        """ populate database roles """
        os.environ['PGDATABASE'] = "postgres"
        sql = self.__prefix_roles(file(self.roles_sql_file).read())
        sql = self.__prefix_passwds(sql)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Creating roles - %s" % (result)

    def grant_permissions(self):
        """ grant database permissions """
        os.environ['PGDATABASE'] = self.db_name
        sql = self.__prefix_database(file(self.perms_sql_file).read())
        sql = self.__prefix_roles(sql)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Granting privileges to core - %s" % (result)

    def deploy_database(self):
        """ deploy database """
        os.environ['PGDATABASE'] = self.db_name
        result = utils.load_sqlfile(self.psql, self.deploy_sql_file)
        return "Creating core tables - %s" % (result)

    def __prefix_database(self, file):
        pattern = r'DATABASE (%s)' % self.db_name
        replacement = r'%s\1' % self.db_prefix
        return re.sub(pattern, replacement, file)

    def __prefix_roles(self, file):
        pattern = r'(%s|%s|%s|%s)' % (self.reader_role, self.writer_role, self.transfer_reader, self.transfer_writer)
        replacement = r'%s\1' % self.role_prefix
        return re.sub(pattern, replacement, file)

    def __prefix_passwds(self, file):
        def getrepl(match):
            return getattr(self, match.group(0))
        pattern = r'(transfer_.{4}er_passwd)'
        return re.sub(pattern, getrepl, file)

