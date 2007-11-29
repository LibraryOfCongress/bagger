package gov.loc.repository.utilities;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ManifestWriterTest {	

	@Test
	public void testWriteManifest() throws Exception {
		ManifestWriter writer = new ManifestWriter();
		writer.setParentPath(this.getParentFile());
		writer.setAlgorithm("md5");
		writer.write("batch1\\dir1\\test3.txt", "8ad8757baa8564dc136c1e07507f4a98");
		writer.write("batch1\\test2.txt", "ad0234829205b9033196ba818f7a872b");
		writer.close();
		
		ManifestReader reader = new ManifestReader();
		reader.setFile(writer.getManifestFile());
		assertTrue(reader.hasNext());
		ManifestReader.FileFixity fileFixity = reader.next();
		assertEquals("batch1\\dir1\\test3.txt", fileFixity.getFile());
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", fileFixity.getFixityValue());
		fileFixity = reader.next();
		assertEquals("batch1\\test2.txt", fileFixity.getFile());
		assertEquals("ad0234829205b9033196ba818f7a872b", fileFixity.getFixityValue());
		assertEquals("md5", reader.getAlgorithm());
		reader.close();
		writer.getManifestFile().delete();
	}

	@Test(expected=Exception.class)
	public void testMissingAlgorithm() throws Exception {
		ManifestWriter writer = new ManifestWriter();		
		writer.setParentPath(this.getParentFile());
		writer.write("foo", "bar");
	}

	@Test(expected=Exception.class)
	public void testMissingParentPath() throws Exception {
		ManifestWriter writer = new ManifestWriter();		
		writer.setAlgorithm("md5");
		writer.write("foo", "bar");
	}
	
			
	private File getParentFile() throws Exception
	{
		String resourceName = "gov/loc/repository/utilities/lcpackage/manifest-md5.txt";
		return new File(this.getClass().getClassLoader().getResource(resourceName).toURI()).getParentFile().getParentFile();
	}		
	
}
