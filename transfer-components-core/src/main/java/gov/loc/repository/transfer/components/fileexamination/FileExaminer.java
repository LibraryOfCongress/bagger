package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.ModelerAware;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.utilities.FixityHelper;

public interface FileExaminer extends Component, ModelerAware {
	
	public static final String COMPONENT_NAME = "fileexaminer";
		
	public void setFixityHelper(FixityHelper fixityHelper);
	
	/*
	 * Examines the files in the basepath and all subdirectories.
	 * A FileExamination is created for each file that is examined and associated with a FileExaminationGroup.
	 * A FileObservationEvent is created and associated with the FileExaminationGroup. 
	 */
	@JobType(name="inventory")
	public void examine(
			@MapParameter(name="repositoryid") String repositoryId,
			@MapParameter(name="packageid") String packageId,
			@MapParameter(name="storagesystemid") String storageSystemId,
			@MapParameter(name="basepath") String basePath,
			@MapParameter(name="requestingagentid") String requestingAgentId,
			@MapParameter(name="createfileinstances") boolean createFileInstances)
			throws Exception;

	/*
	 * Examines the files in the basepath and all subdirectories.
	 * A FileExamination is created for each file that is examined and associated with a FileExaminationGroup.
	 * A FileObservationEvent is created and associated with the FileExaminationGroup. 
	 */
	@JobType(name="inventory")	
	public void examine(
			@MapParameter(name="repositoryid") String repositoryId,
			@MapParameter(name="packageid") String packageId,
			@MapParameter(name="basepath") String basePath,
			@MapParameter(name="mountpath") String mountPath,
			@MapParameter(name="externalidentifiervalue") String externalIdentifierValue,
			@MapParameter(name="externalidentifiertype") String externalIdentifierType,
			@MapParameter(name="requestingagentid") String requestingAgentId,
			@MapParameter(name="createfileinstances") boolean createFileInstances)
			throws Exception;
	
	public void examine(ExternalFileLocation fileLocation, String mountPath, Agent requestingAgent, boolean createFileInstances) throws Exception;

	public void examine(StorageSystemFileLocation fileLocation, Agent requestingAgent, boolean createFileInstances) throws Exception;
	
	
}
