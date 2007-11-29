package gov.loc.repository.utilities;

import java.io.File;

public class ManifestHelper {

	public static String getAlgorithm(File manifestFile)
	{
		return manifestFile.getName().substring(9, manifestFile.getName().length()-4);
	}
	
	public static String getBasePath(File manifestFile)
	{
		return manifestFile.getParent();
	}
		
}
