package gov.loc.repository.bagit.impl;

import java.io.File;
import java.util.List;

import gov.loc.repository.bagit.ManifestGeneratorVerifier;
import gov.loc.repository.results.SimpleResult;

public abstract class AbstractManifestGeneratorVerifier implements
		ManifestGeneratorVerifier {

	@Override
	public SimpleResult verify(List<File> manifests) {
		for(File manifest : manifests)
		{
			SimpleResult result = this.verify(manifest);
			if (! result.isSuccess())
			{
				return result;
			}
		}
		return new SimpleResult(true);
	}

}
