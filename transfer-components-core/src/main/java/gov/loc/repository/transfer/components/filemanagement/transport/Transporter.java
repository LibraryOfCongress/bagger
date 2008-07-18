package gov.loc.repository.transfer.components.filemanagement.transport;

import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.Properties;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;

public class Transporter {

    public String keyFile;
    public String stagingPath;

    public Transporter(String keyFile) {
        this.keyFile = keyFile;
    }

    public void pullAndArchive(String remoteUsername, String remoteHost,
            String remotePath, String stagingBasePath, String archiveUsername, 
	        String archivePath) throws Exception {
	    this.pull(remoteUsername, remoteHost, remotePath, stagingBasePath);
	    this.archive(archiveUsername, archivePath);
    }

    public void pull(String remoteUsername, String remoteHost,
            String remotePath, String stagingBasePath) throws Exception {
        if (!stagingBasePath.endsWith("/")) {
            stagingBasePath += "/";
        }
        this.stagingPath = stagingBasePath + (new File(remotePath)).getName();

        try {
            // scp must be on the path
            Process p = Runtime.getRuntime().exec(
                    "scp -B -q -o StrictHostKeyChecking=no -r -i " 
                    + this.keyFile + " " 
                    + this.toUri(remoteUsername, remoteHost, remotePath) + " " 
			        + stagingBasePath
            );
            p.waitFor();
        }
        catch (IOException ioe) {
            throw new Exception(MessageFormat.format("I/O error during pull operation: {0}", ioe.getStackTrace().toString()));
        }
        catch (InterruptedException ie) {
            throw new Exception(MessageFormat.format("Interrupt error during pull operation: {0}", ie.getStackTrace().toString()));
        }
    }

    public void archive(String archiveUsername, String archivePath) throws Exception {
        boolean isArchiveSuccess = this.execute(archiveUsername, "cp -a " + this.stagingPath + " " + archivePath);
        if (isArchiveSuccess) {
            this.execute(archiveUsername, "rm -rf " + this.stagingPath);
        } 
        else {
            throw new Exception(MessageFormat.format("Archive of {0} to {1} by user {2} was not successful. Also, {0} not cleaned", this.stagingPath, archivePath, archiveUsername));
        }
    }

    private boolean execute(String archiveUsername, String cmd) {
        try {
            // re-using keyFile from before, start an ssh connection to loopback
            // and kick off a copy
            // cp and rm must be on the path of archiveUsername's local account
            JSch jsch = new JSch();
            jsch.addIdentity(this.keyFile);
            Session session = jsch.getSession(archiveUsername, "localhost", 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            String command = cmd;
            Channel channel = session.openChannel("exec");
            ((ChannelExec) channel).setCommand(command);
            channel.setInputStream(null);
            ((ChannelExec) channel).setErrStream(System.err);
            InputStream in = channel.getInputStream();
            channel.connect();
            byte[] tmp = new byte[1024];
            while (true) {
                while (in.available() > 0) {
                    int i = in.read(tmp, 0, 1024);
                    if (i < 0) {
                        break;
                    }
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    //System.out.println("exit-status: " + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                }
                catch (Exception ee) { return false; }
            }
            channel.disconnect();
            session.disconnect();
            return (channel.getExitStatus() == 0);
        }
        catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    private String toUri(String user, String host, String path) {
        return user + "@" + host + ":" + path;
    }
}
