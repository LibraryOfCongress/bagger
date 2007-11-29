package gov.loc.repository.utilities.impl;

import static org.junit.Assert.*;

import gov.loc.repository.utilities.ClassHelper;

import org.junit.Test;

public class ClassHelperTest {

	@Test
	public void testGetClasses() throws Exception {
		assertEquals(2, ClassHelper.getClasses("gov.loc.repository.utilities.classes", false).size());
	}

	@Test
	public void testGetClassesRecursive() throws Exception {
		assertEquals(3, ClassHelper.getClasses("gov.loc.repository.utilities.classes", true).size());
	}
	
}
