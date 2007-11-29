package gov.loc.repository.transfer.components.fileexamination.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.events.fileexaminationgroup.FileExaminationEvent;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.transfer.components.BaseComponent;
import gov.loc.repository.transfer.components.fileexamination.FileExaminationResult;
import gov.loc.repository.transfer.components.fileexamination.FileExaminer;
import gov.loc.repository.utilities.FilenameHelper;
import gov.loc.repository.utilities.FixityHelper;

public class FileExaminerImpl extends BaseComponent implements FileExaminer {

	private FixityHelper fixityHelper;
	private FileExaminationGroup fileExaminationGroup;
	private File baseDir;
		
	public void setFixityHelper(FixityHelper fixityHelper) {
		this.fixityHelper = fixityHelper;		
	}
	
	@Override
	protected String getComponentName() {
		return COMPONENT_NAME;
	}
	
	protected FileExaminationResult examine(String basePath, String relativeFilename) throws Exception {
		FileExaminationResult result = new FileExaminationResult();
		result.relativeFilename = relativeFilename;
				
		File file = new File(basePath, relativeFilename);
		if (file.exists() && file.canRead())
		{
			result.size = file.length();
			result.modifiedDate = new Date(file.lastModified());
			result.fixityAlgorithm = this.fixityHelper.getAlgorithm();
			result.fixityValue = this.fixityHelper.generateFixity(file);
		}
		
		this.getReportingLog().info(result.toString());
		
		return result;
	}

	public void examine(String repositoryId, String packageId, String storageSystemId, String basePath, String requestingAgentId, boolean createFileInstances) throws Exception {
		//Find a File Location
		Package packge = dao.findRequiredPackage(Package.class, repositoryId, packageId);		
		FileLocation fileLocation = packge.getFileLocation(storageSystemId, basePath);
		if (fileLocation == null)
		{
			throw new Exception(MessageFormat.format("File Location for storage system {0} and basepath {1} does not exist", storageSystemId, basePath));
		}
		this.examine(fileLocation, null, this.dao.findRequiredAgent(Agent.class, requestingAgentId), createFileInstances);
	}

	public void examine(String repositoryId, String packageId, String basePath, String mountPath, String externalIdentifierValue, String externalIdentifierType, String requestingAgentId, boolean createFileInstances) throws Exception
	{
		//Find a File Location
		Package packge = dao.findRequiredPackage(Package.class, repositoryId, packageId);		
		FileLocation fileLocation = packge.getFileLocation(new ExternalIdentifier(externalIdentifierValue, IdentifierType.valueOf(externalIdentifierType)));
		if (fileLocation == null)
		{
			throw new Exception(MessageFormat.format("File Location for external file location with identier value {0} identifier type {1} does not exist", externalIdentifierValue, externalIdentifierType));
		}
		this.examine(fileLocation, mountPath, this.dao.findRequiredAgent(Agent.class, requestingAgentId), createFileInstances);
		
	}	
	
	protected void examine(FileLocation fileLocation, String mountPath, Agent requestingAgent, boolean createFileInstances) throws Exception {
		this.fileExaminationGroup = factory.createFileExaminationGroup(fileLocation, true);	
		FileExaminationEvent event = this.factory.createFileExaminationGroupEvent(FileExaminationEvent.class, fileExaminationGroup, Calendar.getInstance().getTime(), this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setPerformingAgent(this.getReportingAgent());
		this.baseDir = determineBaseDir(fileLocation, mountPath);
		if (! this.baseDir.exists())
		{
			throw new Exception(MessageFormat.format("Basepath {0} does not exist", baseDir.toString()));
		}
		this.processDirectory(this.baseDir);
		
		if (createFileInstances)
		{
			factory.createFileInstancesFromFileExaminations(this.fileExaminationGroup.getFileLocation(), this.fileExaminationGroup.getFileExaminations());
		}
		
		event.setEventEnd(Calendar.getInstance().getTime());
		
	}
	
	protected File determineBaseDir(FileLocation fileLocation, String mountPath)
	{
		File baseDir;
		if (mountPath != null)
		{
			baseDir = new File(mountPath);
		}
		else
		{
			baseDir = new File(fileLocation.getBasePath());
		}
		return baseDir;
	}
	
	private void processFile(File file) throws Exception
	{
		FileExaminationResult fileExamResult = this.examine(this.baseDir.toString(), FilenameHelper.removeBasePath(this.baseDir.toString(), file.toString()));
		FileExamination fileExamination = this.factory.createFileExamination(this.fileExaminationGroup, new FileName(fileExamResult.relativeFilename), new Fixity(fileExamResult.fixityValue, Enum.valueOf(Algorithm.class, fileExamResult.fixityAlgorithm)));
		fileExamination.setBytes(fileExamResult.size);
		fileExamination.setFileModifiedTimestamp(fileExamResult.modifiedDate);		
	}
	
	private void processDirectory(File dir) throws Exception
	{
		for(File file : dir.listFiles())
		{
			if (file.isFile())
			{
				this.processFile(file);
			}
			else if (file.isDirectory())
			{
				this.processDirectory(file);
			}
			else
			{
				throw new Exception(MessageFormat.format("{0} is neither a file nor a directory", file.toString()));
			}
		}
	}

	public void examine(ExternalFileLocation fileLocation, String mountPath, Agent requestingAgent, boolean createFileInstances) throws Exception {
		this.examine(fileLocation, mountPath, requestingAgent, createFileInstances);
	}

	public void examine(StorageSystemFileLocation fileLocation, Agent requestingAgent, boolean createFileInstances) throws Exception {
		this.examine(fileLocation, null, requestingAgent, createFileInstances);
		
	}
		
}
