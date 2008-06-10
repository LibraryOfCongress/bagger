package gov.loc.repository.bagit;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagHelper {

	private static final Log log = LogFactory.getLog(BagHelper.class);
	
	public static final String DATA_DIRECTORY = "data";
	public static final String BAGIT = "bagit.txt";
	public static final String MANIFEST_PREFIX = "manifest-";
	public static final String TAG_MANIFEST_PREFIX = "tagmanifest-";
	public static final String MANIFEST_SUFFIX = ".txt";
	public static final String TAG_MANIFEST_SUFFIX = ".txt";

	
	public static File getDataDirectory(File packageDir)
	{
		return new File(packageDir, DATA_DIRECTORY);
	}
	
	public static File getBagIt(File packageDir)
	{
		return new File(packageDir, BAGIT);
	}
	
	public static List<File> getManifests(File packageDir)
	{
		List<File> manifests = new ArrayList<File>();
		for (File file : packageDir.listFiles())
		{
			if (file.isFile() && file.getName().startsWith(MANIFEST_PREFIX) && file.getName().endsWith(MANIFEST_SUFFIX))
			{
				manifests.add(file);
			}
		}
		if (manifests.isEmpty())
		{
			log.debug(MessageFormat.format("Manifest not found for {0}", packageDir));			
		}
		return manifests;
		
	}

	public static File getManifest(File packageDir, String algorithm)
	{
		return new File(packageDir, MANIFEST_PREFIX + algorithm + MANIFEST_SUFFIX);
	}

	public static List<File> getTagManifests(File packageDir)
	{
		List<File> manifests = new ArrayList<File>();
		for (File file : packageDir.listFiles())
		{
			if (file.isFile() && file.getName().startsWith(TAG_MANIFEST_PREFIX) && file.getName().endsWith(TAG_MANIFEST_SUFFIX))
			{
				manifests.add(file);
			}
		}
		if (manifests.isEmpty())
		{
			log.debug(MessageFormat.format("Tag manifest not found for {0}", packageDir));			
		}
		return manifests;
		
	}

	public static File getTagManifest(File packageDir, String algorithm)
	{
		return new File(packageDir, TAG_MANIFEST_PREFIX + algorithm + TAG_MANIFEST_SUFFIX);
	}
	
	
	public static boolean isTag(File packageDir, File file)
	{
		if (packageDir.equals(file.getParentFile()))
		{
			return true;
		}
		return false;
	}
	
	public static List<File> getTags(File packageDir, boolean includeManifests)
	{
		List<File> fileList = new ArrayList<File>();
		for(File file : packageDir.listFiles())
		{
			if (file.isFile() && (includeManifests || (! ManifestHelper.isManifest(file) && ! ManifestHelper.isTagManifest(file))))
			{
				fileList.add(file);
			}
		}
		return fileList;
	}
	
}