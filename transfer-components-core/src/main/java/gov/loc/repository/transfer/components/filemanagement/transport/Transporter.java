package gov.loc.repository.transfer.components.filemanagement.transport;

import java.util.Properties;
import java.io.File;
import java.io.InputStream;
import java.io.IOException;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.Session;

public class Transporter {

    public String keyFile;

    public String stagingPath;

    public Transporter(String keyFile) {
        this.keyFile = keyFile;
    }

    public void pull(String remoteUsername, String remoteHost,
            String remotePath, String stagingBasePath) {
        this.stagingPath = stagingBasePath + (new File(remotePath)).getName();
        try {
            // scp must be on the path
            Process p = Runtime.getRuntime().exec(
                            "scp -B -q -r -i " 
                            + this.keyFile + " " 
                            + this.toUri(remoteUsername, remoteHost, remotePath)
                            + " " + stagingBasePath
                        );
            p.waitFor();
        }
        catch (IOException ioe) {
            System.out.println("*** IO ERROR");
            ioe.printStackTrace();
        }
        catch (InterruptedException ie) {
            System.out.println("*** INTERRUPT ERROR");
            ie.printStackTrace();
        }
    }

    public void archive(String archiveUsername, String archivePath) {
        try {
            // re-using keyFile from before, start an ssh connection to loopback
            // and kick off a copy
            // cp must be on the path of archiveUsername's local account
            JSch jsch = new JSch();
            jsch.addIdentity(this.keyFile);
            Session session = jsch.getSession(archiveUsername, "localhost", 22);
            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);
            session.connect();
            String command = "cp -a " + this.stagingPath + " " + archivePath;
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
                    if (i < 0)
                        break;
                    System.out.print(new String(tmp, 0, i));
                }
                if (channel.isClosed()) {
                    System.out.println("exit-status: "
                            + channel.getExitStatus());
                    break;
                }
                try {
                    Thread.sleep(1000);
                }
                catch (Exception ee) {}
            }
            channel.disconnect();
            session.disconnect();
        }
        catch (Exception e) {
            System.out.println(e);
        }
    }

    private String toUri(String user, String host, String path) {
        return user + "@" + host + ":" + path;
    }
}
