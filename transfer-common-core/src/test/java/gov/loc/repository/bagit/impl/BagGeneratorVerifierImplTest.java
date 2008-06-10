package gov.loc.repository.bagit.impl;

import static org.junit.Assert.*;

import java.io.File;

import gov.loc.repository.bagit.BagGeneratorVerifier;
import gov.loc.repository.utilities.ResourceHelper;

import org.junit.Before;
import org.junit.Test;

public class BagGeneratorVerifierImplTest {

	BagGeneratorVerifier verifier = new BagGeneratorVerifierImpl(null);
	
	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testIsComplete() throws Exception {
		assertTrue(verifier.isComplete(this.getFile("bag_with_one_manifest")).isSuccess());
		assertTrue(verifier.isComplete(this.getFile("bag_with_two_manifests")).isSuccess());
		
		assertFalse(verifier.isComplete(this.getFile("bag_with_no_manifests")).isSuccess());
		verifier.setMissingBagItTolerant(true);
		assertTrue(verifier.isComplete(this.getFile("bag_with_no_bagit")).isSuccess());
		verifier.setMissingBagItTolerant(false);
		assertFalse(verifier.isComplete(this.getFile("bag_with_no_bagit")).isSuccess());
		
		assertFalse(verifier.isComplete(this.getFile("bag_with_no_data_directory")).isSuccess());
		assertFalse(verifier.isComplete(this.getFile("bag_with_extra_directory")).isSuccess());
		assertFalse(verifier.isComplete(this.getFile("bag_with_mismatched_manifests")).isSuccess());
		assertFalse(verifier.isComplete(this.getFile("bag_with_extra_files")).isSuccess());
		
	}

	private File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this, filename);
	}		
	
}
