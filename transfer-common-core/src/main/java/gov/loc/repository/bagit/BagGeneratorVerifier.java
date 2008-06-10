package gov.loc.repository.bagit;

import gov.loc.repository.results.SimpleResult;

import java.io.File;

public interface BagGeneratorVerifier {

	public SimpleResult isValid(File packageDir, boolean verifyTagManifests);
	
	public SimpleResult isComplete(File packageDir);
	
	public void generate(File packageDir, String algorithm, boolean generateTagManifest);
	
	public void setMissingBagItTolerant(boolean isTolerant);
	
	public boolean isMissingBagItTolerant();
	
	
}
