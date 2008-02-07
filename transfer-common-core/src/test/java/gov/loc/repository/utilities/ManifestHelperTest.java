package gov.loc.repository.utilities;


import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

public class ManifestHelperTest {
		
	@Test
	public void testGetAlgorithm() {
		assertEquals("md5", ManifestHelper.getAlgorithm(new File("c:/temp/manifest-md5.txt")));
	}
			
}
