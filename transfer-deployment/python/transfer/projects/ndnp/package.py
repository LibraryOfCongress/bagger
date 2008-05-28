from transfer.decorators import project_name
from transfer.core.package import PackageModeler as PackageModelerCore

class PackageModeler(PackageModelerCore):
    @project_name("packagemodeler-ndnp")
    def __init__(self, config):
        PackageModelerCore.__init__(self, config)

    def create_database(self):
        """ creates database """
        return None

    def create_roles(self):
        """ populates database roles """
        return None
