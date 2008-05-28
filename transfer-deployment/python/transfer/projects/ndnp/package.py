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
            'reader': 'package_modeler_reader_role',
            'writer': 'package_modeler_data_writer_role',
            'transfer_reader': 'transfer_reader_user',
            'transfer_writer': 'transfer_data_writer_user',
            'transfer_fixture_writer': 'transfer_fixture_writer_user',
        }
        self.passwds = {
            'transfer_reader': config['TRANSFER_READER_PASSWD'] if config['TRANSFER_READER_PASSWD'] else "transfer_reader_user",
            'transfer_writer': config['TRANSFER_WRITER_PASSWD'] if config['TRANSFER_WRITER_PASSWD'] else "transfer_data_writer_user",
            'transfer_fixture_writer': config['TRANSFER_FIXTURE_WRITER_PASSWD'] if config['TRANSFER_FIXTURE_WRITER_PASSWD'] else "transfer_fixture_writer_user",
        }
        self.hibernate_writer_props = """hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
                                         hibernate.connection.driver_class=org.postgresql.Driver
                                         hibernate.connection.url=jdbc:postgresql://%s:%s/%s
                                         hibernate.connection.username=%s
                                         hibernate.connection.password=%s
                                      """ % (config['PGHOST'], config['PGPORT'], self.db_name, 
                                             self.roles['transfer_writer'], self.passwds['transfer_writer'])
        self.hibernate_conf = "%s/%s-%s/conf/data_writer.packagemodeler.hibernate.properties" % (
            self.install_dir, self.project_name, config['VERSION']
        )
        self.hibernate_fixture_props = """hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
                                         hibernate.connection.driver_class=org.postgresql.Driver
                                         hibernate.connection.url=jdbc:postgresql://%s:%s/%s
                                         hibernate.connection.username=%s
                                         hibernate.connection.password=%s
                                      """ % (config['PGHOST'], config['PGPORT'], self.db_name, 
                                             self.roles['transfer_fixture_writer'], self.passwds['transfer_fixture_writer'])
        self.hibernate_fixture_conf = "%s/%s-%s/conf/fixture_writer.packagemodeler.hibernate.properties" % (
            self.install_dir, self.project_name, config['VERSION']
        )

    def create_fixtures(self, env):
        """ creates database fixtures """
        # use driver here
        utils.driver('%s createawardphase -name "2005"' % self.driver, self.debug)
        utils.driver('%s createawardphase -name "2006"' % self.driver, self.debug)
        utils.driver('%s createawardphase -name "2007"' % self.driver, self.debug)
        utils.driver('%s createawardphase -name "2008"' % self.driver, self.debug)
        return "Installing %s database fixtures\n====================\n" % (self.project_name)
 
    def create_database(self):
        """ creates database """
        return None

    def create_roles(self):
        """ populates database roles """
        return None

    def drop(self):
        """ drops database """
        return None
