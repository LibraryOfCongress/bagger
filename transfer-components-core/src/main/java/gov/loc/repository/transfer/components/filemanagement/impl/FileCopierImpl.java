package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.apache.commons.io.FileUtils;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.transfer.components.BaseComponent;
import gov.loc.repository.transfer.components.fileexamination.FileExaminer;
import gov.loc.repository.transfer.components.filemanagement.FileCopier;
import gov.loc.repository.transfer.components.filemanagement.filters.FileFilter;

public class FileCopierImpl extends BaseComponent implements FileCopier {

	private FileExaminer fileExaminer;
	
	private static final String CONTENT_DIRECTORY = "contents";
	
	@Override
	protected String getComponentName() {
		return "filecopier";
	}

	public void setFileExaminer(FileExaminer fileExaminer) {
		this.fileExaminer = fileExaminer;
		
	}
	
	public boolean copy(ExternalFileLocation srcFileLocation, String srcMountPath,
			StorageSystemFileLocation destFileLocation, Agent requestingAgent, FileFilter fileFilter)
			throws Exception {
		return this.copy(srcFileLocation, srcMountPath, destFileLocation, requestingAgent, fileFilter);

	}

	protected boolean copy(FileLocation srcFileLocation, String srcMountPath,
			StorageSystemFileLocation destFileLocation, Agent requestingAgent, FileFilter fileFilter)
			throws Exception {
		if (srcFileLocation.isLCPackageStructure() && ! destFileLocation.isLCPackageStructure())
		{
			throw new Exception("Cannot copy from an LC package structure to a non-LC package structure");
		}
		List<FileInstance> filteredFileInstances = new ArrayList<FileInstance>();
		for(FileInstance fileInstance : srcFileLocation.getFileInstances())
		{
			if (fileFilter == null || fileFilter.accept(new File(fileInstance.getFileName().getFilename())))
			{
				filteredFileInstances.add(fileInstance);
			}
		}
		String srcPath = srcFileLocation.getBasePath();
		if (srcMountPath != null)
		{
			srcPath = srcMountPath;
		}
		Calendar start = Calendar.getInstance();
		boolean isSuccess = true;
		for(FileInstance fileInstance : filteredFileInstances)
		{
			//Copy the fileInstance
			File srcFile = new File(srcPath, fileInstance.getFileName().getFilename());
			File destFile = new File(destFileLocation.getBasePath(), fileInstance.getFileName().getFilename());
			if (! srcFileLocation.isLCPackageStructure() && destFileLocation.isLCPackageStructure())
			{
				//Adjust the destFile
				File destDir = new File(destFileLocation.getBasePath(), CONTENT_DIRECTORY);
				destFile = new File(destDir, fileInstance.getFileName().getFilename());
			}
			try
			{
				FileUtils.copyFile(srcFile, destFile);
			}
			catch(Exception ex)
			{
				this.getReportingLog().error(MessageFormat.format("An error occurred copying from {0} to {1}: {2}", srcFile.toString(), destFile.toString(), ex.getMessage()));
				isSuccess = false;
				
			}
			//Adjust path if moving from a non-LC-structured file location to an LC-structured file location
		}
		//Create the File Copy Event
		FileCopyEvent event = this.factory.createFileLocationEvent(FileCopyEvent.class, destFileLocation, start.getTime(), this.getReportingAgent());
		event.setEventEnd(Calendar.getInstance().getTime());
		event.setPerformingAgent(this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setFileLocationSource(srcFileLocation);
		event.setSuccess(isSuccess);
		dao.save(event);
		//Do an inventory for destFileLocation
		fileExaminer.examine(destFileLocation, requestingAgent, false);
				
		FileExaminationGroup fileExaminationGroup = destFileLocation.getFileExaminationGroups().get(0);
		/*
		FileListComparisonResult result = dao.compare(srcFileLocation, fileExaminationGroup);
		if (! result.additionalInTargetList.isEmpty())
		{
			event.setSuccess(false);
			for(FileName fileName : result.additionalInTargetList)
			{
				this.getReportingLog().error(MessageFormat.format("{0} is in destination file location, but not source file location", fileName.getFilename()));
			}
		}
		if (! result.missingFromTargetList.isEmpty())
		{
			event.setSuccess(false);
			for(FileName fileName : result.missingFromTargetList)
			{
				this.getReportingLog().error(MessageFormat.format("{0} is in source file location, but not destination file location", fileName.getFilename()));
			}
		}
		if (! result.incomparableList.isEmpty())
		{
			event.setSuccess(false);
			for(FileName fileName : result.incomparableList)
			{
				this.getReportingLog().error(MessageFormat.format("{0} is incomparable", fileName.getFilename()));
			}
		}
		if (! result.fixityMismatchList.isEmpty())
		{
			event.setSuccess(false);
			for(FileName fileName : result.fixityMismatchList)
			{
				this.getReportingLog().error(MessageFormat.format("{0} has a fixity mismatch", fileName.getFilename()));
			}
		}
		*/
		/*
		for(FileInstance fileInstance : filteredFileInstances)
		{
			//Check for matching FileExamination, unadjusting path if moving from non-LC-structured file location to an LC-structured file location			
			FileExamination matchingFileExamination = this.dao.findFileExamination(fileExaminationGroup, fileInstance.getFileName());
			if (matchingFileExamination == null || ! fileInstance.matches(matchingFileExamination))
			{
				event.setSuccess(false);
				throw new Exception(MessageFormat.format("Comparison for {0} failed", fileInstance.getFileName().getFilename()));
			}
		}
		*/
		//Create FileInstances from FileExaminations
		factory.createFileInstancesFromFileExaminations(destFileLocation, fileExaminationGroup.getFileExaminations());
		return event.isSuccess();
	}

	public boolean copy(StorageSystemFileLocation srcFileLocation, StorageSystemFileLocation destFileLocation, Agent requestingAgent, FileFilter fileFilter) throws Exception {
		return this.copy(srcFileLocation, null, destFileLocation, requestingAgent, fileFilter);
		
	}

	protected FileFilter createFileFilter(String className, FileLocation fileLocation) throws Exception
	{
		Class clazz = Class.forName(className);
		FileFilter filter = (FileFilter)clazz.newInstance();
		filter.setFileLocation(fileLocation);
		return filter;
		
	}
	
	public boolean copy(String repositoryId, String packageId, String srcStorageSystemId, String srcBasePath, String destStorageSystemId, String destBasePath, String requestingAgentId, String fileFilterClassName) throws Exception {
		//Find a File Location
		Package packge = dao.findRequiredPackage(Package.class, repositoryId, packageId);		
		StorageSystemFileLocation srcFileLocation = packge.getFileLocation(srcStorageSystemId, srcBasePath);
		if (srcFileLocation == null)
		{
			throw new Exception(MessageFormat.format("File Location for storage system {0} and basepath {1} does not exist", srcStorageSystemId, srcBasePath));
		}
		StorageSystemFileLocation destFileLocation = packge.getFileLocation(destStorageSystemId, destBasePath);
		if (srcFileLocation == null)
		{
			throw new Exception(MessageFormat.format("File Location for storage system {0} and basepath {1} does not exist", destStorageSystemId, destBasePath));
		}
		return this.copy(srcFileLocation, destFileLocation, dao.findRequiredAgent(Agent.class, requestingAgentId), this.createFileFilter(fileFilterClassName, srcFileLocation));
	}

	public boolean copy(String repositoryId, String packageId, String srcBasePath, String srcMountPath, String srcExternalIdentifierValue, String srcExternalIdentifierType, String destStorageSystemId, String destBasePath, String requestingAgentId, String fileFilterClassName) throws Exception {
		//Find a File Location
		Package packge = dao.findRequiredPackage(Package.class, repositoryId, packageId);		
		ExternalFileLocation srcFileLocation = packge.getFileLocation(new ExternalIdentifier(srcExternalIdentifierValue, IdentifierType.valueOf(srcExternalIdentifierType)));
		if (srcFileLocation == null)
		{
			throw new Exception(MessageFormat.format("File Location for identifier {0} of type {1} does not exist", srcExternalIdentifierValue, srcExternalIdentifierType));
		}
		StorageSystemFileLocation destFileLocation = packge.getFileLocation(destStorageSystemId, destBasePath);
		if (srcFileLocation == null)
		{
			throw new Exception(MessageFormat.format("File Location for storage system {0} and basepath {1} does not exist", destStorageSystemId, destBasePath));
		}
		return this.copy(srcFileLocation, srcMountPath, destFileLocation, dao.findRequiredAgent(Agent.class, requestingAgentId), this.createFileFilter(fileFilterClassName, srcFileLocation));
	}
		
}
