import os
import re
import urllib
from transfer import utils, log

class AbstractDB():
    def __init__(self, config, project_name):
        self.project_name = project_name
        self.db_prefix = config['DB_PREFIX'] + "_" if config['DB_PREFIX'] else ''
        self.role_prefix = config['ROLE_PREFIX'] + "_" if config['ROLE_PREFIX'] else ''
        self.sql_files_location = config['SQL_FILES_LOCATION'] if config['SQL_FILES_LOCATION'] else ''
        self.sql_files = {
            'create': "%s/%s-create.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'roles': "%s/%s-roles.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'tables': "%s/%s-tables.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'perms': "%s/%s-perms.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'fixtures': "%s/%s-fixtures.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
            'drop': "%s/%s-drop.sql" % (config['SQL_FILES_LOCATION'], self.project_name),
        }
        self.install_dir = config['INSTALL_DIR'] if config['INSTALL_DIR'] else "."
        self.driver_package = "files/%s-%s-bin.zip" % (self.project_name, config['VERSION'])
        self.driver = "%s/%s-%s/bin/fixturedriver" % (self.install_dir, self.project_name, config['VERSION'])
        self.version = config['VERSION'] if config['VERSION'] else ''
        self.debug = config['DEBUG'] if config['DEBUG'] else False
        self.psql = config['PSQL'] if config['PSQL'] else "/usr/bin/psql"
        self.db_server = config['PGHOST'] if config['PGHOST'] else 'localhost'
        self.db_port = config['PGPORT'] if config['PGPORT'] else '5432'
        self.url = None
        self.logger = log.Log(self.project_name)
        os.environ['PGHOST'] = self.db_server
        os.environ['PGPORT'] = self.db_port
        os.environ['PGUSER'] = config['PGUSER'] if config['PGUSER'] else 'postgres'
        os.environ['PGPASSWORD'] = config['PGPASSWORD'] if config['PGPASSWORD'] else ""
        if not utils.check_java_home():
            self.logger.error("JAVA_HOME is not set properly: '%s'" % (os.environ['JAVA_HOME']))
            raise RuntimeError("JAVA_HOME is not set properly: '%s'" % (os.environ['JAVA_HOME']))
        if not self.connect():
            self.logger.error("Cannot connect to database server: %s@%s:%s" % (
                os.environ['PGUSER'], self.db_server, self.db_port
            ))
            raise RuntimeError("Cannot connect to database server: %s@%s:%s" % (
                os.environ['PGUSER'], self.db_server, self.db_port
            ))
            
    def create_database(self):
        """ creates database """
        os.environ['PGDATABASE'] = "postgres"
        if utils.list_databases(self.psql, self.debug).find(self.db_name) != -1:
            self.logger.error("%s database already exists!" % (self.db_name))
            raise RuntimeError("%s database already exists!" % (self.db_name))
        sql = utils.prefix_database_in_file(file(self.sql_files['create']).read(), self.original_db_name, self.db_name)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        self.logger.info("Creating %s database" % (self.project_name))
        return

    def create_roles(self):
        """ populates database roles """
        os.environ['PGDATABASE'] = "postgres"
        sql = utils.prefix_roles_in_file(file(self.sql_files['roles']).read(), self.roles, self.role_prefix)
        sql = utils.replace_passwds_in_file(sql, self.passwds)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        self.logger.info("Creating %s roles" % (self.project_name))
        return

    def create_tables(self):
        """ creates database tables """
        os.environ['PGDATABASE'] = self.db_name
        utils.load_sqlfile(self.psql, self.sql_files['tables'], self.debug)
        self.logger.info("Creating %s tables" % (self.project_name))
        return

    def grant_permissions(self):
        """ grants database permissions """
        os.environ['PGDATABASE'] = self.db_name
        sql = utils.prefix_database_in_file(file(self.sql_files['perms']).read(), self.original_db_name, self.db_name)
        sql = utils.prefix_roles_in_file(sql, self.roles, self.role_prefix)
        result = utils.load_sqlstr(self.psql, sql, self.debug)
        self.logger.info("Granting %s privileges" % (self.project_name))
        return

    def drop(self):
        """ drops database and roles """
        os.environ['PGDATABASE'] = "postgres"
        sql = utils.prefix_database_in_file(file(self.sql_files['drop']).read(), self.original_db_name, self.db_name)
       	sql = utils.prefix_roles_in_file(sql, self.roles, self.role_prefix)
       	result = utils.load_sqlstr(self.psql, sql, self.debug)
        for error in re.findall(r'^ERROR: (.+)', result, re.M):
            if re.compile(r'role ".+" does not exist').search(error):
                self.logger.warning(error)
            else:
                self.logger.error("Error loading sql: %s" % (error))
                raise RuntimeError("Error loading sql: %s" % (error))
        self.logger.info("Dropping %s database" % (self.project_name))
        return 

    def create_fixtures(self, fixtures=None):
        """ creates database fixtures """
        result = ""
        if isinstance(fixtures, tuple):
            for fixture in fixtures:
                result += utils.driver("%s %s" % (self.driver, fixture), self.debug)
        elif isinstance(fixtures, str):
            os.environ['PGDATABASE'] = self.db_name
            # probably want to wrap this in a try block
            fixtures_file = "%s/%s" % (self.sql_files_location, fixtures)
            sql = file(fixtures_file).read()
            result += utils.load_sqlstr(self.psql, sql, self.debug)
        else:
            self.logger.error("Unexpected type for fixtures arg")
            raise RuntimeError("Unexpected type for fixtures arg")
        self.logger.info("Installing %s database fixtures" % (self.project_name))
        return

    def deploy_drivers(self):
        """ deploys command-line drivers """
        if not os.path.exists(self.driver_package):
            urllib.urlretrieve(self.url, self.driver_package)
        if not os.path.isdir(self.install_dir):
            self.logger.error("INSTALL_DIR '%s' does not exist" % (self.install_dir))
            raise RuntimeError("INSTALL_DIR '%s' does not exist" % (self.install_dir))
        try:
            utils.unzip(self.driver_package, self.install_dir, self.debug)
        except IOError, e:
            self.logger.error("Could not unzip driver '%s' into '%s': %s" % (self.driver_package, self.install_dir, e))
            raise RuntimeError("Could not unzip driver '%s' into '%s': %s" % (self.driver_package, self.install_dir, e))
        if utils.chmod("0754", self.driver, self.debug).find("Operation not permitted") != -1:
            self.logger.error("Could not chmod driver '%s'" % (self.driver))
            raise RuntimeError("Could not chmod driver '%s'" % (self.driver))
        utils.localize_datasources_props(self.datasources_props, self.db_server, self.db_port, self.original_db_name, self.db_prefix, self.role_prefix, self.passwds, self.debug)
        self.logger.info("Deploying %s drivers" % (self.project_name))
        return 

    def connect(self):
        """ checks if a connection can be made to the database """
        if not os.path.isfile(self.psql):
            self.logger.error("Could not find psql at %s" % (self.psql))
            raise RuntimeError("Could not find psql at %s" % (self.psql))
        os.environ['PGDATABASE'] = "postgres"
        result = utils.load_sqlstr(self.psql, r'\q', self.debug)
        return False if 'ERROR' in result or 'refused' in result or 'not found' in result else True

    def java_home(self):
        """ checks the value of JAVA_HOME """
        return os.path.isfile("%s/bin/java" % (os.environ['JAVA_HOME']))
        
    
