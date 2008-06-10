package gov.loc.repository.bagit;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.utilities.ResourceHelper;

import java.io.File;

import org.junit.Test;

public class BagTest {
	@Test
	public void testBag() throws Exception {
		File packageDir = this.getFile("bag");
		assertEquals(this.getFile("bag/bagit.txt"), BagHelper.getBagIt(packageDir));
		assertEquals(this.getFile("bag/" + BagHelper.DATA_DIRECTORY), BagHelper.getDataDirectory(packageDir));
		assertEquals(this.getFile("bag/manifest-md5.txt"), BagHelper.getManifest(packageDir, "md5"));
		assertEquals(2, BagHelper.getManifests(packageDir).size());
		assertEquals(5, BagHelper.getTags(packageDir, true).size());
		assertEquals(2, BagHelper.getTags(packageDir, false).size());
		assertTrue(BagHelper.isTag(packageDir, this.getFile("bag/package-info.txt")));
	}

	public void testBagMissingManifest() throws Exception {
		File packageDir = this.getFile("bag_missing");
		assertEquals(0, BagHelper.getManifests(packageDir).size());
	}
			
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);
	}		
	
}
