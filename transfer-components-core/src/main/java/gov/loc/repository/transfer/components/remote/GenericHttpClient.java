package gov.loc.repository.transfer.components.remote;

import java.util.Map;

/**
 * Interface to be used to make synchronous http requests that follow a simple name/value pair pattern to a remote service.
 * <p>Note that the result is success/fail, as determined based on the response code.
 * <p>The server cannot update variables and the content of the response body is ignored.
 */
public interface GenericHttpClient
{
	/**
	 * Perform an http request for a constructed url.
	 * <p>The url is constructed by concatenating the parameters to the baseurl.
	 * <p>
	 * @param baseUrl The base of the constructed url, for example, "http://example.com/foo.html"
	 * @param parameterMap The name/value pairs to be concatenated to the baseUrl.
	 * @return If the remote service returns a 200 response code, true is returned.  Otherwise, false is returned. 
	 * @throws Exception
	 */
	public boolean execute(String baseUrl, Map<String,String> parameterMap) throws Exception;
	
	/**
	 * Gets the most recently executed URL.
	 * @return String The URL as a String.
	 */
	public String getUrl();
}
