package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.repository.bagit.bag.BagGeneratorVerifier;
import gov.loc.repository.bagit.bag.BagHelper;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.transfer.components.AbstractPackageModelerAwareComponent;
import gov.loc.repository.utilities.FilenameHelper;
import gov.loc.repository.utilities.ProcessBuilderWrapper;
import gov.loc.repository.utilities.impl.ProcessBuilderWrapperImpl;

public abstract class ConfigurableCopier extends AbstractPackageModelerAwareComponent {

	private BagGeneratorVerifier generator;
	private DirectoryCopier fileCopier;
	private FileCopyVerifier verifier;
	protected ProcessBuilderWrapper pb = new ProcessBuilderWrapperImpl();
	protected CopyDescription copyDescription;
	
	public ConfigurableCopier(ModelerFactory factory, PackageModelDAO dao, String reportingAgentId, BagGeneratorVerifier generator, DirectoryCopier fileCopier, FileCopyVerifier verifier) {
		super(factory, dao, reportingAgentId);
		this.generator = generator;
		this.fileCopier = fileCopier;
		this.verifier = verifier;
		
	}

	protected void internalCopy(FileLocation srcFileLocation, String srcMountPath,
			FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FixityAlgorithm algorithm)
			throws Exception {
		this.internalCopy(srcFileLocation, srcMountPath, destFileLocation, destMountPath, requestingAgent, algorithm, null);
	}
	
	
	protected void internalCopy(FileLocation srcFileLocation, String srcMountPath,
			FileLocation destFileLocation, String destMountPath, Agent requestingAgent, FixityAlgorithm algorithm, Map<String,String> additionalParameters)
			throws Exception {
		this.getLog().debug(MessageFormat.format("Copying from {0} with mount path {1} to {2} with mount path {3}", srcFileLocation, srcMountPath, destFileLocation, destMountPath));
	
		this.copyDescription = new CopyDescription(srcFileLocation, srcMountPath, destFileLocation, destMountPath, additionalParameters);
		
		File destCopyToFile = new File(this.copyDescription.destCopyToPath);
		if (destCopyToFile.exists() && destCopyToFile.listFiles().length != 0)
		{
			throw new RuntimeException(MessageFormat.format("{0} already contains files.  They must be deleted prior to a copy.", this.copyDescription.destCopyToPath));
		}
				
		if (srcFileLocation.isBag() && ! destFileLocation.isBag())
		{
			throw new RuntimeException("Cannot copy from an LC package structure to a non-LC package structure");
		}
				
		Calendar start = Calendar.getInstance();
		
		//Copy
		this.getLog().debug("Performing copy");
		this.fileCopier.copy(this.copyDescription);
		
		//Verify without verify event
		this.getLog().debug("Performing verify");
		if (! this.verifier.verify(copyDescription))
		{
			String msg = MessageFormat.format("Verification of copy from {0} to {1} failed", this.copyDescription.srcFileLocation, this.copyDescription.destFileLocation);
			this.getReportingLog().error(msg);
			throw new Exception(msg);
		}

		if (! srcFileLocation.isBag() && destFileLocation.isBag())
		{
			this.generator.generate(new File(this.copyDescription.destPath), algorithm.toString(), false);
		}

		//Record File Instances
		this.getLog().debug("Recording file instances");
		
		if (! destFileLocation.getFileInstances().isEmpty())
		{
			this.getLog().warn(MessageFormat.format("{0} already has file instances.  Deleting before adding new ones.", destFileLocation));

			this.dao.deleteFileInstances(destFileLocation);
		}
				
		if (destFileLocation.isBag())
		{
			File packageDir = new File(this.copyDescription.destPath);
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
	
		
	public class CopyDescription
	{
		public FileLocation srcFileLocation;
		public String srcMountPath;
		public String srcPath;
		public FileLocation destFileLocation;
		public String destMountPath;
		public String destPath;
		public String destCopyToPath;
        public Map<String,String> additionalParameters;
				
		public CopyDescription(FileLocation srcFileLocation, String srcMountPath,
				FileLocation destFileLocation, String destMountPath, Map<String, String> additionalParameters)
		{
			this.config(srcFileLocation, srcMountPath, destFileLocation, destMountPath, additionalParameters);
		}
		
		private void config(FileLocation srcFileLocation, String srcMountPath,
				FileLocation destFileLocation, String destMountPath, Map<String, String> additionalParameters)
		{
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
			if (additionalParameters != null)
			{
				this.additionalParameters = additionalParameters;
			}
			else
			{
				this.additionalParameters = new HashMap<String, String>();
			}
		}
	}
	
	public interface DirectoryCopier
	{
		void copy(CopyDescription copyDescription);
	}
	
	public interface FileCopyVerifier
	{
		boolean verify(CopyDescription copyDescription);
	}
	
	public interface Chowner
	{
		public static final String USER_KEY = "user";
		public static final String GROUP_KEY = "group";
		
		void changeOwner(CopyDescription copyDescription);
	}
	
	public interface Chmoder
	{
		public static final String DIR_PERMISSIONS_KEY = "dir_permissions";
		public static final String FILE_PERMISSIONS_KEY = "file_permissions";
		
		void changePermissions(CopyDescription copyDescription);
	}
}
