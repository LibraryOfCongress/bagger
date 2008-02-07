package gov.loc.repository.utilities;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ManifestReaderTest {	

	@Test
	public void testReadManifest() throws Exception {
		ManifestReader reader = new ManifestReader();
		reader.setFile(this.getFile("lcpackage/manifest-md5.txt"));
		assertTrue(reader.hasNext());
		ManifestReader.FileFixity fileFixity = reader.next();
		assertEquals("batch1\\dir1\\test3.txt", fileFixity.getFile());
		assertEquals("8ad8757baa8564dc136c1e07507f4a98", fileFixity.getFixityValue());
		reader.next();
		reader.next();
		reader.next();
		fileFixity = reader.next();
		assertEquals("batch1\\test2.txt", fileFixity.getFile());
		assertEquals("ad0234829205b9033196ba818f7a872b", fileFixity.getFixityValue());
		assertEquals("md5", reader.getAlgorithm());
	}

	@Test
	public void testMissingManifest() throws Exception {
		ManifestReader reader = new ManifestReader();		
		assertFalse(reader.hasNext());
	}
	
			
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);

	}		
	
}
