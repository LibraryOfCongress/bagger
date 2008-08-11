package gov.loc.repository.utilities;

import gov.loc.repository.exceptions.ConfigurationException;

import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.CompositeConfiguration;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.configuration.SystemConfiguration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ConfigurationFactory {
	
	private static final Log log = LogFactory.getLog(ConfigurationFactory.class);

	
	static List<URL> resourceList = null;

	static Map<String, Configuration> configurationMap = Collections.synchronizedMap(new HashMap<String, Configuration>());

	/*
	 * Automatically discovers configuration files and creates a Commons Configuration Configuration.
	 * Configuration files must be located in the /config directory of a file location or jar in the classpath.
	 * The configurationName is used to create a wildcard (configurationName.*.properties) to determine which configuration files to use.
	 * The following is the order of precedence:  system properties, configurationName.*.properties, configurationName.properties.
	 * The order of configuration properties that match configurationName.*.properties is not determinate.
	 * All matching is case-insensitive. 
	 */	
	public static Configuration getConfiguration(String configurationName)
	{
		if (! configurationMap.containsKey(configurationName))
		{
			CompositeConfiguration configuration = new CompositeConfiguration();
			configuration.addConfiguration(new SystemConfiguration());
			
			List<URL> resourceList =  PropertyResourceResolver.findPropertyResources(configurationName);			
			for(URL resource : resourceList)
			{
				log.debug("Adding configuration: " + resource.toString());
				try
				{
					configuration.addConfiguration(new PropertiesConfiguration(resource));
				}
				catch(org.apache.commons.configuration.ConfigurationException ex)
				{
					throw new ConfigurationException(ex);
				}
			}
			configurationMap.put(configurationName, configuration);
		}
		return configurationMap.get(configurationName);
	}
		
}
