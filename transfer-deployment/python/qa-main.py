#!/usr/bin/env python
""" Deploys a build of the NDNP Transfer project to QA
"""

from transfer.core import workflow, package as core_package
from transfer.projects.ndnp import package as ndnp_package

def main(config):
    core_modeler = core_package.PackageModeler(config)
    print core_modeler.create_database()
    print core_modeler.create_roles()
    print core_modeler.create_tables()
    print core_modeler.grant_permissions()

    ndnp_modeler = ndnp_package.PackageModeler(config)
    print ndnp_modeler.create_tables()
    print ndnp_modeler.grant_permissions()

    jbpm = workflow.Jbpm(config)
    print jbpm.create_database()
    print jbpm.create_roles()
    print jbpm.create_tables()
    print jbpm.grant_permissions()


if __name__ == '__main__':
    from time import time
    time = time().__str__().split('.')[0]
    config = {
        'DEBUG': False, # If True, will not hit database
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
            'tables': '/home/mjg/workspace/transport-perl/db/jbpm.sql',
            'perms': '/home/mjg/workspace/transport-perl/db/jbpm-perms.sql',
        },
        'PM_CORE_SQL_FILES': {
            'create': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-create.sql',
            'roles': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-roles.sql',
            'tables': '/home/mjg/workspace/transport-perl/db/packagemodeler-core.sql',
            'perms': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-perms.sql',
        },
        'PM_NDNP_SQL_FILES': {
            'tables': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp.sql',
            'perms': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp-perms.sql',
        },
    }
    main(config)
