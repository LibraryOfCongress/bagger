package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.MapParameter;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface RemoteBagCopier extends Component {

    static final String COMPONENT_NAME = "remotebagcopier";
    
    /*
     * Copies from the source FileLocation to the destination FileLocation and performs verification using the NDNP Validation Library.
     * A FileCopyEvent is recorded.
     * If mount path is provided, it is used as the base path for the FileLocation.
     * If the destination FileLocation is a bag, the files are copied into the bag structure and the rest of the bag is generated.
     */ 
    @JobType(name="remotebagcopy")
    public void copy(
            @MapParameter(name="srcfilelocationid") Long srcFileLocationId,
            @MapParameter(name="srcmountpath") String srcMountPath,         
            @MapParameter(name="destfilelocationid") Long destFileLocationId,
            @MapParameter(name="destmountpath") String destMountPath,           
            @MapParameter(name="requestingagentid") String requestingAgentId,
            @MapParameter(name="algorithm") String algorithm
            )
            throws Exception;
    
    public void copy(FileLocation srcFileLocation, String srcMountPath, FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FixityAlgorithm algorithm) throws Exception;
        
}
