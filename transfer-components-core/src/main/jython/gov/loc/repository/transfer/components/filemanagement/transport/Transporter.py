import java
from gov.loc.repository.transfer.components.filemanagement.transport import AbstractTransporter
from org.apache.tools.ant import Project
from org.apache.tools.ant.taskdefs.optional import ssh

""" 
    CLASSPATH: /usr/share/jython2.2.1/jython.jar, /usr/share/java/jsch-0.1.28.jar, 
               /usr/share/ant/lib/ant-jsch.jar, /usr/share/ant/lib/ant.jar

    Compile with: jythonc -idp gov.loc.repository.transfer.components.filemanagement.transport -w . Transporter.py
"""

class Transporter(AbstractTransporter):
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
