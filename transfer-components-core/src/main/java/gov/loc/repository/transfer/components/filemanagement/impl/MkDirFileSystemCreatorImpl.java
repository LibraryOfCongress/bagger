package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.io.FileSystemUtils;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;

import gov.loc.repository.transfer.components.AbstractComponent;
import gov.loc.repository.transfer.components.filemanagement.FileSystemCreator;

@Component("mkDirFileSystemCreator")
public class MkDirFileSystemCreatorImpl extends AbstractComponent implements
		FileSystemCreator {

	@Override
	protected String getComponentName() {
		return COMPONENT_NAME;
	}

	@Override
	public void create(String mountPath, Long size) throws Exception {
		
		File mountFile = new File(mountPath);
		File parentFile = mountFile.getParentFile();
		while(parentFile != null && ! parentFile.exists())
		{
			parentFile = parentFile.getParentFile();
		}
		if (parentFile == null)
		{
			throw new RuntimeException(MessageFormat.format("Unable to determine available space for {0}", mountPath));
		}
		
		Long free = FileSystemUtils.freeSpaceKb(parentFile.toString()) * 1024;
		
		if (free < size)
		{
			throw new RuntimeException(MessageFormat.format("{0} space is needed, but only {1} is available.", size, free));
		}
		
		FileUtils.forceMkdir(mountFile);
		
	}

}
