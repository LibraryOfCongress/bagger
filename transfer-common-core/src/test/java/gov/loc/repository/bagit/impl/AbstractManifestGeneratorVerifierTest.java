package gov.loc.repository.bagit.impl;

import java.io.File;
import java.io.FileWriter;
import java.io.Writer;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.bagit.ManifestGeneratorVerifier;
import gov.loc.repository.utilities.ResourceHelper;

public abstract class AbstractManifestGeneratorVerifierTest {

	boolean canRunTest = false;

	ManifestGeneratorVerifier generator;

	@Test
	public void testGenerateAndValidateManifest() throws Exception
	{
		if (! this.canRunTest)
		{
			return;
		}
		
		File packageDir = this.getFile("bag");
		File manifestFile = BagHelper.getManifest(packageDir, "md5");
		if (manifestFile.exists())
		{
			FileUtils.forceDelete(manifestFile);
		}
		assertFalse(manifestFile.exists());
		
		generator.generateManifest(packageDir, "md5");

		
		assertTrue(manifestFile.exists());

		assertTrue(generator.verify(manifestFile).isSuccess());

		//Add a bad line
		Writer manifestWriter = new FileWriter(manifestFile, true);
		manifestWriter.write("ad0234829205b9033196ba818f7a872c  data\\batch1\\test3.txt");
		manifestWriter.close();

		assertFalse(generator.verify(manifestFile).isSuccess());
		
	}

	@Test
	public void testGenerateAndValidateTagManifest() throws Exception
	{
		if (! this.canRunTest)
		{
			return;
		}
		
		File packageDir = this.getFile("bag");
		File tagManifestFile = BagHelper.getTagManifest(packageDir, "md5");
		if (tagManifestFile.exists())
		{
			FileUtils.forceDelete(tagManifestFile);
		}
		assertFalse(tagManifestFile.exists());
		generator.generateTagManifest(packageDir, "md5");

		assertTrue(tagManifestFile.exists());

		assertTrue(generator.verify(tagManifestFile).isSuccess());

		//Add a bad line
		Writer manifestWriter = new FileWriter(tagManifestFile, true);
		manifestWriter.write("ad0234829205b9033196ba818f7a872c  test3.txt");
		manifestWriter.close();

		assertFalse(generator.verify(tagManifestFile).isSuccess());
		
	}
	
	
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);
	}			
	
}
