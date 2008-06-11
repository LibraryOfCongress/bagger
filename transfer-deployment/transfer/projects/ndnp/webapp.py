import os
from transfer import utils, log
from transfer.core.webapp import WebApp as CoreWebApp

class WebApp(CoreWebApp):
    def __init__(self, config):
        CoreWebApp.__init__(self, config)
        self.project_name = "transfer-ui-ndnp"
        self.catalina_home = config['CATALINA_HOME'] if config['CATALINA_HOME'] else "/opt/coolstack/tomcat"
        self.version = config['VERSION'] if config['VERSION'] else ''
        self.debug = config['DEBUG'] if config['DEBUG'] else False        
        self.install_dir = config['INSTALL_DIR'] if config['INSTALL_DIR'] else "."        
        self.file_location = "files"
        self.webapps_location = "%s/webapps/transfer" % (self.catalina_home)
        self.warfile = "%s/transfer-ui-ndnp-%s-template.war" % (self.file_location, self.version)
        self.datasources_props = "%s/WEB-INF/classes/conf/datasources.properties" % (self.webapps_location)
        self.db_prefix = config['DB_PREFIX'] + "_" if config['DB_PREFIX'] else ''
        self.role_prefix = config['ROLE_PREFIX'] + "_" if config['ROLE_PREFIX'] else ''
        self.db_name = ""
        self.passwds = {
            'transfer_user': config['TRANSFER_PASSWD'] if config['TRANSFER_PASSWD'] else "transfer_user",
            'service_request_broker_user': config['REQUEST_BROKER_PASSWD'] if config['REQUEST_BROKER_PASSWD'] else "service_request_broker_user",
            'jbpm_user': config['JBPM_PASSWD'] if config['JBPM_PASSWD'] else "jbpm_user",
        }
        self.db_server = config['PGHOST'] if config['PGHOST'] else 'localhost'
        self.db_port = config['PGPORT'] if config['PGPORT'] else '5432'
        self.tomcat_start = config['TOMCAT_START'] if config['TOMCAT_START'] else ''
        self.tomcat_stop = config['TOMCAT_STOP'] if config['TOMCAT_STOP'] else ''
        self.logger = log.Log(self.project_name)
        os.environ['CATALINA_HOME'] = self.catalina_home
        utils.stop_tomcat(self.tomcat_stop, self.debug)

    def deploy(self):
        """ deploys web application to tomcat """
        result = ""
        # result += utils.stop_tomcat(self.tomcat_stop, self.debug)
        result += utils.mkdir("%s/webapps/transfer" % (self.catalina_home), self.debug)
        result += utils.unzip(self.warfile, self.webapps_location, self.debug)
        result += utils.localize_datasources_props(self.datasources_props, self.db_server, self.db_port, self.db_name, self.db_prefix, self.role_prefix, self.passwds, self.debug)        
        result += utils.start_tomcat(self.tomcat_start, self.debug)
        self.logger.info("Deploying %s webapp" % (self.project_name))
        return
    
