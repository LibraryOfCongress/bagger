#!/usr/bin/env python
""" Deploys a build of the NDNP Transfer project to QA 
"""

from transfer.core import workflow, broker, package as core_package
from transfer.projects.ndnp import package as ndnp_package

def main(config):
    jbpm = workflow.Jbpm(config)
    core_modeler = core_package.PackageModeler(config)
    ndnp_modeler = ndnp_package.PackageModeler(config)
    request_broker = broker.RequestBroker(config)

    #Set to true to drop dbs and roles
    if True:
        print jbpm.drop()
        print core_modeler.drop()
        print request_broker.drop()

    #Set to true to create dbs, roles, load with fixtures
    if True:
        print jbpm.create_database()
        print core_modeler.create_database()
        print request_broker.create_database()

        print jbpm.create_roles()
        print core_modeler.create_roles()
        print request_broker.create_roles()

        print jbpm.create_tables()
        print core_modeler.create_tables()
        print ndnp_modeler.create_tables()
        print request_broker.create_tables()

        print jbpm.grant_permissions()
        print core_modeler.grant_permissions()
        print ndnp_modeler.grant_permissions()
        print request_broker.grant_permissions()

    print core_modeler.deploy_drivers()
    print ndnp_modeler.deploy_drivers()

    print jbpm.create_fixtures(env="qa", project="ndnp")
    print core_modeler.create_fixtures(env="qa")
    print ndnp_modeler.create_fixtures(env="qa")


if __name__ == '__main__':
    config = {
        'DEBUG': True, # If True, will print out actions rather than take them (e.g., will not hit database)
        'PSQL': '/usr/bin/psql', # Tell me where to find psql (default = '/usr/bin/psql')
        'PGHOST': 'localhost', # This is the host that the PostgreSQL database lives on (default = localhost)
        'PGPORT': '5432', # This is the port that PostgreSQL listens on (default = 5432)
        'PGUSER': 'postgres', # This is a username on PostgreSQL with SUPERUSER privlidges (default = postgres)
        'PGPASSWORD': '', # This is the password for the user specified above (default = '')
        'DB_PREFIX': 'qa', # This will prepend a custom prefix to the database name that will get created.  An _ will be appended. (default = '')
        'ROLE_PREFIX': 'qa', # This will prepend a custom prefix to the roles that will get created.  An _ will be appended. (default = '')
        'TRANSFER_INSTALL_DIR': '', # Set the directory that the CLI tools will be unzipped to (default = '.')
        'VERSION': '1.4', # This is the version of the release being deployed
        'SQL_FILES_LOCATION': '/home/mjg/workspace/transport-perl/db', #Set the location of the sql files (default = '')
        'TRANSFER_PASSWD': '', # Set a password for the package modeler user role (default = 'transfer_user')
        'JBPM_PASSWD': '', # Set a password for the jbpm role (default = 'jbpm_user')
        'REQUEST_BROKER_PASSWD': '', # Set a password for the service_request_broker role (default = 'service_request_broker_user')            
    }
    main(config)
