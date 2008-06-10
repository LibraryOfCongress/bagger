package gov.loc.repository.bagit.impl;

import java.io.File;
import java.io.FileWriter;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.BagHelper;
import gov.loc.repository.bagit.BagGeneratorVerifier;
import gov.loc.repository.bagit.ManifestGeneratorVerifier;
import gov.loc.repository.bagit.ManifestReader;
import gov.loc.repository.results.SimpleResult;
import gov.loc.repository.utilities.FilenameHelper;

public class BagGeneratorVerifierImpl implements BagGeneratorVerifier {

	public static final String VERSION = "0.95";
	
	private static final Log log = LogFactory.getLog(BagGeneratorVerifierImpl.class);
	private ManifestGeneratorVerifier verifier;
	private boolean isMissingBagItTolerant = false;
	
	public BagGeneratorVerifierImpl(ManifestGeneratorVerifier verifier) {
		this.verifier = verifier;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public SimpleResult isComplete(File packageDir) {
		//Is at least one manifest
		List<File> manifestList = BagHelper.getManifests(packageDir);
		if (manifestList.isEmpty())
		{
			String message = MessageFormat.format("Bag at {0} does not have any manifests", packageDir); 
			log.info(message);
			return new SimpleResult(false, message);
		}
		//Has bagit file
		if (! this.isMissingBagItTolerant && ! BagHelper.getBagIt(packageDir).exists())
		{
			String message = MessageFormat.format("Bag at {0} does not have bagit.txt", packageDir);
			log.info(message);
			return new SimpleResult(false, message);
		}
		//Has data directory
		if (! BagHelper.getDataDirectory(packageDir).exists())
		{
			String message = MessageFormat.format("Bag at {0} does not have data directory", packageDir);
			log.info(message);
			return new SimpleResult(false, message);
		}
		//Has no other directory
		int dirCount = 0;
		for(File file : packageDir.listFiles())
		{
			if (file.isDirectory())
			{
				dirCount++;
			}
		}
		if (dirCount > 1)
		{
			String message = MessageFormat.format("Bag at {0} has extra directories", packageDir);
			log.info(message);
			return new SimpleResult(false, message);
		}
		
		File baseManifest = manifestList.get(0);
		Collection<String> baseFiles = this.readManifest(baseManifest);		
		
		//Every manifest covers same files
		if (manifestList.size() > 1)
		{
			
			for(int i=1; i < manifestList.size(); i++)
			{
				File manifest = manifestList.get(i);
				Collection<String> files = this.readManifest(manifest);
				if (baseFiles.size() != files.size() || ! baseFiles.containsAll(files))
				{
					String message = MessageFormat.format("Bag at {0} has manifests with different files", packageDir);
					log.info(message);
					return new SimpleResult(false, message);
				}
				
			}
		}
		//Every file listed in every manifest
		Iterator<File> iter = FileUtils.iterateFiles(BagHelper.getDataDirectory(packageDir), null, true);
		while(iter.hasNext())
		{
			File file = iter.next();
			String relativeFilePath = FilenameHelper.removeBasePath(packageDir.toString(), file.toString());
			if (! baseFiles.contains(relativeFilePath))
			{
				String message = MessageFormat.format("Bag at {0} has file {1} not found in manifest", packageDir, relativeFilePath);
				log.info(message);
				return new SimpleResult(false, message);
			}
		}
		return new SimpleResult(true);
	}

	private Collection<String> readManifest(File manifest)
	{
		Collection<String> files = new ArrayList<String>();
		ManifestReader reader = new ManifestReader(manifest);
		while (reader.hasNext())
		{
			files.add(FilenameHelper.normalize(reader.next().getFile()));
			
		}
		reader.close();
		return files;
	}
	
	@Override
	public SimpleResult isValid(File packageDir, boolean verifyTagManifests) {
		//Is complete
		SimpleResult result = this.isComplete(packageDir);
		if (! result.isSuccess())
		{
			return result;
		}
		//Every checksum checks
		result = verifier.verify(BagHelper.getManifests(packageDir));
		if (! result.isSuccess())
		{
			return result;
		}
		result = verifier.verify(BagHelper.getTagManifests(packageDir));
		
		return result;
	}

	@Override
	public void generate(File packageDir, String algorithm,
			boolean generateTagManifest) {
		//Make sure there is a data directory
		if (! BagHelper.getDataDirectory(packageDir).exists())
		{
			throw new RuntimeException(MessageFormat.format("Bag at {0} does not have data directory", packageDir));
		}
		//Write bagit.txt
		try
		{
			FileWriter writer = new FileWriter(BagHelper.getBagIt(packageDir));
			try
			{
				writer.write("BagIt-Version: " + VERSION + "\nTag-FileCharacter-Encoding: UTF-8");				
			}
			finally
			{
				writer.close();
			}
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
						
		//Write manifest
		this.verifier.generateManifest(packageDir, algorithm);
		//Write tag manifest
		this.verifier.generateTagManifest(packageDir, algorithm);
				
	}
	
	@Override
	public boolean isMissingBagItTolerant() {
		return this.isMissingBagItTolerant();
	}
	
	@Override
	public void setMissingBagItTolerant(boolean isTolerant) {
		this.isMissingBagItTolerant = isTolerant;		
	}
}
