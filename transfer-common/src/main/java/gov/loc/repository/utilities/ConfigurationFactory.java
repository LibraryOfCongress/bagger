package gov.loc.repository.utilities;

import java.io.File;
import java.io.FileFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.io.IOCase;
import org.apache.commons.io.filefilter.WildcardFileFilter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConfigurationFactory {
	
	private static final Log log = LogFactory.getLog(ConfigurationFactory.class);

	
	static List<URL> resourceList = null;

	static Map<String, Configuration> configurationMap = new HashMap<String, Configuration>();

	/*
	 * Automatically discovers configuration files and creates a Commons Configuration Configuration.
	 * Configuration files must be located in the root/base of a file location or jar in the classpath.
	 * The configurationName is used to create a wildcard (configurationName.*.properties) to determine which configuration files to use.
	 * The following is the order of precedence:  system properties, configurationName.*.properties, configurationName.properties.
	 * The order of configuration properties that match configurationName.*.properties is not determinate.
	 * All matching is case-insensitive. 
	 */	
	public static Configuration getConfiguration(String configurationName) throws Exception
	{
		if (! configurationMap.containsKey(configurationName))
		{
			CompositeConfiguration configuration = new CompositeConfiguration();
			configuration.addConfiguration(new SystemConfiguration());

			List<URL> resourceList = findWildcardResourceList(configurationName + ".*.properties");
			URL matchingResource = ConfigurationFactory.class.getClassLoader().getResource(configurationName + ".properties");
			if (matchingResource != null)
			{
				resourceList.add(matchingResource);
			}
			
			for(URL resource : resourceList)
			{
				log.debug("Adding configuration: " + resource.toString());
				configuration.addConfiguration(new PropertiesConfiguration(resource));				
			}
			configurationMap.put(configurationName, configuration);
		}
		return configurationMap.get(configurationName);
	}
	
	public static List<URL> findWildcardResourceList(String wildCard) throws Exception
	{
		if (resourceList == null)
		{
			loadResourceList();
		}
		List<URL> resourceMatchList = new ArrayList<URL>();
		FileFilter wildcardFileFilter = new WildcardFileFilter(wildCard, IOCase.INSENSITIVE);
		for(URL url : resourceList)
		{
			String fileString = url.toExternalForm().substring(url.toExternalForm().lastIndexOf('/')+1);
			File file = new File(fileString);
			if (wildcardFileFilter.accept(file))
			{
				resourceMatchList.add(url);
			}
		}
		return resourceMatchList;		
	}
	
	
	private static void loadResourceList() throws Exception
	{
		resourceList = new ArrayList<URL>();
		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		Enumeration<URL> fileResourceEnum = classLoader.getResources("");		
		while(fileResourceEnum.hasMoreElements())
		{
			File dir = new File(fileResourceEnum.nextElement().getFile());
			if (dir.isDirectory())
			{
				for(File file : dir.listFiles())
				{
					if (file.isFile())
					{
						resourceList.add(file.toURI().toURL());
					}
				}
			}
			
		}
		
		Enumeration<URL> jarResourceEnum = classLoader.getResources("META-INF");		
		while(jarResourceEnum.hasMoreElements())
		{
			URL url = jarResourceEnum.nextElement();
			if (url.toExternalForm().startsWith("jar:"))
			{
				JarURLConnection conn = (JarURLConnection)url.openConnection();
				JarFile jarFile = conn.getJarFile();
				Enumeration<JarEntry> jarEntryEnum = jarFile.entries();
				while(jarEntryEnum.hasMoreElements())
				{
					JarEntry jarEntry = jarEntryEnum.nextElement();			
					if (jarEntry.getName().indexOf('/') == -1)
					{
						
						String entryURLString = conn.getURL().toExternalForm().substring(0, conn.getURL().toExternalForm().length()-8) + jarEntry.getName();
						resourceList.add(new URL(entryURLString));
					}
				}
			}
		}
	}
	
}
