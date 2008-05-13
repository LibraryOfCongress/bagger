#!/usr/bin/env python
""" Deploys a build of the NDNP Transfer project to QA
"""

from transfer.core import workflow, package as core_package
from transfer.projects.ndnp import package as ndnp_package

config = {
    'VERSION': '1.2',  # This is the version of the release being deployed
    'JAVA_HOME': '/usr/jdk/latest', # Tell me where to find Java (default = '/usr/jdk/latest')
    'CATALINA_HOME': '/opt/coolstack/tomcat', # Tell me where to find Tomcat (default = '/opt/coolstack/tomcat')
    'PSQL': '/usr/bin/tee', # Tell me where to find psql (default = '/usr/bin/psql')
    'PGHOST': 'localhost', # This is the host that the PostgreSQL database lives on (default = localhost)
    'PGPORT': '5432', # This is the port that PostgreSQL listens on (default = 5432)
    'PGUSER': 'postgres', # This is a username on PostgreSQL with SUPERUSER privlidges (default = postgres)
    'PGPASSWORD': '', # This is the password for the user specified above (default = '')
    'DB_PREFIX': '', # This will prepend a custom prefix to the database name that will get created (default = '')
    'ROLE_PREFIX': '', # This will prepend a custom prefix to the roles that will get created (default = '')
    'TRANSFER_FIXTURE_WRITER_PASSWD': '', # Set a password for the fixture writer role (default = '')
    'TRANSFER_READER_PASSWD': '', # Set a password for the reader role (default = '')
    'TRANSFER_WRITER_PASSWD': '', # Set a password for the writer role (default = '')
    'JBPM_PASSWD': '', # Set a password for the jbpm role (default = '')
    'TRANSFER_INSTALL_DIR': '', # Set the directory that the CLI tools will be unzipped to (default = '')
    'JBPM_SQL_FILES': {
        'create': '/home/mjg/workspace/transport-perl/db/jbpm-create.sql',
        'roles': '/home/mjg/workspace/transport-perl/db/jbpm-roles.sql',
        'perms': '/home/mjg/workspace/transport-perl/db/jbpm-perms.sql',
        'deploy': '/home/mjg/workspace/transport-perl/db/jbpm.sql',
     },
    'PM_CORE_SQL_FILES': {
        'create': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-create.sql',
        'roles': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-roles.sql',
        'perms': '/home/mjg/workspace/transport-perl/db/packagemodeler-core-perms.sql',
        'deploy': '/home/mjg/workspace/transport-perl/db/packagemodeler-core.sql',
    },
    'PM_NDNP_SQL_FILES': {
        'perms': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp-perms.sql',
        'deploy': '/home/mjg/workspace/transport-perl/db/packagemodeler-ndnp.sql',
    },
}

def main():
    core_modeler = core_package.PackageModeler(config)
    print core_modeler.create_database()
    print core_modeler.populate_roles()
    print core_modeler.grant_permissions()
    print core_modeler.deploy_database()

#    ndnp_modeler = ndnp_package.PackageModeler(config)
#    print ndnp_modeler.grant_permissions()
#    print ndnp_modeler.deploy_database()

#    jbpm = workflow.Jbpm(config)
#    print jbpm.create_database()
#    print jbpm.populate_roles()
#    print jbpm.grant_permissions()
#    print jbpm.deploy_database()

if __name__ == '__main__':
    main()
