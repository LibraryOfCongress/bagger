import os
from transfer import utils
from transfer.core.package import PackageModeler as PackageModelerCore

class RequestBroker(PackageModelerCore):
    
    def __init__(self, config):
        PackageModelerCore.__init__(self, config)
        self.project_name = "RequestBroker"
        self.db_name = "service_request_broker"
        self.roles = {
            'owner': "service_request_broker_role",
            'user': "service_request_broker_user",
        }
        self.passwds = {
            'request_broker': config['REQUEST_BROKER_PASSWD'] if config['REQUEST_BROKER_PASSWD'] else "service_request_broker_user",
        }
        self.create_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['REQUEST_BROKER_SQL_FILES']['create']
        self.roles_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['REQUEST_BROKER_SQL_FILES']['roles']
        self.tables_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['REQUEST_BROKER_SQL_FILES']['tables']
        self.perms_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['REQUEST_BROKER_SQL_FILES']['perms']
        self.drop_sql_file = config['SQL_FILES_LOCATION'] + "/" + config['REQUEST_BROKER_SQL_FILES']['drop']

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

    def drop(self):
        """ drops database and roles """
        return PackageModelerCore.drop(self)
