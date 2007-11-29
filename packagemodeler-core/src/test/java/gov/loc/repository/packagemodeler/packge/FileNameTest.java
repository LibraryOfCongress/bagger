package gov.loc.repository.packagemodeler.packge;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.Before;

import gov.loc.repository.packagemodeler.packge.FileName;

import org.apache.commons.io.FilenameUtils;

public class FileNameTest {
	FileName fileName;
	
	@Before
	public void setup()
	{
		fileName = new FileName();
	}
	
	@Test
	public void testSetFileNameUnix() {
		fileName.setFileName("foo/bar.html");
		assertEquals("foo", fileName.getRelativePath());
		assertEquals("bar", fileName.getBaseName());
		assertEquals("html", fileName.getExtension());
		assertTrue(FilenameUtils.equalsNormalized("foo/bar.html", fileName.getFilename()));
	}

	@Test
	public void testSetFileNameWindows() {
		fileName.setFileName("foo\\bar.html");
		assertEquals("foo", fileName.getRelativePath());
		assertEquals("bar", fileName.getBaseName());
		assertEquals("html", fileName.getExtension());
		assertTrue(FilenameUtils.equalsNormalized("foo/bar.html", fileName.getFilename()));
	}
	
	@Test
	public void testSetFileNameNoExtension() {
		fileName.setFileName("foo/bar.");
		assertEquals("foo", fileName.getRelativePath());
		assertEquals("bar", fileName.getBaseName());
		assertNull(fileName.getExtension());
		assertTrue(FilenameUtils.equalsNormalized("foo/bar.", fileName.getFilename()));
	}
	
	
}
