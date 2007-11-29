package gov.loc.repository.transfer.components.filemanagement;

import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.ModelerAware;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.transfer.components.fileexamination.FileExaminer;
import gov.loc.repository.transfer.components.filemanagement.filters.FileFilter;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;

public interface FileCopier extends Component, ModelerAware {

	@JobType(name="copy")
	public boolean copy(
			@MapParameter(name="repositoryid") String repositoryId,
			@MapParameter(name="packageid") String packageId,
			@MapParameter(name="srcstoragesystemid") String srcStorageSystemId,
			@MapParameter(name="srcbasepath") String srcBasePath,
			@MapParameter(name="deststoragesystemid") String destStorageSystemId,
			@MapParameter(name="destbasepath") String destBasePath,
			@MapParameter(name="requestingagentid") String requestingAgentId,
			@MapParameter(name="filefilterclassname") String fileFilterClassName)
			throws Exception;

	@JobType(name="copy")
	public boolean copy(
			@MapParameter(name="repositoryid") String repositoryId,
			@MapParameter(name="packageid") String packageId,
			@MapParameter(name="srcbasepath") String srcBasePath,
			@MapParameter(name="srcmountpath") String srcMountPath,			
			@MapParameter(name="srcexternalidentifiervalue") String srcExternalIdentifierValue,
			@MapParameter(name="srcexternalidentifiertype") String srcExternalIdentifierType,
			@MapParameter(name="deststoragesystemid") String destStorageSystemId,
			@MapParameter(name="destbasepath") String destBasePath,
			@MapParameter(name="requestingagentid") String requestingAgentId,
			@MapParameter(name="filefilterclassname") String fileFilterClassName)
			throws Exception;
	
	/*
	 * Copies from the source File Location to the destination File Location.
	 * If a source mount path is provided, it is used as the base path for the source File Location.
	 * Returns true if the copy succeeded.
	 */
	public boolean copy(ExternalFileLocation srcFileLocation, String srcMountPath, StorageSystemFileLocation destFileLocation, Agent requestingAgent, FileFilter fileFilter) throws Exception;
	
	public boolean copy(StorageSystemFileLocation srcFileLocation, StorageSystemFileLocation destFileLocation, Agent requestingAgent, FileFilter fileFilter) throws Exception;
	
	public void setFileExaminer(FileExaminer fileExaminer);
}
