package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface ArchivalRemoteBagCopier extends Component {

	static final String COMPONENT_NAME = "archivakremotebagcopier";
	
	/*
	 * Copies from the source FileLocation to the destination FileLocation and performs verification using the BagIt Validation Library.
	 * A FileCopyEvent is recorded.
	 * If mount path is provided, it is used as the base path for the FileLocation.
	 * If the destination FileLocation is LC package structure, the files are copied into the LC package structure.
	 */	
	@JobType(name="twostepremotebagcopy")
	public void copy(
            @RequestParam(name="srcfilelocationid") Long srcFileLocationId,
            @RequestParam(name="srcmountpath") String srcMountPath,			
            @RequestParam(name="destfilelocationid") Long destFileLocationId,
            @RequestParam(name="destmountpath") String destMountPath,			
            @RequestParam(name="requestingagentid") String requestingAgentId,
            @RequestParam(name="algorithm") String algorithm,
            @RequestParam(name="archiveownergroup") String archiveOwnerGroup)
	    throws Exception;

    public void copy(FileLocation srcFileLocation, String srcMountPath, FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FixityAlgorithm algorithm, String archiveOwnerGroup) throws Exception;		

}
