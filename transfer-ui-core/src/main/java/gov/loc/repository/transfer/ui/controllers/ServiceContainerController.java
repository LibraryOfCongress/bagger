package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.serviceBroker.ServiceContainerRegistry;
import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import gov.loc.repository.workflow.continuations.CompletedServiceRequestListener.State;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jmx.support.MBeanServerConnectionFactoryBean;
import org.springframework.jmx.support.MBeanServerFactoryBean;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ServiceContainerController {

	private static final Log log = LogFactory.getLog(ServiceContainerController.class);
	
	private ServiceContainerRegistry registry;
	
	@Required
	@Resource(name="serviceContainerRegistry")
	public void setServiceContainerRegistry(ServiceContainerRegistry registry)
	{
		this.registry = registry;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView handleGet(HttpServletRequest req) throws Exception {
		ModelAndView mav = new ModelAndView("servicecontainer");
		PermissionsHelper permissions = new PermissionsHelper(req);
		mav.addObject("permissions", permissions);
		
		List<ServiceContainerBean> serviceContainerBeanList = new ArrayList<ServiceContainerBean>(); 
		List<String> serviceUrlList = this.registry.listServiceContainers();
		for(String serviceUrl : serviceUrlList)
		{
			ServiceContainerBean serviceContainerBean = this.loadServiceContainerBean(serviceUrl);
			if (serviceContainerBean != null)
			{
				serviceContainerBeanList.add(serviceContainerBean);
			}	
		}
		
		mav.addObject("serviceContainerBeanList", serviceContainerBeanList);
		
		return mav;		
	}

	private ServiceContainerBean loadServiceContainerBean(String serviceUrl)
	{

		try
		{
			MBeanServerConnectionFactoryBean factory = new org.springframework.jmx.support.MBeanServerConnectionFactoryBean();
			factory.setServiceUrl(serviceUrl);
			factory.afterPropertiesSet();
			return new ServiceContainerBean((MBeanServerConnection)factory.getObject(), serviceUrl);
		}
		catch(Exception ex)
		{
			log.warn(MessageFormat.format("Attempt to connect to {0} failed, so unregistering.", serviceUrl));
			this.registry.unregister(serviceUrl);
		}
		return null;
	}
	
	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView handlePost(HttpServletRequest req) throws Exception {
		try
		{
			ServiceContainerBean serviceContainerBean = this.loadServiceContainerBean(req.getParameter("serviceUrl"));
			if (req.getParameter("start") != null && serviceContainerBean.getState().equals("STOPPED")) {
				serviceContainerBean.start();
				req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Started Service Container");
			}
			else if (req.getParameter("stop") != null && serviceContainerBean.getState().equals("STARTED")) {
				serviceContainerBean.stop();
				req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Stopped Service Container");
			}
		}
		catch(Exception ex)
		{
			log.error("Error thrown starting/stop CompletedServiceRequestListener", ex);
			req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Unable to start/stop Service Container");
		}
		return this.handleGet(req);
	}	
	
	public class ServiceContainerBean
	{
		private MBeanServerConnection conn;
		private ObjectName objectName;
		private String serviceUrl;
		
		public ServiceContainerBean(MBeanServerConnection conn, String serviceUrl) {			
			this.conn = conn;
			this.serviceUrl = serviceUrl;
			try
			{
				objectName = new ObjectName("bean:name=serviceContainer");
			}
			catch(MalformedObjectNameException ex)
			{
				throw new RuntimeException(ex);
			}
			/*
			try
			{
				MBeanInfo info = conn.getMBeanInfo(objectName);
				MBeanAttributeInfo[] attInfos = info.getAttributes();
				for(MBeanAttributeInfo attInfo : attInfos)
				{
					log.debug(attInfo.getName());
				}
			}
			catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
			*/
			
		}
		
		public String getServiceUrl()
		{
			return this.serviceUrl;
		}
		
		public String getResponder()
		{
			return (String)getAttribute("Responder");
		}
		
		public String getQueues()
		{
			return arrayToString((String[])getAttribute("Queues"));
		}

		public String getJobTypes()
		{
			return arrayToString((String[])getAttribute("JobTypes"));
		}

		public String getState()
		{
			return (String)getAttribute("StateString");
		}
		
		public Integer getActiveServiceRequestCount()
		{
			return (Integer)getAttribute("ActiveServiceRequestCount");
		}
		
		public void start()
		{
			this.invoke("start");
		}
		
		public void stop()
		{
			this.invoke("stop");
		}
		
		private String arrayToString(String[] array)
		{
			String itemList = "";
			for(String item : array)
			{
				if (itemList.length() != 0)
				{
					itemList += ", ";
				}
				itemList += item;
			}
			return itemList;
		}
		
		private void invoke(String operation)
		{
			try
			{
				conn.invoke(objectName, operation, null, null);
			}
			catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
		}
		
		private Object getAttribute(String attr)
		{
			try
			{
				return conn.getAttribute(objectName, attr);

			}
			catch(Exception ex)
			{
				throw new RuntimeException(ex);
			}
			
		}
	}
}
