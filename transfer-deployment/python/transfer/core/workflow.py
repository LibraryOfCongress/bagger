import os
from transfer import utils
from transfer.core.package import PackageModeler as PackageModelerCore

class Jbpm(PackageModelerCore):
    
    def __init__(self, config):
        PackageModelerCore.__init__(self, config)
        self.db_name = "jbpm32"
        self.roles = {
            'owner': "jbpm_role",
            'user': "jbpm_user",
        }
        self.passwds = {
            'jbspm': config['JBPM_PASSWD'],
        }
        self.create_sql_file = config['JBPM_SQL_FILES']['create']
        self.roles_sql_file = config['JBPM_SQL_FILES']['roles']
        self.perms_sql_file = config['JBPM_SQL_FILES']['perms']
        self.deploy_sql_file = config['JBPM_SQL_FILES']['deploy']

    def create_database(self):
        """ create jbpm database """
        return PackageModelerCore.create_database(self)

    def populate_roles(self):
        """ create database roles """
        return PackageModelerCore.populate_roles(self)

    def grant_permissions(self):
        """ grant JBPM permissions """
        return PackageModelerCore.grant_permissions(self)

    def deploy_database(self):
        """ deploy JBPM database """
        return PackageModelerCore.deploy_database(self)
