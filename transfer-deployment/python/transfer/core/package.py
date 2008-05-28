from transfer import utils
from transfer.decorators import project_name
from transfer.database import AbstractDB

class PackageModeler(AbstractDB):
    @project_name("packagemodeler-core")   
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
        utils.driver('%s createrepository -id ndnp' % self.driver, self.debug)
        utils.driver('%s createperson -id ray -firstname Ray -surname Murray' % self.driver, self.debug)
        utils.driver('%s createperson -id myron -firstname Myron -surname Briggs' % self.driver, self.debug)
        utils.driver('%s createperson -id scott -firstname Scott -surname Phelps' % self.driver, self.debug)
        utils.driver('%s createperson -id brian -firstname Brian -surname Vargas' % self.driver, self.debug)
        utils.driver('%s createperson -id jjoyner-qr -firstname JoKeeta -surname Joyner' % self.driver, self.debug)
        utils.driver('%s createperson -id jjoyner-sysadmin -firstname JoKeeta -surname Joyner' % self.driver, self.debug)
        utils.driver('%s createperson -id jjoyner-ingest -firstname JoKeeta -surname Joyner' % self.driver, self.debug)
        utils.driver('%s createperson -id tami-qr -firstname Tasmin -surname Mills' % self.driver, self.debug)
        utils.driver('%s createperson -id tami-sysadmin -firstname Tasmin -surname Mills' % self.driver, self.debug)
        utils.driver('%s createperson -id tami-ingest -firstname Tasmin -surname Mills' % self.driver, self.debug)
        utils.driver('%s createperson -id lfre-qr -firstname LaTonya -surname Freeman' % self.driver, self.debug)
        utils.driver('%s createperson -id lfre-sysadmin -firstname LaTonya -surname Freeman' % self.driver, self.debug)
        utils.driver('%s createperson -id lfre-ingest -firstname LaTonya -surname Freeman' % self.driver, self.debug)
        utils.driver('%s createsystem -id rdc-workflow' % self.driver, self.debug)
        utils.driver('%s createsystem -id transfer-components-core-%s' % (self.driver, self.version), self.debug)
        utils.driver('%s createrole -id repository_system' % self.driver, self.debug)
        utils.driver('%s createsystem -id ndnp-staging-repository -roles repository_system' % self.driver, self.debug)
        utils.driver('%s createrole -id storage_system' % self.driver, self.debug)
        utils.driver('%s createsystem -id rdc -roles storage_system' % self.driver, self.debug)
        utils.driver('%s createsystem -id rs15 -roles storage_system' % self.driver, self.debug)
        utils.driver('%s createsystem -id rs25 -roles storage_system' % self.driver, self.debug)
        utils.driver('%s createrole -id ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id CU-Riv -name "University of California, Riverside" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id FUG -name "University of Florida Libraries, Gainesville" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id KyU -name "University of Kentucky Libraries, Lexington" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id NN -name "New York Public Library, New York City" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id UUML -name "University of Utah, Salt Lake City" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id VIC -name "Library of Virginia, Richmond" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id DLC -name "Library of Congress" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id MnHi -name "Minnesota Historical Society" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id NbU -name "University of Nebraska, Lincoln" -roles ndnp_awardee' % self.driver, self.debug)
        utils.driver('%s createorganization -id TxDN -name "University of North Texas, Denton" -roles ndnp_awardee' % self.driver, self.debug)
        return "Installing %s database fixtures\n====================\n" % (self.project_name)
