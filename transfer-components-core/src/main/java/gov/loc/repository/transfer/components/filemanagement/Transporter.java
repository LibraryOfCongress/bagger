package gov.loc.repository.transfer.components.filemanagement;
   
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
import com.martiansoftware.jsap.FlaggedOption;
import com.martiansoftware.jsap.JSAP;
import com.martiansoftware.jsap.JSAPResult;
import com.martiansoftware.jsap.Parameter;
import com.martiansoftware.jsap.SimpleJSAP;

import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.Chowner;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.ProcessBuilderWrapper.ProcessBuilderResult;
import gov.loc.repository.utilities.impl.ProcessBuilderWrapperImpl;

public class Transporter {

    private static final Log log = LogFactory.getLog(Transporter.class);

    private String keyFile;
    private String stagingBasePath;
    protected ProcessBuilderWrapper pb = new ProcessBuilderWrapperImpl();
    
    public static void main(String[] args) throws Exception
    {
    	final String KEYFILE = "keyfile";
    	final String REMOTE_USERNAME = "remote_username";
    	final String REMOTE_HOST = "remote_host";
    	final String STAGING_BASE_PATH = "staging_base_path";
    	final String SOURCE_PATH = "source_path";
    	final String DEST_PATH = "dest_path";
    	final String OWNER = "owner";
    	
    	SimpleJSAP jsap = new SimpleJSAP("Transporter",
    			"Performs a two step copy.",
    			new Parameter[] {
    				new FlaggedOption(KEYFILE, JSAP.STRING_PARSER, "/home/transfer/.ssh/id_rsa", true, 'k', "keyfile", "keyfile containing the keys to remote machines" ),
    				new FlaggedOption(REMOTE_USERNAME, JSAP.STRING_PARSER, null, true, 'u', "username", "remote username" ),
    				new FlaggedOption(REMOTE_HOST, JSAP.STRING_PARSER, null, true, 'h', "host", "remote host" ),
    				new FlaggedOption(STAGING_BASE_PATH, JSAP.STRING_PARSER, "/tmp", true, 'b', "staging", "staging base path" ),
    				new FlaggedOption(SOURCE_PATH, JSAP.STRING_PARSER, null, true, 's', "source", "source path" ),
    				new FlaggedOption(DEST_PATH, JSAP.STRING_PARSER, null, true, 'd', "dest", "destination path" ),
    				new FlaggedOption(OWNER, JSAP.STRING_PARSER, null, true, 'o', "owner", "archival owner" ),    				
    	}
    	);
    	
    	JSAPResult config = jsap.parse(args);    
        if ( jsap.messagePrinted() ) System.exit( 1 );

        Transporter transporter = new Transporter(config.getString(KEYFILE), config.getString(STAGING_BASE_PATH));
        transporter.pullAndArchive(config.getString(REMOTE_USERNAME), config.getString(REMOTE_HOST), config.getString(SOURCE_PATH), config.getString(DEST_PATH), config.getString(OWNER));
    }
    
    public Transporter(String keyFile, String stagingBasePath) {
        this.keyFile = keyFile;
        this.stagingBasePath= stagingBasePath;
        if (! this.stagingBasePath.endsWith("/")) {
            this.stagingBasePath += "/";
        }            
        
    }
        
    public void pullAndArchive(String remoteUsername, String remoteHost, String srcPath, String destPath, String owner)
    {
                    
    	this.pull(remoteUsername, remoteHost, srcPath);
        this.archive(stagingBasePath, srcPath, owner, destPath);
    }

    public void pullAndArchive(String remoteUsername, String remoteHost, CopyDescription copyDescription)
    {
        
        this.pullAndArchive(remoteUsername, remoteHost, copyDescription.srcPath, copyDescription.destCopyToPath, copyDescription.additionalParameters.get(Chowner.USER_KEY));
    }
    
    
    private void pull(String remoteUsername, String remoteHost, String srcPath)
    {
        log.debug(MessageFormat.format("stagingBasePath set to {0}", stagingBasePath));
        String commandLine = MessageFormat.format("scp -B -q -o StrictHostKeyChecking=no -r -i {0} {1} {2}", this.keyFile, this.toUri(remoteUsername, remoteHost, srcPath), stagingBasePath);
        log.debug("Commandline is " + commandLine);
        ProcessBuilderResult result = pb.execute(commandLine);
        if (result.getExitValue() != 0)
        {
            log.error(MessageFormat.format("{0} returned {1}.  Output was {2}", commandLine, result.getExitValue(), result.getOutput()));
            throw new RuntimeException("Pull remote directory failed");
        }
    }  

    private void archive(String stagingBasePath, String srcPath, String owner, String destPath)
    {
                                                
        String stagingPath = stagingBasePath + (new File(srcPath)).getName();

        boolean isArchiveSuccess = this.execute(owner, "cp -rf " + stagingPath + " " + destPath);
        if (isArchiveSuccess) {
            this.execute(owner, "rm -rf " + stagingPath);
        } 
        else {
            throw new RuntimeException(MessageFormat.format("Archive of {0} to {1} by user {2} was not successful. Also, {0} not cleaned", stagingPath, destPath, owner));
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