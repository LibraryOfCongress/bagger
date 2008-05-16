import os
from transfer import utils
from transfer.core.package import PackageModeler as PackageModelerCore

class Jbpm(PackageModelerCore):
    
    def __init__(self, config):
        PackageModelerCore.__init__(self, config)
        self.project_name = "JBPM"
        self.db_name = "jbpm32"
        self.roles = {
            'owner': "jbpm_role",
            'user': "jbpm_user",
        }
        self.passwds = {
            'jbpm': config['JBPM_PASSWD'],
        }
        self.create_sql_file = config['JBPM_SQL_FILES']['create']
        self.roles_sql_file = config['JBPM_SQL_FILES']['roles']
        self.tables_sql_file = config['JBPM_SQL_FILES']['tables']
        self.perms_sql_file = config['JBPM_SQL_FILES']['perms']
        self.fixtures_sql_file = config['JBPM_SQL_FILES']['fixtures']

    def create_database(self):
        """ creates database """
        return PackageModelerCore.create_database(self)

    def create_roles(self):
        """ populates database roles """
        return PackageModelerCore.create_roles(self)

    def create_tables(self):
        """ creates database tables """
        return PackageModelerCore.create_tables(self)

    def grant_permissions(self):
        """ grants database permissions """
        return PackageModelerCore.grant_permissions(self)

    def create_fixtures(self, project, env):
        """ creates database fixtures """
        return PackageModelerCore.create_fixtures(self, project, env)