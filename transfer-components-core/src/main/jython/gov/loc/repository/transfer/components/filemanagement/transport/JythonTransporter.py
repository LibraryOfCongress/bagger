""" 
Transport (scp) utility
-----------------------
Relies upon jython.jar (2.2.1), jsch-0.1.28.jar, ant-jsch.jar, ant.jar

When compiled from command-line, invoke thusly: 
  jythonc -idp gov.loc.repository.transfer.components.filemanagement.transport -w . Transporter.py
"""

import java
from org.apache.tools.ant import Project
from org.apache.tools.ant.taskdefs.optional import ssh
from gov.loc.repository.transfer.components.filemanagement.transport import AbstractTransporter

class JythonTransporter(AbstractTransporter):
    def __init__(self, keyfile):
        self.keyfile = keyfile

    def transport(self, from_uri, to_uri):
        "@sig public void transport(String from_uri, String to_uri)"
        scp = ssh.Scp()
        scp.setKeyfile(self.keyfile)
        scp.setPassphrase('')
        scp.setTrust(True)
        scp.setProject(Project())
        scp.setFile(from_uri)
        scp.setTodir(to_uri)
        scp.execute()
        print "%s -> %s" % (from_uri, to_uri)
        