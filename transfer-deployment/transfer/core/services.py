from transfer import utils

class TransferServices():
    def __init__(self, config):
        self.config = config
        self.project_name = "transfer-services"
        self.file_location = "files"
        self.debug = config['DEBUG'] if config['DEBUG'] else False        
        self.install_dir = config['TRANSFER_SERVICES_INSTALL_DIR'] if config['TRANSFER_SERVICES_INSTALL_DIR'] else "."        
        self.db_prefix = config['DB_PREFIX'] + "_" if config['DB_PREFIX'] else ''
        self.role_prefix = config['ROLE_PREFIX'] + "_" if config['ROLE_PREFIX'] else ''
        self.passwds = {
            'transfer_user': config['TRANSFER_PASSWD'] if config['TRANSFER_PASSWD'] else "transfer_user",
            'request_broker': config['REQUEST_BROKER_PASSWD'] if config['REQUEST_BROKER_PASSWD'] else "service_request_broker_user",
        }
        self.driver_package = "%s/transfer-services-core-%s-bin.zip" % (self.file_location, config['VERSION'])
        self.driver_location = "%s/%s-%s/bin" % (self.install_dir, self.project_name, config['VERSION'])
        self.drivers = ("componentdriver","servicecontainerdriver")
        self.component_location = "%s/%s-%s" % (self.install_dir, self.project_name, config['VERSION'])
        self.component_packages = []
        for project in config['COMPONENT_PROJECTS']:
          self.component_packages.append("%s/transfer-components-%s-%s-bin.zip" % (self.file_location, project, config['VERSION']))
        self.servicecontainer_props = """host=%s
                                         queues=%s
                                         jobtypes=%s
                                      """ % (config['HOST'] if config['HOST'] else "localhost", config['QUEUES'] if config['QUEUES'] else "jobqueue", config['JOBTYPES'] if ['JOBTYPES'] else "test")
        self.servicecontainer_conf = "%s/%s-%s/conf/servicecontainer.properties" % (self.install_dir, self.project_name, config['VERSION'])
        self.datasources_props = "%s/%s-%s/conf/datasources.properties" % (
            self.install_dir, self.project_name, config['VERSION']
        )


    def deploy_drivers(self):
        """ deploys command-line drivers """
        result  = utils.unzip(self.driver_package, self.install_dir, self.debug)
        for component_package in self.component_packages:
            result += utils.unzip(component_package, self.component_location, self.debug)        
        for driver in self.drivers:
            result += utils.chmod("+x", "%s/%s" % (self.driver_location, driver), self.debug)
        result += utils.localize_datasources_props(self.datasources_props, self.project_name, True, self.db_prefix, self.role_prefix, self.passwds, self.debug)
        result += utils.strtofile(self.servicecontainer_props, self.servicecontainer_conf, self.debug)
        return "Deploying %s drivers\n====================\n%s" % (self.project_name, result)
