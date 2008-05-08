import os
from transfer import utils

class PackageModeler():
    def __init__(self, settings):
        db_prefix = settings.DB_PREFIX + "_" if settings.DB_PREFIX else ""
        role_prefix = settings.ROLE_PREFIX + "_" if settings.ROLE_PREFIX else ""
        self.transfer_reader_passwd = settings.TRANSFER_READER_PASSWD
        self.transfer_writer_passwd = settings.TRANSFER_WRITER_PASSWD
        self.project_name = "packagemodeler-core"
        self.sql_file = "files/inventory-core.sql"
        self.db_name = db_prefix + "package_modeler"
        self.reader_role = role_prefix + "package_modeler_reader_role"
        self.writer_role = role_prefix + "package_modeler_data_writer_role"
        self.transfer_reader = role_prefix + "transfer_reader_role"
        self.transfer_writer = role_prefix + "transfer_data_writer_role"
        self.owner_privs = "NOSUPERUSER NOINHERIT NOCREATEDB NOCREATEROLE"
        self.user_privs = "NOSUPERUSER INHERIT NOCREATEDB NOCREATEROLE LOGIN"
        self.psql = settings.PSQL
        os.environ['PGUSER'] = settings.PGUSER
        os.environ['PGHOST'] = settings.PGHOST
        os.environ['PGPORT'] = settings.PGPORT
        os.environ['PGPASSWORD'] = settings.PGPASSWORD

    def create_database(self):
        """ create package modeler database """
        os.environ['PGDATABASE'] = "postgres"
        databases = utils.list_databases(self.psql)
        if databases.find(self.db_name) != -1:
            return "ERROR:  *** The %s database exists!" % (self.db_name)
        sql = "CREATE DATABASE %s ENCODING = 'UTF8';" % (self.db_name)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Creating Databases - %s" % (result)

    def populate_roles(self):
        """ populate database roles """
        os.environ['PGDATABASE'] = "postgres"

        owners_sql = """CREATE ROLE %s %s;
                        CREATE ROLE %s %s;
                     """ % (self.reader_role, self.owner_privs, 
                            self.writer_role, self.owner_privs)
        users_sql = """CREATE ROLE %s WITH PASSWORD '%s' %s;
                       CREATE ROLE %s WITH PASSWORD '%s' %s;
                    """ % (self.transfer_reader, self.transfer_reader_passwd, self.user_privs, 
                           self.transfer_writer, self.transfer_writer_passwd, self.user_privs)
        grants_sql = """GRANT %s TO %s;
                        GRANT %s TO %s;
                     """ % (self.reader_role, self.transfer_reader, 
                            self.writer_role, self.transfer_writer)
        sql = owners_sql + users_sql + grants_sql
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Creating roles- %s" % (result)

    def grant_permissions(self):
        """ grant database permissions """
        os.environ['PGDATABASE'] = self.db_name
        # TODO: specify a file w/ same info or checkout from SVN
        # TODO: and/or look for more DRY way to handle templating?
        sql = ""
        sql += "GRANT CONNECT ON DATABASE %s TO %s;" % (self.db_name, self.writer_role)
        sql += "GRANT USAGE ON SCHEMA core TO %s;" % (self.writer_role)
        sql += "GRANT USAGE ON SCHEMA agent TO %s;" % (self.writer_role)
        sql += "GRANT SELECT ON TABLE agent.agent TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT ON TABLE agent.agent_role TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT ON TABLE agent.role TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT ON TABLE core.repository TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.canonicalfile TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.canonicalfile_fixity TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_file_examination_group TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_file_location TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.event_package TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.external_filelocation TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination_fixity TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileexamination_group TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileinstance TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.fileinstance_fixity TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.filelocation TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.package TO GROUP %s;" % (self.writer_role)
        sql += "GRANT SELECT, UPDATE, INSERT, DELETE ON TABLE core.storagesystem_filelocation TO GROUP %s;" % (self.writer_role)
        sql += "GRANT CONNECT ON DATABASE %s TO %s;" % (self.db_name, self.reader_role)
        sql += "GRANT USAGE ON SCHEMA core TO %s;" % (self.reader_role)
        sql += "GRANT USAGE ON SCHEMA agent TO %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE agent.agent TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE agent.agent_role TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE agent.role TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.repository TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.canonicalfile TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.canonicalfile_fixity TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.event_file_examination_group TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.event_file_location TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.event_package TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.external_filelocation TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.fileexamination TO public;" 
        sql += "GRANT SELECT ON TABLE core.fileexamination_fixity TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.fileexamination_group TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.fileinstance TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.fileinstance_fixity TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.filelocation TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.package TO GROUP %s;" % (self.reader_role)
        sql += "GRANT SELECT ON TABLE core.storagesystem_filelocation TO GROUP %s;" % (self.reader_role)
        result = utils.load_sqlstr(self.psql, sql)
        return "INFO:  Granting privileges to core - %s" % (result)

    def deploy_database(self):
        """ deploy database """
        os.environ['PGDATABASE'] = self.db_name
        result = utils.load_sqlfile(self.psql, self.sql_file)
        return "Creating core tables - %s" % (result)

