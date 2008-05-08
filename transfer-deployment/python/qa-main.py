#!/usr/bin/env python
""" Deploys a build of the NDNP Transfer project to QA
"""

from transfer.core import workflow, package as core_package
from transfer.projects.ndnp import package as ndnp_package
import settings

def main():
    core_modeler = core_package.PackageModeler(settings)
    core_modeler.create_database()
    core_modeler.populate_roles()
    core_modeler.grant_permissions()
    core_modeler.deploy_database()

    ndnp_modeler = ndnp_package.PackageModeler(settings)
    ndnp_modeler.create_database()
    ndnp_modeler.populate_roles()
    ndnp_modeler.grant_permissions()
    ndnp_modeler.deploy_database()

    jbpm = workflow.Jbpm(settings)
    jbpm.create_database()
    jbpm.populate_roles()
    jbpm.grant_permissions()
    jbpm.deploy_database()

if __name__ == '__main__':
    main()
