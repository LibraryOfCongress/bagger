#!/usr/bin/env python
""" Deploys a build of RS25 Transfer Services to QA 
"""

from transfer.core import services

config = {
    'DEBUG': False, # If True, will print out actions rather than take them (e.g., will not hit database)
    'TRANSFER_SERVICES_INSTALL_DIR': '/opt/transfer', # Set the directory that the CLI tools will be unzipped to (default = '.')
    'VERSION': 'CHANGEME', # This is the version of the release being deployed
    'COMPONENT_PROJECTS': ("core",),
    'HOST': 'CHANGEME', #The hostname, e.g., localhost or ac, that the service container is to be exposed under.  Also used to identify the responder.  Default is localhost.    
    'QUEUES': "rs25jobqueue", #List of the queues to listen to, e.g., jobqueue,firewirejobqueue.  Default is jobqueue
    'JOBTYPES': "archivalremotebagcopy", #List of the job types to handle, e.g., test,inventoryfilesondisk.  Default is test
    'PGHOST': 'localhost', # This is the host that the PostgreSQL database lives on (default = localhost)
    'PGPORT': '5432', # This is the port that PostgreSQL listens on (default = 5432)      
    'DB_PREFIX': 'qa', # This will prepend a custom prefix to the database name that will get created.  An _ will be appended. (default = '')
    'ROLE_PREFIX': 'qa', # This will prepend a custom prefix to the roles that will get created.  An _ will be appended. (default = '')
    'TRANSFER_PASSWD': '', # Set a password for the package modeler user role (default = 'transfer_user')
    'REQUEST_BROKER_PASSWD': '', # Set a password for the service_request_broker role (default = 'service_request_broker_user')
    'COMPONENTS_PROPS': {
#        'components.zfsfilesystemcreator.pool':'zpool',
        'components.archivalremotedirectorycopier.keyfile':'/export/home/transfer/.ssh/id_rsa',
        'components.archivalremotedirectorycopier.staging.basepath':'/tmp'
    }                
}

transfer_services = services.TransferServices(config)

# Uncomment to deploy transfer service drivers
transfer_services.deploy_drivers()