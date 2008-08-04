package gov.loc.repository.transfer.components.fileexamination.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;

import gov.loc.repository.bagit.bag.BagHelper;
import gov.loc.repository.bagit.manifest.FixityGenerator;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromFilesOnDiskEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.transfer.components.AbstractPackageModelerAwareComponent;
import gov.loc.repository.transfer.components.fileexamination.FilesOnDiskInventorier;
import gov.loc.repository.utilities.FilenameHelper;

public class FilesOnDiskInventorierImpl extends AbstractPackageModelerAwareComponent implements
		FilesOnDiskInventorier {

	private FixityGenerator fixityGenerator;
	
	@Autowired
	public FilesOnDiskInventorierImpl(ModelerFactory factory, PackageModelDAO dao, String reportingAgentId, FixityGenerator fixityGenerator) {		
		super(factory, dao, reportingAgentId);
		this.fixityGenerator = fixityGenerator;
	}
	
	@Override
	protected String getComponentName() {
		return COMPONENT_NAME;
	}

	public void inventory(long fileLocationKey, String mountPath,
			String algorithm, String requestingAgentId) throws Exception {
		this.inventory(this.dao.loadRequiredFileLocation(fileLocationKey), mountPath, FixityAlgorithm.fromString(algorithm), this.dao.findRequiredAgent(Agent.class, requestingAgentId));

	}

	@SuppressWarnings("unchecked")
	public void inventory(FileLocation fileLocation, String mountPath, FixityAlgorithm algorithm,
			Agent requestingAgent) throws Exception {
		InventoryFromFilesOnDiskEvent event = this.factory.createFileLocationEvent(InventoryFromFilesOnDiskEvent.class, fileLocation, Calendar.getInstance().getTime(), this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setPerformingAgent(this.getReportingAgent());
		
		File dir = new File(fileLocation.getBasePath());
		if (mountPath != null)
		{
			dir = new File(mountPath);
		}
		this.getLog().debug("Directory for inventorying is " + dir.toString());
		if (! dir.exists() || ! dir.isDirectory())
		{
			throw new RuntimeException(dir.toString() + " does not exist or is not a directory");
		}
		
		if (! fileLocation.getFileInstances().isEmpty())
		{
			this.getLog().warn(MessageFormat.format("{0} already has file instances.  Deleting before adding new ones.", fileLocation));

			this.dao.deleteFileInstances(fileLocation);
		}
		
		
		Iterator<File> fileIter = FileUtils.iterateFiles(dir, null, true);
		while(fileIter.hasNext())
		{
			File file = fileIter.next();
			if (file.isFile())
			{
				FileName fileName = new FileName(FilenameHelper.removeBasePath(dir.toString(), file.toString()));
				if (fileLocation.isBag() && BagHelper.isTag(dir, file))
				{
					this.factory.createFileInstance(fileLocation, fileName);
				}
				else
				{
					String fixityValue = fixityGenerator.generateFixity(file, algorithm.toString());
					this.factory.createFileInstance(fileLocation, fileName, new Fixity(fixityValue, algorithm));
				}
			}
		}
		event.setEventEnd(Calendar.getInstance().getTime());
		this.dao.save(fileLocation);
	}

}
