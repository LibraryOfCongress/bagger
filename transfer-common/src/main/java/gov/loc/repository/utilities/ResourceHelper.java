package gov.loc.repository.utilities;

import java.io.File;

public class ResourceHelper {

	public static File getFile(Class clazz, String filename) throws Exception
	{
		String resourceName = clazz.getPackage().getName().replace('.', '/');
		if (filename != null)
		{
			resourceName += "/" + filename;
		}
		return new File(clazz.getClassLoader().getResource(resourceName).toURI());
	}

	public static File getFile(Object obj, String filename) throws Exception
	{
		return getFile(obj.getClass(), filename);
	}
	
}
