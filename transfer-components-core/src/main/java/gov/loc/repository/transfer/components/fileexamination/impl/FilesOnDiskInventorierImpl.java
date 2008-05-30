package gov.loc.repository.transfer.components.fileexamination.impl;

import java.io.File;
import java.util.Calendar;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

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
import gov.loc.repository.utilities.FixityHelper;
import gov.loc.repository.utilities.PackageHelper;

@Component("filesOnDiskInventorierComponent")
@Scope("prototype")
public class FilesOnDiskInventorierImpl extends AbstractPackageModelerAwareComponent implements
		FilesOnDiskInventorier {

	private FixityHelper fixityHelper;
	
	@Autowired
	public FilesOnDiskInventorierImpl(@Qualifier("modelerFactory")ModelerFactory factory, @Qualifier("packageModelDao")PackageModelDAO dao, @Qualifier("fixityHelper")FixityHelper fixityHelper) {		
		super(factory, dao);
		this.fixityHelper = fixityHelper;
	}
	
	@Override
	protected String getComponentName() {
		return COMPONENT_NAME;
	}

	public void inventory(long fileLocationKey, String mountPath,
			String algorithm, String requestingAgentId) throws Exception {
		this.inventory(this.dao.loadRequiredFileLocation(fileLocationKey), mountPath, Fixity.Algorithm.fromString(algorithm), this.dao.findRequiredAgent(Agent.class, requestingAgentId));

	}

	@SuppressWarnings("unchecked")
	public void inventory(FileLocation fileLocation, String mountPath, Fixity.Algorithm algorithm,
			Agent requestingAgent) throws Exception {
		InventoryFromFilesOnDiskEvent event = this.factory.createFileLocationEvent(InventoryFromFilesOnDiskEvent.class, fileLocation, Calendar.getInstance().getTime(), this.getReportingAgent());
		event.setRequestingAgent(requestingAgent);
		event.setPerformingAgent(this.getReportingAgent());
		
		this.fixityHelper.setAlgorithm(algorithm.getJavaSecurityName());
		
		File dir = new File(fileLocation.getBasePath());
		if (mountPath != null)
		{
			dir = new File(mountPath);
		}
		this.getLog().debug("Directory for inventorying is " + dir.toString());
		Iterator<File> fileIter = FileUtils.iterateFiles(dir, null, true);
		while(fileIter.hasNext())
		{
			File file = fileIter.next();
			if (file.isFile())
			{
				FileName fileName = new FileName(FilenameHelper.removeBasePath(dir.toString(), file.toString()));
				if (fileLocation.isLCPackageStructure() && PackageHelper.isInLCPackageRoot(dir, file))
				{
					this.factory.createFileInstance(fileLocation, fileName);
				}
				else
				{
					String fixityValue = fixityHelper.generateFixity(file);
					this.factory.createFileInstance(fileLocation, fileName, new Fixity(fixityValue, algorithm));
				}
			}
		}
		event.setEventEnd(Calendar.getInstance().getTime());
		this.dao.save(fileLocation);
	}

}
