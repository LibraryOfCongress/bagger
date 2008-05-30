package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
//import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Set;
//import java.io.FileFilter;

import org.apache.commons.io.FileUtils;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity.Algorithm;
import gov.loc.repository.transfer.components.AbstractPackageModelerAwareComponent;
import gov.loc.repository.transfer.components.fileexamination.LCManifestGenerator;
import gov.loc.repository.transfer.components.fileexamination.Verifier;
import gov.loc.repository.utilities.FilenameHelper;
import gov.loc.repository.utilities.ManifestReader;
import gov.loc.repository.utilities.PackageHelper;
//import gov.loc.repository.transfer.components.filemanagement.transport.*;

public abstract class AbstractLCPackageCopier extends AbstractPackageModelerAwareComponent {

	private LCManifestGenerator generator;
	
	public AbstractLCPackageCopier(ModelerFactory factory, PackageModelDAO dao, LCManifestGenerator generator) {
		super(factory, dao);
		this.generator = generator;
	}
	
	protected void copy(FileLocation srcFileLocation, String srcMountPath,
			FileLocation destFileLocation, String destMountPath, Agent requestingAgent, Verifier verifier, Algorithm algorithm)
			throws Exception {	

		if (!srcFileLocation.isLCPackageStructure() || !destFileLocation.isLCPackageStructure())
		{
			throw new Exception("Both the source and destination file locations must be LC package structure.");
		}
		Set<FileInstance> fileInstances = srcFileLocation.getFileInstances();
		/*
        List<FileInstance> filteredFileInstances = new ArrayList<FileInstance>();
        for(FileInstance fileInstance : srcFileLocation.getFileInstances())
		{
			if (fileFilter == null || fileFilter.accept(new File(fileInstance.getFileName().getFilename())))
			{
				filteredFileInstances.add(fileInstance);
			}
		}
        */
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
		
		for(FileInstance fileInstance : fileInstances)
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

		if (! srcFileLocation.isLCPackageStructure() && destFileLocation.isLCPackageStructure())
		{
			this.generator.generate(destFileLocation, destMountPath, algorithm, requestingAgent);
		}

		//Record File Instances
		if (destFileLocation.isLCPackageStructure())
		{
			//Load from manifest
			File packageDir = new File(destFileLocation.getBasePath());
			if (destMountPath != null)
			{
				packageDir = new File(destMountPath);
			}			
	        File manifestFile = PackageHelper.discoverManifest(packageDir);
	        ManifestReader reader = new ManifestReader();
	        reader.setFile(manifestFile);
	        factory.createFileInstances(destFileLocation, reader);
	        
            List<File> fileList = PackageHelper.discoverLCPackageRootFiles(packageDir);
            for(File file : fileList)
            {
                String filename = FilenameHelper.removeBasePath(packageDir.toString(), file.toString());
                factory.createFileInstance(destFileLocation, new FileName(filename));
            }            
		}
		dao.save(destFileLocation);
		
		//Create the File Copy Event
		FileCopyEvent event = this.factory.createFileLocationEvent(FileCopyEvent.class, destFileLocation, start.getTime(), this.getReportingAgent());
		event.setEventEnd(Calendar.getInstance().getTime());
		event.setPerformingAgent(this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setFileLocationSource(srcFileLocation);
		dao.save(event);		
	}
}
