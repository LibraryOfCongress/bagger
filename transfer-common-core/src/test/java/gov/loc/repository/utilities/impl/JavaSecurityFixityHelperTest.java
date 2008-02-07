package gov.loc.repository.utilities.impl;

import static org.junit.Assert.*;

import gov.loc.repository.utilities.FixityHelper;
import gov.loc.repository.utilities.ResourceHelper;

import org.junit.Test;

public class JavaSecurityFixityHelperTest {

	@Test
	public void testGenerateFixity() throws Exception {
		FixityHelper fixityHelper = new JavaSecurityFixityHelper();
		fixityHelper.setAlgorithm("md5");
		assertEquals("5a105e8b9d40e1329780d62ea2265d8a", fixityHelper.generateFixity(ResourceHelper.getFile(this, "test1.txt")));
	}

	
	
}
