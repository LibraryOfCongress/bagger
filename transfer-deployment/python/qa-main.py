#!/usr/bin/env python
""" Deploys a build of the NDNP Transfer project to QA
"""

from transfer.core import workflow, package as core_package
from transfer.projects.ndnp import package as ndnp_package

def main(config):
    core_modeler = core_package.PackageModeler(config)
    ndnp_modeler = ndnp_package.PackageModeler(config)
    jbpm = workflow.Jbpm(config)

    print jbpm.create_database()
    print core_modeler.create_database()

    print jbpm.create_roles()
    print core_modeler.create_roles()

    print jbpm.create_tables()
    print core_modeler.create_tables()
    print ndnp_modeler.create_tables()

    print jbpm.grant_permissions()
    print core_modeler.grant_permissions()
    print ndnp_modeler.grant_permissions()

    #print jbpm.create_fixtures(project="ndnp", env="qa")
    #print core_modeler.create_fixtures(env="qa")
    #print ndnp_modeler.create_fixtures(env="qa")

    #print core_modeler.deploy_drivers()
    #print ndnp_modeler.deploy_drivers()


if __name__ == '__main__':
    from time import time
    time = time().__str__().split('.')[0]
    config = {
        'DEBUG': True, # If True, will print out actions rather than take them (e.g., will not hit database)
        'PSQL': '/usr/bin/psql', # Tell me where to find psql (default = '/usr/bin/psql')
        'PGHOST': 'localhost', # This is the host that the PostgreSQL database lives on (default = localhost)
        'PGPORT': '5433', # This is the port that PostgreSQL listens on (default = 5432)
        'PGUSER': 'postgres', # This is a username on PostgreSQL with SUPERUSER privlidges (default = postgres)
        'PGPASSWORD': '', # This is the password for the user specified above (default = '')
        'DB_PREFIX': 'mike_%s' % time, # This will prepend a custom prefix to the database name that will get created (default = '')
        'ROLE_PREFIX': 'mike_%s' % time, # This will prepend a custom prefix to the roles that will get created (default = '')
        'TRANSFER_READER_PASSWD': '', # Set a password for the reader role (default = '')
        'TRANSFER_WRITER_PASSWD': '', # Set a password for the writer role (default = '')
        'JBPM_PASSWD': '', # Set a password for the jbpm role (default = '')
        'JBPM_SQL_FILES': {
            'create': '/home/mjg/workspace/transport-perl/db/jbpm-create.sql',
            'roles': '/home/mjg/workspace/transport-perl/db/jbpm-roles.sql',
            'tables': '/home/mjg/workspace/transport-perl/db/jbpm-tables.sql',
            'perms': '/home/mjg/workspace/transport-perl/db/jbpm-perms.sql',
            'fixtures': '/home/mjg/workspace/transport-perl/db/jbpm-fixtures.sql',
        },
        'PM_CORE_SQL_FILES': {
            'create': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-create.sql',
            'roles': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-roles.sql',
            'tables': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-tables.sql',
            'perms': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-perms.sql',
            'fixtures': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-fixtures.sql',
        },
        'PM_NDNP_SQL_FILES': {
            'tables': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp-tables.sql',
            'perms': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp-perms.sql',
            'fixtures': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp-fixtures.sql',
        },
    }
    main(config)
