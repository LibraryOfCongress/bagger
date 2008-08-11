package gov.loc.repository.utilities;

import static org.junit.Assert.*;

import org.junit.Test;

public class FilenameHelperTest {

	@Test
	public void testRemoveBasePath() throws Exception {
		assertEquals("bar.html", FilenameHelper.removeBasePath("/foo", "/foo/bar.html"));
		assertEquals("bar.html", FilenameHelper.removeBasePath("c:/foo", "c:\\foo\\bar.html"));
	}

	@Test(expected=Exception.class)
	public void testRemoveBadBasePath() throws Exception {
		FilenameHelper.removeBasePath("/xfoo", "/foo/bar.html");
	}
	
	@Test
	public void testGetRoot() throws Exception
	{
		assertEquals("foo", FilenameHelper.getRoot("foo/bar/foobar.txt"));
		assertNull(FilenameHelper.getRoot("foobar.txt"));
	}
}
