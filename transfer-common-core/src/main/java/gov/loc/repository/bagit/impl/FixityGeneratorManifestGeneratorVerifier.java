package gov.loc.repository.bagit.impl;

import java.io.File;
import java.text.MessageFormat;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;

import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.bagit.ManifestHelper;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.bagit.ManifestWriter;
import gov.loc.repository.bagit.ManifestReader.FileFixity;
import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.fixity.FixityGenerator;
import gov.loc.repository.results.SimpleResult;
import gov.loc.repository.utilities.FilenameHelper;

public class FixityGeneratorManifestGeneratorVerifier extends AbstractManifestGeneratorVerifier {

	private FixityGenerator fixityGenerator;
	
	public FixityGeneratorManifestGeneratorVerifier(FixityGenerator fixityGenerator) {
		this.fixityGenerator = fixityGenerator;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void generateManifest(File packageDir, String algorithmName) {
		
		FixityAlgorithm algorithm = this.getFixityAlgorithm(algorithmName);
		ManifestWriter writer = new ManifestWriter(BagHelper.getManifest(packageDir, algorithmName));
		try
		{
			Iterator<File> iter = FileUtils.iterateFiles(BagHelper.getDataDirectory(packageDir), null, true);
			while(iter.hasNext())
			{
				File file = iter.next();
				String fixityValue = this.fixityGenerator.generateFixity(file, algorithm);
				writer.write(FilenameHelper.removeBasePath(packageDir.toString(), file.toString()), fixityValue);
			}
		}
		finally
		{
			writer.close();
		}
				
	}

	private FixityAlgorithm getFixityAlgorithm(String algorithmName)
	{
		FixityAlgorithm algorithm = FixityAlgorithm.fromString(algorithmName);
		if (! this.fixityGenerator.canGenerate(algorithm))
		{
			throw new RuntimeException("Fixity Generator cannot generate " + algorithm);
		}
		return algorithm;
	}
	
	@Override
	public SimpleResult verify(File manifest) {
		ManifestReader reader = new ManifestReader(manifest);
		String message = "";
		FixityAlgorithm algorithm = this.getFixityAlgorithm(ManifestHelper.getAlgorithm(manifest));
		while(reader.hasNext())
		{
			FileFixity fileFixity = reader.next();
			File file = new File(manifest.getParentFile(), fileFixity.getFile());
			if (! file.exists())
			{
				message += MessageFormat.format("File {0} not found", fileFixity.getFile());
			}
			else if (! this.fixityGenerator.fixityMatches(file, algorithm, fileFixity.getFixityValue()))
			{
				message += MessageFormat.format("File {0} with fixity {1} does not match generated fixity", fileFixity.getFile(), fileFixity.getFixityValue());
			}
		}
		if (message.length() > 0)
		{
			return new SimpleResult(false, message);
		}
		return new SimpleResult(true);
	}
	
	@Override
	public void generateTagManifest(File packageDir, String algorithmName) {
		FixityAlgorithm algorithm = this.getFixityAlgorithm(algorithmName);
		ManifestWriter writer = new ManifestWriter(BagHelper.getTagManifest(packageDir, algorithmName));
		try
		{
			List<File> tagFiles = BagHelper.getTags(packageDir, false);
			for(File file : tagFiles)
			{
				String fixityValue = this.fixityGenerator.generateFixity(file, algorithm);
				writer.write(FilenameHelper.removeBasePath(packageDir.toString(), file.toString()), fixityValue);
			}
		}
		finally
		{
			writer.close();
		}
		
	}
	
}
