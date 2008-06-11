from transfer import log
from transfer.database import AbstractDB

class RequestBroker(AbstractDB):
    def __init__(self, config):
        AbstractDB.__init__(self, config, project_name="requestbroker")
        self.original_db_name = "service_request_broker"
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {
            'owner': "service_request_broker_role",
            'user': "service_request_broker_user",
        }
        self.passwds = {
            'request_broker': config['REQUEST_BROKER_PASSWD'] if config['REQUEST_BROKER_PASSWD'] else "service_request_broker_user",
        }
        self.datasources_props = "%s/%s-%s/conf/datasources.properties" % (
            self.install_dir, self.project_name, config['VERSION']
        )
        self.logger = log.Log(self.project_name)

    def deploy_drivers(self):
        """ deploys CLI drivers """
        return None

    def create_fixtures(self):
        """ creates database fixtures """
        return None

    def deploy_process_def(self, driver):
        """ deploys process definition """
        return None
