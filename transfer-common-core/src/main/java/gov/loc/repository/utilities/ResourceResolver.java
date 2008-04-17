package gov.loc.repository.utilities;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

public class ResourceResolver {

	private static final Log log = LogFactory.getLog(ResourceResolver.class);
	private static final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(); 
	
	public static URL findResource(String resourceName)
	{
		log.debug(MessageFormat.format("Finding resource {0}", resourceName));
		Resource resource = resolver.getResource(resourceName);
		try
		{
			if (resource != null && resource.exists())
			{
				return resource.getURL();
			}
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		return null; 
	}
	
	public static List<URL> findWildcardResourceList(String wildCard)
	{
		log.debug(MessageFormat.format("Finding resource list for {0}", wildCard));
		List<URL> urlList = new ArrayList<URL>();
		try
		{
			
			Resource[] resourceArray = resolver.getResources("classpath*:" + wildCard);
			
			for(Resource resource : resourceArray)
			{
				log.debug("Matching resource " + resource.getURL());
				urlList.add(resource.getURL());
			}
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
		
		return urlList;		
	}

	
}
