package gov.loc.repository.transfer.components.filemanagement;
   
import static gov.loc.repository.transfer.components.ComponentConstants.DEFAULT_STAGING_BASEPATH;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.Properties;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.ChannelExec;

import gov.loc.repository.transfer.components.filemanagement.impl.ArchivalRemoteBagCopierImpl;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;

public class Transporter {

    private static final Log log = LogFactory.getLog(Transporter.class);

    private String keyFile;
    private String stagingBasePath;

    protected ProcessBuilderWrapper pb;
    
    public Transporter(String keyFile) {
        this.keyFile = keyFile;
    }

    public void pullAndArchive(String remoteUsername, String remoteHost, CopyDescription copyDescription)
    {
        this.pull(remoteUsername, remoteHost, copyDescription);
        this.archive(copyDescription);
    }

    public void pull(String remoteUsername, String remoteHost, CopyDescription copyDescription)
    {
        this.stagingBasePath = 
            (copyDescription.additionalParameters.containsKey("stagingBasePath"))
            ? copyDescription.additionalParameters.get("stagingBasePath") : DEFAULT_STAGING_BASEPATH;
        log.debug(MessageFormat.format("stagingBasePath set to {0}", this.stagingBasePath));
        String commandLine = MessageFormat.format("scp -B -q -o StrictHostKeyChecking=no -r -i {0} {1} {2}", this.keyFile, this.toUri(remoteUsername, remoteHost, copyDescription.srcPath), this.stagingBasePath);
        log.debug("Commandline is " + commandLine);
        ProcessBuilderResult result = pb.execute(commandLine);
        if (result.getExitValue() != 0)
        {
            log.error(MessageFormat.format("{0} returned {1}.  Output was {2}", commandLine, result.getExitValue(), result.getOutput()));
            throw new RuntimeException("Pull remote directory failed");
        }
    }  

    public void archive(CopyDescription copyDescription)
    {
        String[] archiveOwnerGroup = copyDescription.additionalParameters.get(ArchivalRemoteBagCopierImpl.ARCHIVE_OWNERGROUP_KEY).split(":");
        String archiveOwner = archiveOwnerGroup[0]; 
        String archiveGroup;
        if (archiveOwnerGroup.length > 1) {
            // archiveGroup is currently not used for anything
            archiveGroup = archiveOwnerGroup[1];
        }
                                                
        if (! this.stagingBasePath.endsWith("/")) {
            this.stagingBasePath += "/";
        }
        String stagingPath = this.stagingBasePath + (new File(copyDescription.srcPath)).getName();

        boolean isArchiveSuccess = this.execute(archiveOwner, "cp -a " + stagingPath + " " + copyDescription.destCopyToPath);
        if (isArchiveSuccess) {
            this.execute(archiveOwner, "rm -rf " + stagingPath);
        } 
        else {
            throw new RuntimeException(MessageFormat.format("Archive of {0} to {1} by user {2} was not successful. Also, {0} not cleaned", stagingPath, copyDescription.destCopyToPath, archiveOwner));
        }
    }
    
    private boolean execute(String archiveOwner, String cmd) {
        try {
            // re-using keyFile from before, start an ssh connection to loopback
            // and kick off a copy
            // cp and rm must be on the path of archiveOwner's local account
            JSch jsch = new JSch();
            jsch.addIdentity(this.keyFile);
            Session session = jsch.getSession(archiveOwner, "localhost", 22);
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
                    log.debug(MessageFormat.format("exit-status: {0}", channel.getExitStatus()));
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
            log.error(MessageFormat.format("transport error: {0}", e.getMessage()));
            return false;
        }
    }

    private String toUri(String user, String host, String path) {
        return user + "@" + host + ":" + path;
    }
}