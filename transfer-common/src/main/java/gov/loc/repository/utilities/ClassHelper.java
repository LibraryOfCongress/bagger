package gov.loc.repository.utilities;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class ClassHelper {
	//Borrowed from http://forum.java.sun.com/thread.jspa?threadID=341935&tstart=0
	public static List<Class> getClasses(String pckgname, boolean recursive) throws Exception
	{
		List<Class> classes = new ArrayList<Class>();
		// Get a File object for the package
		File directory = null;
		try
		{
			ClassLoader cld = Thread.currentThread().getContextClassLoader();
			if (cld == null)
			{
				throw new ClassNotFoundException("Can't get class loader.");
			}
			String path = pckgname.replace('.', '/');
			URL resource = cld.getResource(path);
			if (resource == null)
			{
				throw new ClassNotFoundException("No resource for " + path);
			}
			directory = new File(resource.getFile());
		}
		catch (NullPointerException x)
		{
			throw new ClassNotFoundException(pckgname + " (" + directory
			+ ") does not appear to be a valid package");
		}
		if (directory.exists())
		{
			// Get the list of the files contained in the package
			File[] files = directory.listFiles();
			for (int i = 0; i < files.length; i++)
			{
				// we are only interested in .class files
				if (files[i].getName().endsWith(".class"))
				{
					// removes the .class extension
					String className = pckgname + '.' + files[i].getName().substring(0, files[i].getName().length() - 6);
					//System.out.println(className);
					classes.add(Class.forName(className));
				}
				else if (recursive && files[i].isDirectory())
				{
					classes.addAll(getClasses(pckgname + '.' + files[i].getName() , recursive));
				}
			}
		}
		else
		{
			throw new ClassNotFoundException(pckgname + " does not appear to be a valid package");
		}
		return classes;
	}

}
