package gov.loc.repository.transfer.components.packaging.impl;


import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;

import gov.loc.repository.transfer.components.AbstractComponentTest;
import gov.loc.repository.transfer.components.packaging.impl.ZipPackager;

import java.io.File;
import java.io.FileNotFoundException;

public class ZipPackagerTest extends AbstractComponentTest {

	private File zipFile;
	private File testFileArray[];
	private ZipPackager packager = new ZipPackager();
	
	@Before
	public void setUp() throws Exception {

		zipFile = this.getFile("test.zip");
		testFileArray = new File[3];
		testFileArray[0] = new File(zipFile.getParent(), "test1.blob");
		testFileArray[1] = new File(zipFile.getParent(), "test2");
		testFileArray[2] = new File(zipFile.getParent(), "test2/test2.blob");
		
		if (! zipFile.exists())
		{
			throw new FileNotFoundException("test.zip is missing: " + zipFile.toString());
		}
	}
	
	@After
	public void tearDown() throws Exception {
		for(int i=0; i < testFileArray.length; i++)
		{
			testFileArray[i].delete();
		}
		
	}

	@Test
	public void unzip() throws Exception {
		
		packager.unpackage(zipFile, zipFile.getParentFile());		
		for(int i=0; i < testFileArray.length; i++)
		{
			assertTrue(testFileArray[i].toString() + " exists", testFileArray[i].exists());
		}		
	}

	//Zip file not found
	@Test(expected=FileNotFoundException.class)
	public void unzipMissingFile() throws Exception {
		
		packager.unpackage(new File("c:\\missing.zip"), zipFile.getParentFile());		
	}
	
	//Null parameters
	@Test(expected=IllegalArgumentException.class)
	public void unzipNullParameter1() throws Exception {
		packager.unpackage(null, zipFile.getParentFile());
	}

	@Test(expected=IllegalArgumentException.class)
	public void unzipNullParameter2() throws Exception {
		packager.unpackage(zipFile, null);
	}
		
}
