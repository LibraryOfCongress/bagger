import os
import urllib
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
        self.datasources_conf = "%s/WEB-INF/classes/conf/datasources.properties" % (self.webapps_location)
        self.workflow_ndnp_conf = "%s/WEB-INF/classes/conf/workflow.ndnp.properties" % (self.webapps_location)
        self.workflow_ndnp_props = config['WORKFLOW_NDNP_PROPS'] if config.has_key('WORKFLOW_NDNP_PROPS') else {}
        self.ui_conf = "%s/WEB-INF/classes/conf/ui.local.properties" % (self.webapps_location)
        self.ui_props = config['UI_PROPS'] if config.has_key('UI_PROPS') else {}
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
        self.tomcat_start = config['TOMCAT_START'] if config['TOMCAT_START'] else "/usr/sbin/svcadm enable svc:/application/csk-tomcat"
        self.tomcat_stop = config['TOMCAT_STOP'] if config['TOMCAT_STOP'] else "/usr/sbin/svcadm disable svc:/application/csk-tomcat"
        self.logger = log.Log(self.project_name)
        self.url = 'https://beryllium.rdc.lctl.gov/trac/ndnptransfer/browser/trunk/transfer-ui-ndnp/releases/transfer-ui-ndnp-%s-template.war?format=raw' % (self.version)
        os.environ['CATALINA_HOME'] = self.catalina_home
        if not self.tomcat_exists():
            self.logger.error("One of the Tomcat scripts is not set properly")
            raise RuntimeError("One of the Tomcat scripts is not set properly")
        utils.stop_tomcat(self.tomcat_stop, self.debug)

    def deploy(self):
        """ deploys web application to tomcat """
        if not os.path.exists(self.warfile):
            urllib.urlretrieve(self.url, self.warfile)
        if not os.path.isdir("%s/webapps" % (self.catalina_home)):
            self.logger.error("%s/webapps does not exist" % (self.catalina_home))
            raise RuntimeError("%s/webapps does not exist" % (self.catalina_home))
        if os.path.isdir("%s/webapps/transfer" % (self.catalina_home)):
            utils.rmdir("%s/webapps/transfer" % (self.catalina_home))
        if utils.mkdir("%s/webapps/transfer" % (self.catalina_home), self.debug).find("Permission denied") != -1:
            self.logger.error("Could not create directory '%s/webapps/transfer'" % (self.catalina_home))
            raise RuntimeError("Could not create directory '%s/webapps/transfer'" % (self.catalina_home))        
        try:
            utils.unzip(self.warfile, self.webapps_location, self.debug)
        except IOError, e:
            self.logger.error("Could not unzip warfile '%s' into '%s': %s" % (self.warfile, self.webapps_location, e))
            raise RuntimeError("Could not unzip driver '%s' into '%s': %s" % (self.warfile, self.webapps_location, e))
        utils.localize_datasources_props(self.datasources_conf, self.db_server, self.db_port, self.db_name, self.db_prefix, self.role_prefix, self.passwds, self.debug)
        utils.append_props(self.workflow_ndnp_props, self.workflow_ndnp_conf)        
        utils.append_props(self.ui_props, self.ui_conf)        
        utils.start_tomcat(self.tomcat_start, self.debug)
        self.logger.info("Deploying %s webapp" % (self.project_name))
        return
    
    def tomcat_exists(self):
        """ checks to make sure tomcat scripts exist """
        return os.path.isfile(self.tomcat_start.split()[0]) and os.path.isfile(self.tomcat_stop.split()[0])
