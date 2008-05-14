package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.io.FileFilter;

import org.apache.commons.io.FileUtils;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.transfer.components.BaseComponent;
import gov.loc.repository.transfer.components.fileexamination.Verifier;
import gov.loc.repository.utilities.PackageHelper;

public abstract class AbstractFileCopier extends BaseComponent {
		
	public AbstractFileCopier(ModelerFactory factory, PackageModelDAO dao) {
		super(factory, dao);
	}
	
	protected void copy(FileLocation srcFileLocation, String srcMountPath,
			FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FileFilter fileFilter, Verifier verifier)
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
		File destDir = new File(destFileLocation.getBasePath());
		if (destMountPath != null)
		{
			destDir = new File(destMountPath);
		}
		if (! srcFileLocation.isLCPackageStructure() && destFileLocation.isLCPackageStructure())
		{
			//Adjust the destFile
			destDir = new File(destDir, PackageHelper.CONTENT_DIRECTORY);
		}
		
		for(FileInstance fileInstance : filteredFileInstances)
		{
			//Copy the fileInstance
			File srcFile = new File(srcPath, fileInstance.getFileName().getFilename());
			File destFile = new File(destDir, fileInstance.getFileName().getFilename());
			try
			{
				this.getLog().debug(MessageFormat.format("Copying {0} to {1}", srcFile.toString(), destFile.toString()));
				FileUtils.copyFile(srcFile, destFile);
			}
			catch(Exception ex)
			{
				this.getReportingLog().error(MessageFormat.format("An error occurred copying from {0} to {1}: {2}", srcFile.toString(), destFile.toString(), ex.getMessage()));
				throw ex;
				
			}
		}
		//Verify without verify event
		if (! verifier.verify(destFileLocation, destMountPath))
		{
			String msg = MessageFormat.format("Verification of copy from {0} to {1} failed", srcFileLocation.toString(), destFileLocation.toString());
			this.getReportingLog().error(msg);
			throw new Exception(msg);
		}
		
		//Create the File Copy Event
		FileCopyEvent event = this.factory.createFileLocationEvent(FileCopyEvent.class, destFileLocation, start.getTime(), this.getReportingAgent());
		event.setEventEnd(Calendar.getInstance().getTime());
		event.setPerformingAgent(this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setFileLocationSource(srcFileLocation);
		dao.save(event);		
	}

}
