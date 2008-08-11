package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.transfer.components.filemanagement.impl.ByFileDirectoryCopier.ByFileCopyFilter;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;

public class CopyAllByFileCopyFilter implements ByFileCopyFilter {

	@Override
	public Map<File, File> filter(CopyDescription copyDescription) {
		
		Map<File,File> fileMap = new HashMap<File, File>();
		for(FileInstance fileInstance : copyDescription.srcFileLocation.getFileInstances())
		{
			//Copy the fileInstance
			File srcFile = new File(copyDescription.srcPath, fileInstance.getFileName().getFilename());
			File destFile = new File(copyDescription.destCopyToPath, fileInstance.getFileName().getFilename());
			fileMap.put(srcFile, destFile);
		}
		return fileMap;
	}

}
