import os
from transfer import utils

class AbstractDB():
    def __init__(self, config):
        self.db_prefix = config['DB_PREFIX'] + "_" if config['DB_PREFIX'] else ''
        self.role_prefix = config['ROLE_PREFIX'] + "_" if config['ROLE_PREFIX'] else ''
        self.original_db_name = ""
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {}
        self.passwds = {}
        self.hibernate_writer_props = ""
        self.hibernate_fixture_props = ""
        self.hibernate_conf = ""
        self.hibernate_fixture_conf = ""
        self.sql_files = {
            'create': "%s/%s-create.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'roles': "%s/%s-roles.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'tables': "%s/%s-tables.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'perms': "%s/%s-perms.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'fixtures': "%s/%s-fixtures.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'drop': "%s/%s-drop.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
        }
        self.install_dir = config['TRANSFER_INSTALL_DIR'] if config['TRANSFER_INSTALL_DIR'] else "."
        self.driver_package = "files/%s-%s-bin.zip" % (self.project_name, config['VERSION'])
        self.driver = "%s/%s-%s/bin/fixturedriver" % (self.install_dir, self.project_name, config['VERSION'])
        self.version = config['VERSION'] if config['VERSION'] else ''
        self.debug = config['DEBUG'] if config['DEBUG'] else False
        self.psql = config['PSQL'] if config['PSQL'] else "/usr/bin/psql"
        os.environ['PGUSER'] = config['PGUSER'] if config['PGUSER'] else 'postgres'
        os.environ['PGHOST'] = config['PGHOST'] if config['PGHOST'] else 'localhost'
        os.environ['PGPORT'] = config['PGPORT'] if config['PGPORT'] else '5432'
        os.environ['PGPASSWORD'] = config['PGPASSWORD'] if config['PGPASSWORD'] else ""

    def create_database(self):
        """ creates database """
        os.environ['PGDATABASE'] = "postgres"
        if utils.list_databases(self.psql, self.debug).find(self.db_prefix + self.db_name) != -1:
            return "ERROR:  *** The %s database exists!" % (self.db_prefix + self.db_name)
        sql = utils.prefix_database_in_file(file(self.sql_files['create']).read(), self.original_db_name, self.db_name)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        return "Creating %s database\n=====================\n%s" % (self.project_name, result)

    def create_roles(self):
        """ populates database roles """
        os.environ['PGDATABASE'] = "postgres"
        sql = utils.prefix_roles_in_file(file(self.sql_files['roles']).read(), self.roles, self.role_prefix)
        sql = utils.replace_passwds_in_file(sql, self.passwds)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        return "Creating %s roles\n===================\n%s" % (self.project_name, result)

    def create_tables(self):
        """ creates database tables """
        os.environ['PGDATABASE'] = self.db_prefix + self.db_name
        result = utils.load_sqlfile(self.psql, self.sql_files['tables'], self.debug)
        return "Creating %s tables\n======================\n%s" % (self.project_name, result)

    def grant_permissions(self):
        """ grants database permissions """
        os.environ['PGDATABASE'] = self.db_prefix + self.db_name
        sql = utils.prefix_database_in_file(file(self.sql_files['perms']).read(), self.original_db_name, self.db_name)
        sql = utils.prefix_roles_in_file(sql, self.roles, self.role_prefix)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        return "Granting %s privileges\n====================\n%s" % (self.project_name, result)

    def drop(self):
        """ drops database and roles """
        os.environ['PGDATABASE'] = "postgres"
        sql = utils.prefix_database_in_file(file(self.sql_files['drop']).read(), self.original_db_name, self.db_name)
        sql = utils.prefix_roles_in_file(sql, self.roles, self.role_prefix)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        return "Dropping %s\n====================\n%s" % (self.project_name, result)

    def create_fixtures(self, env, project=None):
        """ creates database fixtures """
        # subclasses define this method since jBPM fixtures are installed differently than 
        # Package Modeler drivers
        pass

    def deploy_drivers(self):
        """ deploys command-line drivers """
        """ deploys command-line drivers """
        result  = utils.unzip(self.driver_package, self.install_dir, self.debug)
        result += utils.chmod("+x", self.driver, self.debug)
        result += utils.strtofile(self.hibernate_writer_props, self.hibernate_conf, self.debug)
        result += utils.strtofile(self.hibernate_fixture_props, self.hibernate_fixture_conf, self.debug)
        return "Deploying %s drivers\n====================\n%s" % (self.project_name, result)



