package gov.loc.repository.transfer.components.remote.impl;

import java.util.Map;
import java.net.URL;
import java.net.HttpURLConnection;
import java.net.URLEncoder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.transfer.components.remote.GenericHttpClient;

public class GenericHttpClientImpl implements GenericHttpClient {

	private static final Log log = LogFactory.getLog(GenericHttpClientImpl.class);
	
	private URL conURL;
	
	public boolean execute(String baseUrl, Map<String, String> parameterMap)
			throws Exception {		
		String url = baseUrl;
		if (! parameterMap.isEmpty())
		{
			url += "?";
			boolean isFirst = true;
			for(String name : parameterMap.keySet())
			{
				if (! isFirst)
				{
					url += "&";
				}
				isFirst = false;
				String value = parameterMap.get(name);
				url += URLEncoder.encode(name, "UTF8") + "=" + URLEncoder.encode(value, "UTF8");
			}
		}
		conURL = new URL(url);
		log.info("Requesting " + conURL.toString());
		HttpURLConnection con = (HttpURLConnection)conURL.openConnection();
		if (con.getResponseCode() == 200)
		{
			return true;
		}
		return false;
	}

	public String getUrl() {
		if (this.conURL != null)
		{
			return this.conURL.toExternalForm();
		}
		return (String)null;
	}
}
