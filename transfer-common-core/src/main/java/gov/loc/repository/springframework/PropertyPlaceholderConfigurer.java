package gov.loc.repository.springframework;

import gov.loc.repository.utilities.PropertyResourceResolver;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;

public class PropertyPlaceholderConfigurer extends org.springframework.beans.factory.config.PropertyPlaceholderConfigurer {
	
	private static final Log log = LogFactory.getLog(PropertyPlaceholderConfigurer.class);
		
	public void setConfigurationName(String configurationName) throws Exception
	{
		List<URL> resourceUrlList = PropertyResourceResolver.findPropertyResources(configurationName);
		ArrayList<Resource> resourceList = new ArrayList<Resource>();
		log.debug(MessageFormat.format("Found {0} property resources", resourceUrlList.size()));
		//Load in reverse order
		for(int i=resourceUrlList.size()-1; i >= 0; i--)
		{			
			URL url = resourceUrlList.get(i);
			//String resourceLocation = url.toString().substring(url.toString().lastIndexOf("/") + 1);
			log.debug("Using property file: " + url.toString());
			resourceList.add(new UrlResource(url));
			
		}
		this.setLocations(resourceList.toArray(new Resource[0]));
	}
	
	
	
}
