package gov.loc.repository.utilities;

import static org.junit.Assert.*;

import gov.loc.repository.utilities.PackageHelper;
import gov.loc.repository.utilities.ResourceHelper;

import java.io.File;

import org.junit.Test;

public class PackageHelperTest {
	@Test
	public void testLCPackage() throws Exception {
		File packageDirectory = this.getFile("lcpackage");
		assertTrue(PackageHelper.isLCPackage(packageDirectory));
		assertEquals("manifest-md5.txt", PackageHelper.discoverManifest(packageDirectory).getName());
		assertEquals("batch1", PackageHelper.discoverContentDirectory(packageDirectory).getName());
		assertEquals(PackageHelper.PACKAGE_INFORMATION_DIRECTORY, PackageHelper.discoverPackageInformation(packageDirectory).getName());
		assertTrue(PackageHelper.isInLCPackageRoot(packageDirectory, this.getFile("lcpackage/manifest-md5.txt")));
		assertFalse(PackageHelper.isInLCPackageRoot(packageDirectory, this.getFile("lcpackage/batch1/test1.txt")));
	}

	@Test(expected=Exception.class)
	public void testLCPackageMissingManifest() throws Exception {
		File packageDirectory = this.getFile("lcpackage_missing");
		assertFalse(PackageHelper.isLCPackage(packageDirectory));
		PackageHelper.discoverManifest(packageDirectory);		
	}
	
	@Test(expected=Exception.class)
	public void testLCPackageDuplicateManifest() throws Exception {
		File packageDirectory = this.getFile("lcpackage_multiple_manifest");
		assertFalse(PackageHelper.isLCPackage(packageDirectory));
		PackageHelper.discoverManifest(packageDirectory);		
	}

	@Test(expected=Exception.class)
	public void testLCPackageDuplicateContent() throws Exception {
		File packageDirectory = this.getFile("lcpackage_multiple_content");
		assertFalse(PackageHelper.isLCPackage(packageDirectory));
		PackageHelper.discoverContentDirectory(packageDirectory);		
	}
		
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);
	}		
	
}
