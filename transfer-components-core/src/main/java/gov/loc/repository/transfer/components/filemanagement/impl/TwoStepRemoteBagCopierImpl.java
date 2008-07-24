package gov.loc.repository.transfer.components.filemanagement.impl;

import java.util.HashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import gov.loc.repository.bagit.bag.BagGeneratorVerifier;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.transfer.components.filemanagement.TwoStepRemoteBagCopier;

@Component("twoStepRemoteBagCopierComponent")
public class TwoStepRemoteBagCopierImpl extends ConfigurableCopier implements TwoStepRemoteBagCopier {

    static final String COMPONENT_NAME = "remotedirectorycopier";
    public static final String ARCHIVE_USERNAME_KEY = "archive_username";
    
    @Autowired  
    public TwoStepRemoteBagCopierImpl(@Qualifier("modelerFactory")ModelerFactory factory, @Qualifier("packageModelDao")PackageModelDAO dao, @Qualifier("javaSecurityBagGeneratorVerifier")BagGeneratorVerifier generator, @Qualifier("bagFileCopyVerifier")FileCopyVerifier verifier, @Qualifier("twoStepRemoteDirectoryCopier") DirectoryCopier copier) {
    	super(factory, dao, generator, copier, verifier);
    }
    
    @Override
    protected String getComponentName() {
        return COMPONENT_NAME;
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
        Map<String,String> additionalParameters = new HashMap<String,String>();
        additionalParameters.put(ARCHIVE_USERNAME_KEY, archiveUsername);
        
        this.internalCopy(srcFileLocation, srcMountPath, destFileLocation, destMountPath, requestingAgent, algorithm, additionalParameters);
    }
    
}
