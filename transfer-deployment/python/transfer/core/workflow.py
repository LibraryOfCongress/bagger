import os
from transfer import utils

class Jbpm():
    def __init__(self, settings):
        db_prefix = settings.DB_PREFIX + "_" if settings.DB_PREFIX else ""
        role_prefix = settings.ROLE_PREFIX + "_" if settings.ROLE_PREFIX else ""
        self.jbpm_passwd = settings.JBPM_PASSWD
        self.sql_file = "files/jbpm.sql"
        self.db_name = db_prefix + "jbpm32"
        self.owner_role = role_prefix + "jbpm_role"
        self.user_role = role_prefix + "jbpm_user"
        self.owner_privs = "NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE"
        self.user_privs = "NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE LOGIN"
        self.psql = settings.PSQL
        os.environ['PGUSER'] = settings.PGUSER
        os.environ['PGHOST'] = settings.PGHOST
        os.environ['PGPORT'] = settings.PGPORT
        os.environ['PGPASSWORD'] = settings.PGPASSWORD

    def create_database(self):
        """ create databases """
        os.environ['PGDATABASE'] = "postgres"
        databases = utils.list_databases(self.psql)
        if databases.find(self.db_name) != -1:
            print "ERROR:  *** The %s database exists!" % (self.db_name)
            sys.exit(1)
        sql = "CREATE DATABASE %s ENCODING = 'UTF8';" % (self.db_name)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Creating JBPM database - %s" % (result)

    def populate_roles(self):
        """ create database roles """
        os.environ['PGDATABASE'] = "postgres"
        sql = """CREATE ROLE %s %s;
                 CREATE ROLE %s WITH PASSWORD '%s' %s;
                 GRANT %s TO %s;
              """ % (self.owner_role, self.owner_privs,
                     self.user_role, self.jbpm_passwd, self.user_privs,
                     self.owner_role, self.user_role)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Creating JBPM roles - %s" % (result)

    def grant_permissions(self):
        """ grant JBPM permissions """
        os.environ['PGDATABASE'] = self.db_name
        sql = ""
        sql += "GRANT CONNECT ON DATABASE %s TO %s;" % (self.db_name, self.user_role)
        sql += "GRANT ALL ON TABLE hibernate_sequence TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_action TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_bytearray TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_byteblock TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_comment TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_decisionconditions TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_delegation TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_event TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_exceptionhandler TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_id_group TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_id_membership TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_id_permissions TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_id_user TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_job TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_log TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_moduledefinition TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_moduleinstance TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_node TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_pooledactor TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_processdefinition TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_processinstance TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_runtimeaction TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_swimlane TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_swimlaneinstance TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_task TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_taskactorpool TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_taskcontroller TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_taskinstance TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_token TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_tokenvariablemap TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_transition TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_variableaccess TO GROUP %s;" % (self.owner_role)
        sql += "GRANT ALL ON TABLE jbpm_variableinstance TO GROUP %s;" % (self.owner_role)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Granting privileges to jbpm - %s" % (result)

    def deploy_database(self):
        """ deploy JBPM database """
        os.environ['PGDATABASE'] = self.db_name
        result = utils.load_sqlfile(self.psql, self.sql_file)
        return "INFO:  Creating jbpm tables - %s" % (result)


