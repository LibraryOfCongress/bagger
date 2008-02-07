package gov.loc.repository.utilities;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.io.FilenameUtils;

public class FilenameHelper {
	
	public static String getRoot(String fileName)
	{
		File file = new File(fileName);
		File parentFile = file.getParentFile();
		if (parentFile == null)
		{
			return null;
		}
		while(parentFile != null)
		{
			file = parentFile;
			parentFile = file.getParentFile();
		}
		
		return normalize(file.toString());
		
	}
	
	public static String getPath(String fileName)
	{
		return FilenameUtils.getFullPathNoEndSeparator(fileName);
	}
	
	public static String getBaseName(String fileName)
	{
		return FilenameUtils.getBaseName(fileName);
	}
	
	public static boolean hasExtension(String fileName)
	{
		if (FilenameUtils.getExtension(fileName).length() != 0)
		{
			return true;
		}
		return false;
	}
	
	public static String getExtension(String fileName)
	{
		return FilenameUtils.getExtension(fileName);
	}

	public static String getFileName(String path, String baseName, String extension)
	{
		String fileName = baseName + ".";
		if (extension != null)
		{
			fileName += extension;
		}
		return FilenameUtils.concat(path, fileName);
	}
	
	public static boolean equals(String filename1, String filename2)
	{
		return FilenameUtils.equalsNormalized(filename1, filename2);
	}
	
	public static String removeBasePath(String basePath, String filename) throws Exception
	{
		if (! filename.startsWith(basePath))
		{
			throw new Exception(MessageFormat.format("Cannot remove basePath {0} from {1}", basePath, filename));
		}
		if (basePath.equals(filename))
		{
			return "";
		}
		return filename.substring(basePath.length() + 1);
	}
	
	public static String concat(String basePath, String additionalFilename)
	{
		return FilenameUtils.concat(basePath, additionalFilename);
	}
	
	public static String normalize(String filename)
	{
		String newFilename = FilenameUtils.normalize(filename);
		return FilenameUtils.separatorsToUnix(newFilename);
	}
}
