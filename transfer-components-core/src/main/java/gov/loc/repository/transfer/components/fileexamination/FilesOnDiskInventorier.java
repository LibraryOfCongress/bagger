package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.ModelerAware;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.utilities.FixityHelper;

public interface FilesOnDiskInventorier extends Component, ModelerAware {
	
	public static final String COMPONENT_NAME = "filesondiskinventorier";
		
	public void setFixityHelper(FixityHelper fixityHelper);
	
	/*
	 * Examines the files in the basepath and all subdirectories.
	 * A FileInstance is created for each file that is examined and associated with the FileLocation.
	 * An InventoryFromFilesOnDiskEvent is created and associated with the FileLocation. 
	 */
	@JobType(name="inventoryfilesondisk")	
	public void inventory(
			@MapParameter(name="filelocationkey") long fileLocationKey,
			@MapParameter(name="mountpath") String mountPath,
			@MapParameter(name="requestingagentid") String requestingAgentId)
			throws Exception;
	
	public void inventory(FileLocation fileLocation, String mountPath, Agent requestingAgent) throws Exception;
		
}
