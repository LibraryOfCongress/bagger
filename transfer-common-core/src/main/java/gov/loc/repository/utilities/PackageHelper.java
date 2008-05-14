package gov.loc.repository.utilities;

import java.io.File;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class PackageHelper {

	public static final String PACKAGE_INFORMATION_DIRECTORY = "package-information";
	public static final String CONTENT_DIRECTORY = "contents";
	
	private static final Log log = LogFactory.getLog(PackageHelper.class);
	
	public static File discoverContentDirectory(File packageDirectory) throws Exception
	{
		if (packageDirectory == null)
		{
			throw new Exception("Package directory is null");
		}
		if (! packageDirectory.isDirectory())
		{
			throw new Exception(MessageFormat.format("Package directory ({0}) is not a directory", packageDirectory.toString()));
		}
		File contentDirectory = internalDiscoverContentDirectory(packageDirectory);
		if (contentDirectory == null)
		{
			throw new Exception(MessageFormat.format("{0} has no or multiple multiple content directories", packageDirectory.toString()));
		}
		return contentDirectory;
	}
	
	private static File internalDiscoverContentDirectory(File packageDirectory)
	{		
		File contentDir = null;
		
		for (File file : packageDirectory.listFiles())
		{
			if (file.isDirectory() && ! PACKAGE_INFORMATION_DIRECTORY.equals(file.getName()))
			{
				if (contentDir == null)
				{
					contentDir = file;
				}
				else
				{
					log.debug(MessageFormat.format("{0} has multiple content directories", packageDirectory.toString()));
					return null;
				}
			}
		}
		if (contentDir == null)
		{
			log.debug(MessageFormat.format("Content directory not found for {0}", packageDirectory.toString()));			
		}
		return contentDir;		
		
	}
	
	public static File internalDiscoverManifest(File packageDirectory)
	{
		
		File manifestFile = null;
		for (File file : packageDirectory.listFiles())
		{
			if (file.isFile() && file.getName().startsWith("manifest-") && file.getName().endsWith(".txt"))
			{
				if (manifestFile == null)
				{
					manifestFile = file;
				}
				else
				{
					log.debug(MessageFormat.format("{0} has multiple manifests", packageDirectory.toString()));
					return null;
				}
			}
		}
		if (manifestFile == null)
		{
			log.debug(MessageFormat.format("Manifest not found for {0}", packageDirectory.toString()));			
		}
		return manifestFile;
		
	}
	
	public static File discoverManifest(File packageDirectory) throws Exception
	{
		File manifestFile = internalDiscoverManifest(packageDirectory);
		if (manifestFile == null)
		{
			throw new Exception(MessageFormat.format("{0} has no or multiple multiple manifests", packageDirectory.toString()));
		}
		return manifestFile;
	}
	
	public static File getManifest(File packageDirectory, String algorithm)
	{
		return new File(packageDirectory, "manifest-" + algorithm.toLowerCase() + ".txt");
	}
	
	public static File discoverPackageInformation(File packageDirectory)
	{
		for (File file : packageDirectory.listFiles())
		{
			if (file.isDirectory() && PACKAGE_INFORMATION_DIRECTORY.equals(file.getName()))
			{
				return file;
			}
		}
		return null;		
	}
	
	public static boolean isInLCPackageRoot(File packageDirectory, File file)
	{
		if (packageDirectory.equals(file.getParentFile()))
		{
			return true;
		}
		return false;
	}
	
	public static boolean isLCPackage(File packageDirectory)
	{
		//There must be a manifest
		if (internalDiscoverManifest(packageDirectory) == null)
		{
			return false;
		}
		//There may be only one content directory
		if (internalDiscoverContentDirectory(packageDirectory) == null)
		{
			return false;
		}
		return true;
	}

	public static List<File> discoverLCPackageRootFiles(File packageDirectory)
	{
		List<File> fileList = new ArrayList<File>();
		for(File file : packageDirectory.listFiles())
		{
			if (file.isFile())
			{
				fileList.add(file);
			}
		}
		return fileList;
	}
	
}