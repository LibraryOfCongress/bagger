import os
from transfer import utils
from transfer.core.package import PackageModeler as PackageModelerCore

class PackageModeler(PackageModelerCore):
    
    def __init__(self, config):
        PackageModelerCore.__init__(self, config)
        self.perms_sql_file = config['PM_NDNP_SQL_FILES']['perms']
        self.deploy_sql_file = config['PM_NDNP_SQL_FILES']['deploy']

    def create_database(self):
        """ create package modeler database """
        return None

    def populate_roles(self):
        """ populates roles """
        return None

    def grant_permissions(self):
        """ grant database permissions """
        return PackageModelerCore.grant_permissions(self)

    def deploy_database(self):
        """ deploy database """
        return PackageModelerCore.deploy_database(self)
