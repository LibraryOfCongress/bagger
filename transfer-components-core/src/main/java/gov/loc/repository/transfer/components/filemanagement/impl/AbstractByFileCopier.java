package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Set;

import gov.loc.repository.bagit.BagGeneratorVerifier;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.packge.FileInstance;

public abstract class AbstractByFileCopier extends AbstractCopier {

	
	public AbstractByFileCopier(ModelerFactory factory, PackageModelDAO dao, BagGeneratorVerifier generator) {
		super(factory, dao, generator);
	}

	protected Set<FileInstance> performFilter()
	{
		return srcFileLocation.getFileInstances();
	}
	
	
	@Override
	protected void performCopy() throws Exception
	{
		Set<FileInstance> filteredFileInstances = this.performFilter();
		//TODO:  Change so that gets files from File System, not Package Modeler
		
		for(FileInstance fileInstance : filteredFileInstances)
		{
			//Copy the fileInstance
			File srcFile = new File(srcPath, fileInstance.getFileName().getFilename());
			File destFile = new File(destCopyToPath, fileInstance.getFileName().getFilename());
			this.getLog().debug(MessageFormat.format("Performing copy from {0} to {1}", srcFile, destFile));
			this.performFileCopy(srcFile, destFile);
		}		
	}
	
	protected abstract void performFileCopy(File srcFile, File destFile) throws Exception;
}
