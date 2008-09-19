package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

import java.io.FileInputStream;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.sql.DataSource;

import org.apache.commons.dbcp.BasicDataSource;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class AdminController {

	public static final String LATENCY_KEY = "healthcheck.servicecontainers.latency";
	public static final String HOSTS_KEY = "healthcheck.servicecontainers.hosts";
	
	protected static final Log log = LogFactory.getLog(AdminController.class);
	private Resource configResource;
	private ServiceRequestDAO dao;
	private DataSource packageModelerDataSource; 
	private DataSource serviceRequestBrokerDataSource;
	private DataSource jbpmDataSource;

	
	@Required
	public void setConfigResource(Resource configResource)
	{
		this.configResource = configResource;
	}
		
	@Required
	@Autowired
	public void setServiceRequestDAO(ServiceRequestDAO dao)
	{
		this.dao = dao;
	}
	
	@Required
	@javax.annotation.Resource(name="packageModelerDataSource")
	public void setPackageModelerDataSource(DataSource dataSource)
	{
		this.packageModelerDataSource = dataSource;
	}

	@Required
	@javax.annotation.Resource(name="jbpmDataSource")
	public void setJbpmDataSource(DataSource dataSource)
	{
		this.jbpmDataSource = dataSource;
	}

	@Required
	@javax.annotation.Resource(name="serviceBrokerDataSource")
	public void setServiceRequestBrokerDataSource(DataSource dataSource)
	{
		this.serviceRequestBrokerDataSource = dataSource;
	}
	

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

	@RequestMapping("healthcheck.html")
	public ModelAndView handleHealthCheck() throws Exception 
	{
		ModelAndView mav = new ModelAndView("healthcheck");

		List<String> passedTestList = new ArrayList<String>();
		List<String> problemList = new ArrayList<String>();
		
		//Check the dbs
		log.debug("Checking PackageModeler datasource");
		if (this.checkDb(this.packageModelerDataSource))
		{
			passedTestList.add("PackageModeler db is OK");
		}
		else
		{
			problemList.add("PackageModeler db is not connected");
		}

		log.debug("Checking Jbpm datasource");
		if (this.checkDb(this.jbpmDataSource))
		{
			passedTestList.add("Jbpm db is OK");
		}
		else
		{
			problemList.add("Jbpm db is not connected");
		}

		boolean serviceRequestBrokerDbOK = true;

		log.debug("Checking ServiceRequestBroker datasource");
		if (this.checkDb(this.serviceRequestBrokerDataSource))
		{
			passedTestList.add("ServiceRequestBroker db is OK");
		}
		else
		{
			problemList.add("ServiceRequestBroker db is not connected");
			serviceRequestBrokerDbOK = false;
		}
		
		if (serviceRequestBrokerDbOK)
		{
		
			//Load every times so that get changes
			Properties props = new Properties();
			InputStream in = new FileInputStream(this.configResource.getFile()); 
			props.load(in);
			in.close();
		
			Long latency = Long.parseLong(props.getProperty(LATENCY_KEY));
			String hostsString = props.getProperty(HOSTS_KEY, "");
			log.debug(MessageFormat.format("Hosts string is {0}", hostsString));
			String[] serviceContainerHosts = new String[] {};
			if (hostsString.length() > 0)
			{
				serviceContainerHosts = props.getProperty(HOSTS_KEY, "").split(",");
			}
			log.debug("Finding servicecontainer registrations");
			List<ServiceContainerRegistration> registrationList = this.dao.findServiceContainerRegistrations(latency);
			boolean passed = true;
			log.debug("Checking expected service containers are found");
			for(String host : serviceContainerHosts)
			{
				if (! this.hasHost(host, registrationList))
				{
					problemList.add(MessageFormat.format("Expected service container {0} is missing", host));;
					passed = false;
				}
				else
				{
					log.debug(MessageFormat.format("Expected service container {0} is found", host));
				}
			}
			
			if (passed)
			{
				passedTestList.add("All expected service containers found");
			}
			
			passed = true;
			log.debug("Checking no unexpected service containers are found");
			for(ServiceContainerRegistration registration : registrationList)
			{
				if (! this.hasHost(registration.getHost(), serviceContainerHosts))
				{
					problemList.add(MessageFormat.format("Unexpected service container {0} found", registration.getHost()));;
					passed = false;
				}
				else
				{
					log.debug(MessageFormat.format("Expected service container {0} found", registration.getHost()));
				}
			}
			
			if (passed)
			{
				passedTestList.add("No unexpected service containers found");
			}
		}

		log.debug("Tests complete");
		mav.addObject("passedTestList", passedTestList);
		mav.addObject("problemList", problemList);
		
		return mav;
	}
	
	private boolean checkDb(DataSource dataSource)
	{
		try
		{
			BasicDataSource bds = (BasicDataSource)dataSource;
			log.debug(MessageFormat.format("NumActive: {0}, NumIdle: {1}", bds.getNumActive(), bds.getNumIdle()));
			Connection con = dataSource.getConnection();
			PreparedStatement stmt = con.prepareStatement("select 1");
			stmt.executeQuery();
			stmt.close();
			con.close();
			
			return true;
			
		}
		catch(Exception ex)
		{			
			log.warn(ex);
		}
		return false;
	}
	
	private boolean hasHost(String host, List<ServiceContainerRegistration> registrationList)
	{
		for(ServiceContainerRegistration registration : registrationList)
		{
			if (host.equals(registration.getHost()))
			{
				return true;
			}			
		}
		return false;
	}
	
	private boolean hasHost(String host, String[] serviceContainerHosts)
	{
		for(String checkHost : serviceContainerHosts)
		{
			if (host.equals(checkHost))
			{
				return true;
			}
		}
		return false;
	}
}
