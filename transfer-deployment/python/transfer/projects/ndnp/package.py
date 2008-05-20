import os
from transfer import utils
from transfer.core.package import PackageModeler as PackageModelerCore

class PackageModeler(PackageModelerCore):
    
    def __init__(self, config):
        PackageModelerCore.__init__(self, config)
        self.project_name = "packagemodeler-ndnp"
        self.tables_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['PM_NDNP_SQL_FILES']['tables']
        self.perms_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['PM_NDNP_SQL_FILES']['perms']

    def create_database(self):
        """ creates database """
        return None

    def create_roles(self):
        """ populates database roles """
        return None

    def create_tables(self):
        """ creates database tables """
        return PackageModelerCore.create_tables(self)

    def grant_permissions(self):
        """ grants database permissions """
        return PackageModelerCore.grant_permissions(self)
