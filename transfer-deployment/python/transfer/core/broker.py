from transfer.decorators import project_name
from transfer.database import AbstractDB

class RequestBroker(AbstractDB):
    @project_name("requestbroker")
    def __init__(self, config):
        AbstractDB.__init__(self, config)
        self.original_db_name = "service_request_broker"
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {
            'owner': "service_request_broker_role",
            'user': "service_request_broker_user",
        }
        self.passwds = {
            'request_broker': config['REQUEST_BROKER_PASSWD'] if config['REQUEST_BROKER_PASSWD'] else "service_request_broker_user",
        }
