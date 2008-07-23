package gov.loc.repository.transfer.components.filemanagement.impl;

import static gov.loc.repository.transfer.components.ComponentConstants.TRANSPORT_USERNAME;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;

import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.DirectoryCopier;

public class TwoStepRemoteDirectoryCopier implements DirectoryCopier {

	private static final Log log = LogFactory.getLog(TwoStepRemoteDirectoryCopier.class);
	
	private String keyFile;
	
	public TwoStepRemoteDirectoryCopier(String keyFile) {
		this.keyFile = keyFile;
	}
	
	@Override
	public void copy(CopyDescription copyDescription) {
        log.debug(MessageFormat.format("Performing copy from {0} to {1}", copyDescription.srcPath, copyDescription.destCopyToPath));

		String remoteHost = ((StorageSystemFileLocation)copyDescription.destFileLocation).getStorageSystem().getHost();

	    
		this.pull(TRANSPORT_USERNAME, remoteHost, copyDescription);
	    this.archive(copyDescription);
		
	}
	
    private void pull(String remoteUsername, String remoteHost, CopyDescription copyDescription) {

        try {
            // scp must be on the path
            Process p = Runtime.getRuntime().exec(
                    "scp -B -q -o StrictHostKeyChecking=no -r -i " 
                    + this.keyFile + " " 
                    + this.toUri(remoteUsername, remoteHost, copyDescription.srcPath) + " " 
			        + copyDescription.destCopyToPath
            );
            p.waitFor();
        }
        catch (IOException ioe) {
            throw new RuntimeException(MessageFormat.format("I/O error during pull operation: {0}", ioe.getStackTrace().toString()));
        }
        catch (InterruptedException ie) {
            throw new RuntimeException(MessageFormat.format("Interrupt error during pull operation: {0}", ie.getStackTrace().toString()));
        }
    }

    private void archive(CopyDescription copyDescription)
    {
    	String archiveUsername = copyDescription.additionalParameters.get(TwoStepRemoteBagCopierImpl.ARCHIVE_USERNAME_KEY);
	    String stagingBasePath = copyDescription.destCopyToPath;
	    if (!stagingBasePath.endsWith("/")) {
            stagingBasePath += "/";
        }
        String stagingPath = stagingBasePath + (new File(copyDescription.srcPath)).getName();

    	boolean isArchiveSuccess = this.execute(archiveUsername, "cp -a " + stagingPath + " " + copyDescription.destCopyToPath);
        if (isArchiveSuccess) {
            this.execute(archiveUsername, "rm -rf " + stagingPath);
        } 
        else {
            throw new RuntimeException(MessageFormat.format("Archive of {0} to {1} by user {2} was not successful. Also, {0} not cleaned", stagingPath, copyDescription.destCopyToPath, archiveUsername));
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
