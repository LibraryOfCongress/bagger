#!/usr/bin/env python
""" Deploys a build of the NDNP Transfer project to QA
"""

from transfer.core import workflow, package as core_package
from transfer.projects.ndnp import package as ndnp_package
import settings

def main():
    core_modeler = core_package.PackageModeler(settings)
    core_modeler.create_database(create_sql)
    core_modeler.populate_roles(roles_sql)
    core_modeler.grant_permissions(perms_sql)
    core_modeler.deploy_database()

    ndnp_modeler = ndnp_package.PackageModeler(settings)
    #ndnp_modeler.create_database()
    #ndnp_modeler.populate_roles()
    ndnp_modeler.grant_permissions(ndnp_perms_sql)
    ndnp_modeler.deploy_database()

    jbpm = workflow.Jbpm(settings)
    jbpm.create_database(jbpm_create_sql)
    jbpm.populate_roles(jbpm_roles_sql)
    jbpm.grant_permissions(jbpm_perms_sql)
    jbpm.deploy_database()

if __name__ == '__main__':
    main()
# This is the version of the release being deployed
VERSION = '1.2'

# Tell me where to find Java
# (default = '/usr/jdk/latest')
JAVA_HOME = '/usr/jdk/latest'

# Tell me where to find Tomcat
# (default = '/opt/coolstack/tomcat')
CATALINA_HOME = '/opt/coolstack/tomcat'

# Tell me where to find psql
# (default = '/usr/bin/psql')
PSQL = '/usr/bin/psql'

# This is the host that the PostgreSQL database lives on
# (default = localhost)
PGHOST = 'localhost'

# This is the port that the PostgreSQL server listens on 
# (default = 5432)
PGPORT = '5432'

# This is the name of a user on PostgreSQL server with SUPERUSER privlidges
# (default = postgres)
PGUSER = 'postgres'

# This is the password for the user specified above
# (default = '')
PGPASSWORD = ''

# This will prepend a custom prefix to the database name that will get created
# (default = '')
DB_PREFIX = ''

# This will prepend a custom prefix to the roles that will get created
# (default = '')
ROLE_PREFIX = ''

# Set a password for the fixture writer role
# (default = '')
TRANSFER_FIXTURE_WRITER_PASSWD = ''

# Set a password for the reader role
# (default = '')
TRANSFER_READER_PASSWD = ''

# Set a password for the writer role
# (default = '')
TRANSFER_WRITER_PASSWD = ''

# Set a password for the jbpm role
# (default = '')
JBPM_PASSWD = ''

# Set the directory that the CLI tools will be unzipped to
# (default = '')
TRANSFER_INSTALL_DIR = ''

