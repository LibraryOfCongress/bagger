package gov.loc.repository.bagit;

import gov.loc.repository.results.SimpleResult;

import java.io.File;
import java.util.List;

public interface ManifestGeneratorVerifier {
	public void generateManifest(File packageDir, String algorithm);
	
	public void generateTagManifest(File packageDir, String algorithm);
	
	public SimpleResult verify(File manifest);
	
	public SimpleResult verify(List<File> manifests);	
}
