from transfer import utils
from transfer.core.webapp import WebApp as CoreWebApp

class WebApp(CoreWebApp):
    def __init__(self, config):
        CoreWebApp.__init__(self, config)
        self.catalina_home = ""

    def deploy(self):
        pass    