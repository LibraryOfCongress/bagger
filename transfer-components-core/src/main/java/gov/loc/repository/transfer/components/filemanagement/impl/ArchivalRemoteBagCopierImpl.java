package gov.loc.repository.transfer.components.filemanagement.impl;

import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.bagit.bag.BagGeneratorVerifier;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.transfer.components.filemanagement.ArchivalRemoteBagCopier;

public class ArchivalRemoteBagCopierImpl extends ConfigurableCopier implements ArchivalRemoteBagCopier {

    static final String COMPONENT_NAME = "remotedirectorycopier";
    
    public ArchivalRemoteBagCopierImpl(ModelerFactory factory, PackageModelDAO dao, String reportingAgentId, BagGeneratorVerifier generator, FileCopyVerifier verifier, DirectoryCopier copier) {
    	super(factory, dao, reportingAgentId, generator, copier, verifier);
    }
    
    @Override
    protected String getComponentName() {
        return COMPONENT_NAME;
    }
    
    @Override
    public void copy(Long srcFileLocationId, String srcMountPath,
            Long destFileLocationId, String destMountPath,
            String requestingAgentId, String algorithm, String owner, String group) throws Exception {
        this.copy(this.dao.loadRequiredFileLocation(srcFileLocationId), srcMountPath, this.dao.loadRequiredFileLocation(destFileLocationId), destMountPath, this.dao.findRequiredAgent(Agent.class, requestingAgentId), FixityAlgorithm.fromString(algorithm), owner, group);
    }

    @Override
    public void copy(FileLocation srcFileLocation, String srcMountPath,
            FileLocation destFileLocation, String destMountPath,
            Agent requestingAgent, FixityAlgorithm algorithm, String owner, String group) throws Exception {
        Map<String,String> additionalParameters = new HashMap<String,String>();
		additionalParameters.put(Chowner.USER_KEY, owner);
		additionalParameters.put(Chowner.GROUP_KEY, group);
        
        this.internalCopy(srcFileLocation, srcMountPath, destFileLocation, destMountPath, requestingAgent, algorithm, additionalParameters);
    }
    
}
