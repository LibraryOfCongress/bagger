package gov.loc.repository.transfer.components.filemanagement.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.transfer.components.fileexamination.LCManifestGenerator;
import gov.loc.repository.transfer.components.fileexamination.Verifier;
import gov.loc.repository.transfer.components.filemanagement.LCPackageCopier;
//import gov.loc.repository.transfer.components.filemanagement.transport.*;

@Component("lcPackageCopierComponent")
@Scope("prototype")
public class LCPackageCopierImpl extends AbstractLCPackageCopier implements LCPackageCopier {

    private Verifier verifier;
    
    @Autowired
    public LCPackageCopierImpl(@Qualifier("modelerFactory")ModelerFactory factory, @Qualifier("packageModelDao")PackageModelDAO dao, @Qualifier("batchVerifier")Verifier batchVerifier, @Qualifier("md5DeepComponent")LCManifestGenerator generator) {
        super(factory, dao, generator);
        this.verifier = batchVerifier;
    }
    
    @Override
    protected String getComponentName() {
        return COMPONENT_NAME;
    }

    /* 
    public void copy(long srcFileLocationId, String srcHost,
            long destFileLocationId, String destHost,
            String requestingAgentId) throws Exception {
        this.copy(srcFileLocationId, srcHost, destFileLocationId, destHost, requestingAgentId);
        //Transporter transporter = new Transporter("/home/transfer/.ssh/id_rsa");
        //transporter.transport("/home/mjg/tmp/test1", "transfer@rg.rdc.lctl.gov:tmp/test2");  
    }

    public void copy(FileLocation srcFileLocation, String srcMountPath,
            FileLocation destFileLocation, String destMountPath,
            Agent requestingAgent) throws Exception {
        LCManifestVerifier verifier = new Md5DeepImpl();
        //this.copy(this.dao.loadRequiredFileLocation(srcFileLocationId), srcMountPath, this.dao.loadRequiredFileLocation(destFileLocationId), destMountPath, this.dao.findRequiredAgent(Agent.class, requestingAgentId));
        this.copy(srcFileLocation, srcMountPath, destFileLocation, destMountPath, requestingAgent, null, (Verifier) verifier);
    }
    */
    @Override
    public void copy(Long srcFileLocationId, String srcMountPath,
            Long destFileLocationId, String destMountPath,
            String requestingAgentId, String algorithm) throws Exception {
        this.copy(this.dao.loadRequiredFileLocation(srcFileLocationId), srcMountPath, this.dao.loadRequiredFileLocation(destFileLocationId), destMountPath, this.dao.findRequiredAgent(Agent.class, requestingAgentId), Algorithm.fromString(algorithm));

    }

    @Override
    public void copy(FileLocation srcFileLocation, String srcMountPath,
            FileLocation destFileLocation, String destMountPath,
            Agent requestingAgent, Algorithm algoritm) throws Exception {
        //BatchFileFilter fileFilter = new BatchFileFilter();
        //fileFilter.initialize(srcFileLocation, srcMountPath);
        this.copy(srcFileLocation, srcMountPath, destFileLocation, destMountPath, requestingAgent, verifier, algoritm);
    }

}
