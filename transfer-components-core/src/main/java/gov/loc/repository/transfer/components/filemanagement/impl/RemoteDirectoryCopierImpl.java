package gov.loc.repository.transfer.components.filemanagement.impl;

import static gov.loc.repository.transfer.components.ComponentConstants.*;

import java.io.File;
import java.text.MessageFormat;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.loc.repository.bagit.bag.BagGeneratorVerifier;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.transfer.components.filemanagement.RemoteDirectoryCopier;
import gov.loc.repository.transfer.components.filemanagement.transport.Transporter;

@Component("remoteDirectoryCopierComponent")
@Scope("prototype")
public class RemoteDirectoryCopierImpl extends AbstractCopier implements RemoteDirectoryCopier {

    static final String COMPONENT_NAME = "remotedirectorycopier";
    
    private BagGeneratorVerifier verifier;

    // rely on convention for getting ssh key? follow up with brian
    //   user will always be transfer?
    //   homedir will always be /home/transfer?
    //   key will always be in .ssh/id_rsa?
    // pass in via constructor
    // homedir is in /home on AIX and Linux boxes, /export/home on Solaris
    private String keyFile = "/home/transfer/.ssh/id_rsa";

    // get this from the user
    // comes from interface?
    private String archiveUsername = "foo"; 

    // get this from the user
    //   value will vary with remoteHost, since rs25 and sun29 are different
    // this will be in this.destCopyToPath
    private String archivePath = "/lcbp/foo/bar"; 
    
    @Autowired  
    public RemoteDirectoryCopierImpl(@Qualifier("modelerFactory")ModelerFactory factory, @Qualifier("packageModelDao")PackageModelDAO dao, @Qualifier("javaSecurityBagGeneratorVerifier")BagGeneratorVerifier verifier) {
        super(factory, dao, verifier);
        this.verifier = verifier;
    }

    @Override
    protected String getComponentName() {
        return COMPONENT_NAME;
    }
    
    @Override
    protected void performCopy() throws Exception
    {
        this.getLog().debug(MessageFormat.format("Performing copy from {0} to {1}", this.srcPath, this.destCopyToPath));
        this.performTransport(this.srcPath, this.destCopyToPath);       
    }

    @Override
    protected boolean performVerify()
    {
        SimpleResult result = this.verifier.isValid(new File(this.destCopyToPath));
        if (result.isSuccess()) {
            this.getLog().info(MessageFormat.format("Package transported to {0} verified as a valid bag.", this.destCopyToPath));
            return true;
        }
        this.getLog().error(MessageFormat.format("Package at {0} not valid: {1}", this.destCopyToPath, result.getMessage()));
        return false;        
    }

    @Override
    public void copy(Long srcFileLocationId, String srcMountPath,
            Long destFileLocationId, String destMountPath,
            String requestingAgentId, String algorithm, String archiveUsername) throws Exception {
        this.copy(this.dao.loadRequiredFileLocation(srcFileLocationId), srcMountPath, this.dao.loadRequiredFileLocation(destFileLocationId), destMountPath, this.dao.findRequiredAgent(Agent.class, requestingAgentId), FixityAlgorithm.fromString(algorithm), archiveUsername);
    }

    @Override
    public void copy(FileLocation srcFileLocation, String srcMountPath,
            FileLocation destFileLocation, String destMountPath,
            Agent requestingAgent, FixityAlgorithm algorithm, String archiveUsername) throws Exception {
        this.internalCopy(srcFileLocation, srcMountPath, destFileLocation, destMountPath, requestingAgent, algorithm);
    }
    
    protected void performTransport(String srcPath, String destPath) throws Exception
    {
        // How to get remoteHost from storagesystem in PM?
        String remoteHost = "";
        
        try {
            Transporter transporter = new Transporter(this.keyFile);

            // assume "transfer" is remoteUsername 
            //   stored as a component constant, TRANSPORT_USERNAME
            transporter.pullAndArchive(TRANSPORT_USERNAME, remoteHost, srcPath, 
                    destPath, this.archiveUsername, this.archivePath);
        } catch (Exception ex) {
            throw new Exception(MessageFormat.format("Error during transport: {0}", ex.getStackTrace().toString()));
        }
    }
}
