#!/usr/bin/env python
""" Deploys a build of the Transfer Core components for NDIIP to production.
    Requires packagemodeler-core-*-bin.zip.
"""

from transfer.core import workflow, broker, package as core_package

config = {
    'DEBUG': True, # If True, will print out actions rather than take them (e.g., will not hit database)
    'PSQL': '/usr/postgres8.2/bin/psql', # Tell me where to find psql (default = '/usr/bin/psql')
    'PGHOST': 'localhost', # This is the host that the PostgreSQL database lives on (default = localhost)
    'PGPORT': '5432', # This is the port that PostgreSQL listens on (default = 5432)
    'PGUSER': 'bvar', # This is a username on PostgreSQL with SUPERUSER privlidges (default = postgres)
    'PGPASSWORD': '', # This is the password for the user specified above (default = '')
    'DB_PREFIX': 'mgiatest', # This will prepend a custom prefix to the database name that will get created.  An _ will be appended. (default = '')
    'ROLE_PREFIX': 'mgiatest', # This will prepend a custom prefix to the roles that will get created.  An _ will be appended. (default = '')
    'INSTALL_DIR': '/export/home/mgia/transfer-bin', # Set the directory that the CLI tools will be unzipped to (default = '.')
    'VERSION': '1.5', # This is the version of the release being deployed
    'SQL_FILES_LOCATION': '/export/home/mgia/transfer-sql', #Set the location of the sql files (default = '')
    'TRANSFER_PASSWD': '', # Set a password for the package modeler user role (default = 'transfer_user')
}

core_modeler = core_package.PackageModeler(config)

# Uncomment to drop dbs and roles
core_modeler.drop()

# Uncomment to create dbs, roles, load with fixtures
core_modeler.create_database()
core_modeler.create_roles()
core_modeler.create_tables()
core_modeler.grant_permissions()
core_modeler.deploy_drivers()

#BRIAN -- We need to discuss what fixtures are needed
core_modeler.create_fixtures(fixtures=(
        'createrepository -id ndiip',
        'createperson -id brian -firstname Brian -surname Vargas',
        'createsoftware -id packagemodeler-core-%s' % config['VERSION'],
        'createrole -id storage_system',
        'createsystem -id sun9 -host sun9.loc.gov -roles storage_system',
        'createsystem -id rs25 -host rs25.loc.gov -roles storage_system',
	'createsystem -id sun29 -host sun29.loc.gov -roles storage_system'
       ))

