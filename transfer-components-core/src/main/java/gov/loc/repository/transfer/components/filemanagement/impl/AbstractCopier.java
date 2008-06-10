package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.List;

import gov.loc.repository.bagit.BagGeneratorVerifier;
import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.transfer.components.AbstractPackageModelerAwareComponent;
import gov.loc.repository.utilities.FilenameHelper;

public abstract class AbstractCopier extends AbstractPackageModelerAwareComponent {

	
	
	private BagGeneratorVerifier generator;
	protected FileLocation srcFileLocation;
	protected String srcMountPath;
	protected String srcPath;
	protected FileLocation destFileLocation;
	protected String destMountPath;
	protected String destPath;
	protected String destCopyToPath;
	
	public AbstractCopier(ModelerFactory factory, PackageModelDAO dao, BagGeneratorVerifier generator) {
		super(factory, dao);
		this.generator = generator;
	}
		
	protected abstract void performCopy() throws Exception;

	protected abstract boolean performVerify() throws Exception;
	
	protected void internalCopy(FileLocation srcFileLocation, String srcMountPath,
			FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FixityAlgorithm algorithm)
			throws Exception {
		this.getLog().debug(MessageFormat.format("Copying from {0} with mount path {1} to {2} with mount path {3}", srcFileLocation, srcMountPath, destFileLocation, destMountPath));
		
		this.srcFileLocation = srcFileLocation;
		this.srcMountPath = srcMountPath;
		this.srcPath = srcMountPath != null ? srcMountPath : srcFileLocation.getBasePath();
		this.destFileLocation = destFileLocation;
		this.destMountPath = destMountPath;
		this.destPath = destMountPath != null ? destMountPath : destFileLocation.getBasePath();
		this.destCopyToPath = destPath;
		if (! srcFileLocation.isBag() && destFileLocation.isBag())
		{
			//Adjust the destDir
			this.destCopyToPath = FilenameHelper.concat(this.destPath, BagHelper.DATA_DIRECTORY);
		}
				
		if (srcFileLocation.isBag() && ! destFileLocation.isBag())
		{
			throw new Exception("Cannot copy from an LC package structure to a non-LC package structure");
		}

		Calendar start = Calendar.getInstance();
		

		//Copy
		this.getLog().debug("Permorming copy");
		this.performCopy();
		
		//Verify without verify event
		this.getLog().debug("Permorming verify");
		if (! this.performVerify())
		{
			String msg = MessageFormat.format("Verification of copy from {0} to {1} failed", srcFileLocation.toString(), destFileLocation.toString());
			this.getReportingLog().error(msg);
			throw new Exception(msg);
		}

		if (! srcFileLocation.isBag() && destFileLocation.isBag())
		{
			this.generator.generate(new File(this.destPath), algorithm.toString(), false);
		}

		//Record File Instances
		this.getLog().debug("Recording file instances");
		if (destFileLocation.isBag())
		{
			File packageDir = new File(this.destPath);
			//Load from manifest
			factory.createFileInstancesFromBagManifests(destFileLocation, BagHelper.getManifests(packageDir));
			List<File> tagList = BagHelper.getTags(packageDir, true);
            for(File file : tagList)
            {
                String filename = FilenameHelper.removeBasePath(packageDir.toString(), file.toString());
                factory.createFileInstance(destFileLocation, new FileName(filename));
            }            
		}
		else
		{
			this.addFileInstancesForNonLCPackageStructureFileLocation(destFileLocation, destMountPath);
		}
		dao.save(destFileLocation);
		
		//Create the File Copy Event
		this.getLog().debug("Creating File Copy Event");
		FileCopyEvent event = this.factory.createFileLocationEvent(FileCopyEvent.class, destFileLocation, start.getTime(), this.getReportingAgent());
		event.setEventEnd(Calendar.getInstance().getTime());
		event.setPerformingAgent(this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setFileLocationSource(srcFileLocation);
		dao.save(event);		
	}
	
	protected void addFileInstancesForNonLCPackageStructureFileLocation(FileLocation destFileLocation, String destMountPath)
	{
		throw new RuntimeException("Destination File Location is not LC-package structured and method not provided to create File Instances");
	}
}
