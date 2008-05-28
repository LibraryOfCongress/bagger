from transfer.decorators import project_name
from transfer.database import TransferDB

class PackageModeler(TransferDB):
    @project_name("packagemodeler-core")   
    def __init__(self, config):
        TransferDB.__init__(self, config)        
        self.original_db_name = "package_modeler"
        self.db_name = self.db_prefix + self.original_db_name
        self.roles = {
            'reader': 'package_modeler_reader_role',
            'writer': 'package_modeler_data_writer_role',
            'transfer_reader': 'transfer_reader_user',
            'transfer_writer': 'transfer_data_writer_user',
        }
        self.passwds = {
            'transfer_reader': config['TRANSFER_READER_PASSWD'] if config['TRANSFER_READER_PASSWD'] else "transfer_reader_user",
            'transfer_writer': config['TRANSFER_WRITER_PASSWD'] if config['TRANSFER_WRITER_PASSWD'] else "transfer_data_writer_user",
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
