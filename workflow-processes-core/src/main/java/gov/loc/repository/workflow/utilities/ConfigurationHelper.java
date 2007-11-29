package gov.loc.repository.workflow.utilities;


import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;

public abstract class ConfigurationHelper {

	private static final String CFG_FILENAME = "workflow.core.cfg.xml";
	
	private static Configuration configuration;
	static
	{
		try
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			URL url = ConfigurationHelper.class.getClassLoader().getResource(CFG_FILENAME);
			builder.setURL(url);
			configuration = builder.getConfiguration(true);
		}
		catch(ConfigurationException ex)
		{
			throw new RuntimeException();
		}		
	}

	public static Configuration getConfiguration()
	{
		return configuration;
	}
}
