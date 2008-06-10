package gov.loc.repository.bagit;

import static org.junit.Assert.*;

import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.ManifestWriter;
import gov.loc.repository.utilities.ResourceHelper;

import java.io.File;

import org.junit.Test;

public class ManifestWriterTest {	

	@Test
	public void testWriteManifest() throws Exception {
		ManifestWriter writer = new ManifestWriter(BagHelper.getManifest(this.getFile("manifestwriter.txt").getParentFile(), "md5"));
		writer.write("batch1\\dir1\\test3.txt", "8ad8757baa8564dc136c1e07507f4a98");
		writer.write("batch1\\test2.txt", "ad0234829205b9033196ba818f7a872b");
		writer.close();
		
		ManifestReader reader = new ManifestReader(writer.getManifestFile());
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
				
	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);
	}		
	
}
