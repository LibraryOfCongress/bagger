import os
from transfer.decorators import project_name
from transfer.database import AbstractDB
from transfer import utils

class Jbpm(AbstractDB):
    @project_name("jbpm")
    def __init__(self, config):
        AbstractDB.__init__(self, config)
        self.original_db_name = "jbpm32"
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {
            'owner': "jbpm_role",
            'user': "jbpm_user",
        }
        self.passwds = {
            'jbpm_user': config['JBPM_PASSWD'] if config['JBPM_PASSWD'] else "jbpm_user",
        }
        self.workflow_name = "workflow-processes-core"
        self.driver_package = "files/%s-%s-bin.zip" % (self.workflow_name, self.version)
        self.driver = "%s/%s-%s/bin/processdeployer" % (self.install_dir, self.workflow_name, self.version)
        self.datasources_props = "%s/%s-%s/conf/datasources.properties" % (
            self.install_dir, self.workflow_name, self.version
        )

    def deploy_process_def(self, driver):
        """ deploys process definition """
        return None
    