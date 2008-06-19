package gov.loc.repository.transfer.components.filemanagement.transport;

import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.optional.ssh.Scp;

public class Transporter {

    public String keyfile;
    
    public Transporter(String keyfile) {
        this.keyfile = keyfile;
    }

    public void transport(String from_uri, String to_uri) {
        Scp scp = new Scp();
        scp.setKeyfile(this.keyfile);
        scp.setPassphrase("");
        scp.setTrust(true);
        scp.setProject(new Project());
        scp.setFile(from_uri);
        scp.setTodir(to_uri);
        scp.execute();
    }
}