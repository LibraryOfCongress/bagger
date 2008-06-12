from transfer import utils, log
from transfer.database import AbstractDB

class PackageModeler(AbstractDB):
    def __init__(self, config):
        AbstractDB.__init__(self, config, project_name="packagemodeler-core")
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
            self.install_dir, self.project_name, config['VERSION']
        )
        self.url = 'https://beryllium.rdc.lctl.gov/trac/transfer/browser/trunk/%s/release/%s-%s-bin.zip?format=raw' % (self.project_name, self.project_name, self.version)
        #self.logger = log.Log(self.project_name)

    def deploy_process_def(self, driver):
        """ deploys process definition """
        return None
