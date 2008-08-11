package gov.loc.repository.transfer.components.filemanagement.impl;

import static org.junit.Assert.*;
import gov.loc.repository.transfer.components.filemanagement.FileSystemCreator;
import gov.loc.repository.utilities.ResourceHelper;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

public class MkDirFileSystemCreatorImplTest {

	File mountFile;
	FileSystemCreator creator = new MkDirFileSystemCreatorImpl();
	
	
	@Before
	public void setUp() throws Exception {
		mountFile = new File(this.getFile("dummy.txt").getParent() + File.separator + "mkdirtest");
		if (mountFile.exists())
		{
			FileUtils.forceDelete(mountFile);
		}
	}

	@Test
	public void testCreate() throws Exception {
		creator.create(mountFile.toString(), 100L);
		assertTrue(mountFile.exists());
	}

	protected File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this.getClass(), filename);
	}
	
}
