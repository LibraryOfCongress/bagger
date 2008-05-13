package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.MapParameter;

public interface FilesOnDiskInventorier extends Component {
	
	public static final String COMPONENT_NAME = "filesondiskinventorier";
		
	/*
	 * Examines the files in the basepath and all subdirectories.
	 * A FileInstance is created for each file that is examined and associated with the FileLocation.
	 * An InventoryFromFilesOnDiskEvent is created and associated with the FileLocation. 
	 */
	@JobType(name="inventoryfilesondisk")	
	public void inventory(
			@MapParameter(name="filelocationkey") long fileLocationKey,
			@MapParameter(name="mountpath") String mountPath,
			@MapParameter(name="algorithm") String algorithm,
			@MapParameter(name="requestingagentid") String requestingAgentId)
			throws Exception;
	
	public void inventory(FileLocation fileLocation, String mountPath, Fixity.Algorithm algorithm, Agent requestingAgent) throws Exception;
		
}
