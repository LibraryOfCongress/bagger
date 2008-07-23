package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.MapParameter;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface TwoStepRemoteBagCopier extends Component {

	static final String COMPONENT_NAME = "twostepremotebagcopier";
	
	/*
	 * Copies from the source FileLocation to the destination FileLocation and performs verification using the BagIt Validation Library.
	 * A FileCopyEvent is recorded.
	 * If mount path is provided, it is used as the base path for the FileLocation.
	 * If the destination FileLocation is LC package structure, the files are copied into the LC package structure.
	 */	
	@JobType(name="twostepremotebagcopy")
	public void copy(
            @MapParameter(name="srcfilelocationid") Long srcFileLocationId,
			@MapParameter(name="srcmountpath") String srcMountPath,			
			@MapParameter(name="destfilelocationid") Long destFileLocationId,
			@MapParameter(name="destmountpath") String destMountPath,			
			@MapParameter(name="requestingagentid") String requestingAgentId,
			@MapParameter(name="algorithm") String algorithm,
            @MapParameter(name="archiveusername") String archiveUsername)
	    throws Exception;

    public void copy(FileLocation srcFileLocation, String srcMountPath, FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FixityAlgorithm algorithm, String archiveUsername) throws Exception;		

}
