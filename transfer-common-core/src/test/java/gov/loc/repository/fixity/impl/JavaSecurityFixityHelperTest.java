package gov.loc.repository.fixity.impl;

import static org.junit.Assert.*;

import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.fixity.FixityGenerator;
import gov.loc.repository.fixity.impl.JavaSecurityFixityGenerator;
import gov.loc.repository.utilities.ResourceHelper;

import org.junit.Test;

public class JavaSecurityFixityHelperTest {

	@Test
	public void testGenerateFixity() throws Exception {
		FixityGenerator fixityHelper = new JavaSecurityFixityGenerator();
		assertTrue(fixityHelper.canGenerate(FixityAlgorithm.MD5));
		assertEquals("5a105e8b9d40e1329780d62ea2265d8a", fixityHelper.generateFixity(ResourceHelper.getFile(this, "test1.txt"), FixityAlgorithm.MD5));		
	}

	
	
}
