package gov.loc.repository.bagit.impl;

import gov.loc.repository.fixity.impl.JavaSecurityFixityGenerator;

import org.junit.Before;

public class FixityGeneratorManifestGeneratorVerifierTest extends AbstractManifestGeneratorVerifierTest {
			
	@Before
	public void setup() throws Exception {
		canRunTest = true;
		
		generator = new FixityGeneratorManifestGeneratorVerifier(new JavaSecurityFixityGenerator());
		
	}
		
}
