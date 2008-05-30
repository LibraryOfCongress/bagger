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
        self.hibernate_writer_props = """hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
                                         hibernate.connection.driver_class=org.postgresql.Driver
                                         hibernate.connection.url=jdbc:postgresql://%s:%s/%s
                                         hibernate.connection.username=%s
                                         hibernate.connection.password=%s
                                      """ % (config['PGHOST'], config['PGPORT'], self.db_name, 
                                             self.roles['transfer_user'], self.passwds['transfer_user'])
        self.hibernate_conf = "%s/%s-%s/conf/data_writer.packagemodeler.hibernate.properties" % (
            self.install_dir, self.project_name, config['VERSION']
        )
        self.hibernate_fixture_props = """hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
                                         hibernate.connection.driver_class=org.postgresql.Driver
                                         hibernate.connection.url=jdbc:postgresql://%s:%s/%s
                                         hibernate.connection.username=%s
                                         hibernate.connection.password=%s
                                      """ % (config['PGHOST'], config['PGPORT'], self.db_name, 
                                             self.roles['transfer_user'], self.passwds['transfer_user'])
        self.hibernate_fixture_conf = "%s/%s-%s/conf/fixture_writer.packagemodeler.hibernate.properties" % (
            self.install_dir, self.project_name, config['VERSION']
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
