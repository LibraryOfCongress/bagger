#!/usr/bin/env python
""" Deploys a build of NDNP Transfer Services to QA 
"""

from transfer.core import transferservices

config = {
    'DEBUG': False, # If True, will print out actions rather than take them (e.g., will not hit database)
    'TRANSFER_SERVICES_INSTALL_DIR': '/home/justin/transfer', # Set the directory that the CLI tools will be unzipped to (default = '.')
    'VERSION': '1.5', # This is the version of the release being deployed
    'COMPONENT_PROJECTS': ("core","ndnp"),
    'HOST': '', #The hostname, e.g., localhost or ac, that the service container is to be exposed under.  Also used to identify the responder.  Default is localhost.
    'QUEUES': "jobqueue,firewirejobqueue", #List of the queues to listen to, e.g., jobqueue,firewirejobqueue.  Default is jobqueue
    'JOBTYPES': "test,inventoryfilesondisk,generatelcmanifest,verifylcmanifest,ndnpcopy", #List of the job types to handle, e.g., test,inventoryfilesondisk.  Default is test
}

transfer_services = transferservices.TransferServices(config)

if True:
    print transfer_services.deploy_drivers()