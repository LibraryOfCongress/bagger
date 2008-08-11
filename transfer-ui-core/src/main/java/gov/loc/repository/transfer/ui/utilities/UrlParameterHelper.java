package gov.loc.repository.transfer.ui.utilities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.text.MessageFormat;

public class UrlParameterHelper {
    
	protected static final Log log = LogFactory.getLog(UrlParameterHelper.class);
	
	public static Map<String,String> parse(String url, String description) throws Exception 	{
	    //ignore standard parameters
		url = url.replaceFirst("\\?.*", "");
		if (! url.endsWith("/")) {
			url += "/";
		}
		Map<String,String> urlParameterMap = new HashMap<String, String>();
		String descriptionPatternString = "";
		if (description != null) {
			Pattern keyPattern = Pattern.compile("\\{(.+?)\\}");
			Matcher keyMatcher = keyPattern.matcher(description);
			boolean isFound = false;
			int start = 0;
			List<String> keyList = new ArrayList<String>();
			while(keyMatcher.find()) {
				isFound = true;
				descriptionPatternString += description.substring(start, keyMatcher.start()) + "(.+?)";
				start = keyMatcher.end();
				keyList.add(keyMatcher.group(1));
			}
			//add any additional description after the last match
			descriptionPatternString += description.substring(start, description.length());
			if (!isFound) {
				throw new Exception("UrlParameterDescription does not describe any parameters");
			}
			descriptionPatternString += "/";
			log.debug(MessageFormat.format(
		        "compiled url description {0} to regexp pattern {1}", 
		        description, 
		        descriptionPatternString
		    ));
			Pattern descriptionPattern = Pattern.compile(descriptionPatternString);
			Matcher descriptionMatcher = descriptionPattern.matcher(url);
			if (descriptionMatcher.find()) {
				for(int i = 1; i <= keyList.size(); i++) {
					if (descriptionMatcher.groupCount() >= i) {
						urlParameterMap.put(
						    keyList.get(i-1), 
						    descriptionMatcher.group(i)
						);
					}
				}
			}
		}
		return urlParameterMap;
	}
}
