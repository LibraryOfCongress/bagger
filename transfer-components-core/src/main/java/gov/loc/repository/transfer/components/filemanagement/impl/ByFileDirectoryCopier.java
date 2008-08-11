package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.Chmoder;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.Chowner;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.DirectoryCopier;

public class ByFileDirectoryCopier implements DirectoryCopier {

	private static final Log log = LogFactory.getLog(ByFileDirectoryCopier.class);
	
	protected FileCopier fileCopier;
	protected ByFileCopyFilter filter;
	protected Chmoder chmoder;
	protected Chowner chowner;
	
	public ByFileDirectoryCopier(ByFileCopyFilter filter, FileCopier fileCopier, Chmoder chmoder, Chowner chowner) {
		this.fileCopier = fileCopier;
		this.filter = filter;
	}

	@Override
	public void copy(CopyDescription copyDescription) {
		Map<File, File> fileMap = this.filter.filter(copyDescription);
		for(File srcFile : fileMap.keySet())
		{
			File destFile = fileMap.get(srcFile);
			log.debug(MessageFormat.format("Performing copy from {0} to {1}", srcFile, destFile));
			this.fileCopier.copy(srcFile, destFile);
			
		}
		if (chowner != null && copyDescription.additionalParameters.containsKey(Chowner.GROUP_KEY) && copyDescription.additionalParameters.containsKey(Chowner.USER_KEY))
		{
			chowner.changeOwner(copyDescription);
		}
		
		if (chmoder != null && copyDescription.additionalParameters.containsKey(Chmoder.DIR_PERMISSIONS_KEY) && copyDescription.additionalParameters.containsKey(Chmoder.FILE_PERMISSIONS_KEY))
		{
			chmoder.changePermissions(copyDescription);
		}
		
	}
			
	public interface FileCopier
	{
		void copy(File srcFile, File destFile);	
	}
	
	public interface ByFileCopyFilter
	{
		Map<File, File> filter(CopyDescription copyDescription);
	}
}
