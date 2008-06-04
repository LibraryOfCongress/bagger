from transfer import utils
from transfer.decorators import project_name
from transfer.database import AbstractDB

class PackageModeler(AbstractDB):
    @project_name("packagemodeler-ndnp")
    def __init__(self, config):
        AbstractDB.__init__(self, config)
        self.original_db_name = "package_modeler"
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {
            'transfer_role': 'package_modeler_role',
            'transfer_user': 'transfer_user',
        }
        self.passwds = {
            'transfer_user': config['TRANSFER_PASSWD'] if config['TRANSFER_PASSWD'] else "transfer_user",
        }
        self.datasources_props = "%s/%s-%s/conf/datasources.properties" % (
            self.install_dir, self.project_name, self.version
        )

    def create_database(self):
        """ creates database """
        return None

    def create_roles(self):
        """ populates database roles """
        return None

    def drop(self):
        """ drops database """
        return None
