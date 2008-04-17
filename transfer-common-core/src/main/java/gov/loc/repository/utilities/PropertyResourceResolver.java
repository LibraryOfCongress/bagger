package gov.loc.repository.utilities;

import java.net.URL;
import java.util.List;

public class PropertyResourceResolver {

		
	public static List<URL> findPropertyResources(String configurationName)
	{
		List<URL> urlList = ResourceResolver.findWildcardResourceList("conf/" + configurationName + ".*.properties");
		URL url = ResourceResolver.findResource("conf/" + configurationName + ".properties");
		if (url != null)
		{
			urlList.add(url);
		}
		return urlList;
	}
		
}
