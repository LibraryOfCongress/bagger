package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.io.FileUtils;

import gov.loc.repository.bagit.BagGeneratorVerifier;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

public abstract class AbstractLocalByFileCopier extends AbstractByFileCopier {

	public AbstractLocalByFileCopier(ModelerFactory factory, PackageModelDAO dao, BagGeneratorVerifier generator ) {
		super(factory, dao, generator);
	}

	@Override
	protected void performFileCopy(File srcFile, File destFile) throws Exception
	{
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
}
