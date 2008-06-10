package gov.loc.repository.bagit;


import static org.junit.Assert.*;

import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.utilities.ResourceHelper;

import java.io.File;

import org.junit.Test;

public class ManifestHelperTest {
		
	@Test
	public void testGetAlgorithm() {
		assertEquals("md5", ManifestHelper.getAlgorithm(new File("c:/temp/manifest-md5.txt")));
		assertEquals("md5", ManifestHelper.getAlgorithm(new File("c:/temp/tagmanifest-md5.txt")));

	}

	@Test(expected=RuntimeException.class)
	public void testGetMissingAlgorithm() {
		ManifestHelper.getAlgorithm(new File("c:/temp/manifest.txt"));
	}

	
	@Test
	public void testIsManifest() throws Exception
	{
		assertTrue(ManifestHelper.isManifest(this.getFile("bag/manifest-md5.txt")));
		assertFalse(ManifestHelper.isManifest(this.getFile("bag/tagmanifest-sha1.txt")));
	}
	
	@Test
	public void testIsTagManifest() throws Exception
	{
		assertFalse(ManifestHelper.isTagManifest(this.getFile("bag/manifest-md5.txt")));
		assertTrue(ManifestHelper.isTagManifest(this.getFile("bag/tagmanifest-sha1.txt")));
				
	}
		
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);
	}			
	
	
}
