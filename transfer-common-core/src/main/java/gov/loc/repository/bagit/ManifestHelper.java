package gov.loc.repository.bagit;

import java.io.File;

public class ManifestHelper {

	public static String getAlgorithm(File manifestFile)
	{
		String filename = manifestFile.getName();
		if (filename.startsWith(BagHelper.MANIFEST_PREFIX))
		{
			return manifestFile.getName().substring(BagHelper.MANIFEST_PREFIX.length(), manifestFile.getName().length()-BagHelper.MANIFEST_SUFFIX.length());
		}
		else if (filename.startsWith(BagHelper.TAG_MANIFEST_PREFIX))
		{
			return manifestFile.getName().substring(BagHelper.TAG_MANIFEST_PREFIX.length(), manifestFile.getName().length()-BagHelper.TAG_MANIFEST_SUFFIX.length());
			
		}
		throw new RuntimeException("Algorithm not found in manifest filename");
			
	}
	
	public static String getBasePath(File manifestFile)
	{
		return manifestFile.getParent();
	}
	
	public static boolean isTagManifest(File manifestFile)
	{
		if (manifestFile.getName().startsWith(BagHelper.TAG_MANIFEST_PREFIX) && manifestFile.getName().endsWith(BagHelper.TAG_MANIFEST_SUFFIX))
		{
			return true;
		}
		return false;
	}
	
	public static boolean isManifest(File manifestFile)
	{
		if (manifestFile.getName().startsWith(BagHelper.MANIFEST_PREFIX) && manifestFile.getName().endsWith(BagHelper.MANIFEST_SUFFIX))
		{
			return true;
		}
		return false;
		
	}
}
