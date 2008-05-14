package gov.loc.repository.transfer.ui.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

	protected static final Log log = LogFactory.getLog(AdminController.class);

	@RequestMapping("index.html")
	public ModelAndView handleIndex() 
	{
		ModelAndView mav = new ModelAndView("admin");
		log.debug("Handling request at /admin/index.html");
		
		Runtime runtime = Runtime.getRuntime();
		mav.addObject("totalMemory", runtime.totalMemory()/1024);
		mav.addObject("maxMemory", runtime.maxMemory()/1024);
		mav.addObject("freeMemory", runtime.freeMemory()/1024);
		
		Map<String, String> systemInfoMap = new HashMap<String, String>();
		Properties systemProps = System.getProperties();
		for(Object prop:systemProps.keySet()){
		    String key = prop.toString();
		    //splits up long classpaths so wrapping isnt a hassle
		    String value = whiteSpaceTokens((String)systemProps.get(prop));
		    log.debug("AdminController.handleIndex: [" + key + "][" + value + "]");
		    systemInfoMap.put(key, value);
		}
		// These are a test for ticket #124, IE vs Firefox -- jste
		/* 
		String tk = "package.access";
		String tv = "sun.,org.apache.catalina.,org.apache.coyote.,org.apache.tomcat.,org.apache.jasper.,sun.beans.";
		String v = whiteSpaceTokens(tv);
	    systemInfoMap.put(tk, v);
	    String tk2 = "java.class.path";
	    String tv2 = ":/home/justin/apache-tomcat-5.5.23/bin/bootstrap.jar:/home/justin/apache-tomcat-5.5.23/bin/commons-logging-api.jar;";
		String v2 = whiteSpaceTokens(tv2);
	    systemInfoMap.put(tk2, v2);
	     */ 
		mav.addObject("systemInfo", systemInfoMap);
		return mav;
	}
	
	private String whiteSpaceTokens(String prop) {
		String[] tokens = {":", ",", "/"};
		for (int i=0; i< tokens.length; i++) {
			String v = prop.replace(tokens[i],tokens[i]+" ");
			prop = v;
		}
		return prop;
	}
	
}
