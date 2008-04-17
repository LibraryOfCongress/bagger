package gov.loc.repository.service;

import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.ResourceResolver;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CommandLineComponentDriver {

	public static void main(String[] args) throws Exception {
		String key=null;
		String jobType=null;
		Map<String,Object> variableMap = new HashMap<String, Object>();
		for(String arg: args)
		{
			if (arg.startsWith("-"))
			{
				if (key != null)
				{
					System.err.println(MessageFormat.format("key {0} follows another key", arg));
					return;
				}
				key = arg.substring(1);
			}
			else
			{
				if (key != null)
				{
					Object value = arg;
					if (arg.equalsIgnoreCase("true"))
					{
						value = true;
					}
					else if (arg.equalsIgnoreCase("false"))
					{
						value = false;
					}
					else
					{
						try
						{
							long l = Long.parseLong(arg);
							value = l;
						}
						catch(NumberFormatException ex)
						{							
						}
					}
					variableMap.put(key, value);
					key = null;
				}
				else if (jobType == null)
				{
					jobType = arg;
				}
				else
				{
					System.err.println(MessageFormat.format("JobType is already defined as {0} and there is no key for value {1}", jobType, arg));
					return;
				}
			}
		}
		if (jobType == null)
		{
			System.err.println("jobType is not defined");
			return;
		}
		System.out.println("jobType:  " + jobType);
		System.out.println("variableMap:");
		for(String k : variableMap.keySet())
		{
			Object v = variableMap.get(k);
			System.out.println(MessageFormat.format("{0} = : {1} [{2}]", k, v, v.getClass().getSimpleName()));
		}
		
		List<URL> contextUrlList = ResourceResolver.findWildcardResourceList("conf/services-context-*.xml");
		List<String> contextLocationList = new ArrayList<String>();
		for(URL url : contextUrlList)
		{
			String contextLocation = url.toString();
			contextLocationList.add(contextLocation);
		}
		
		ApplicationContext context = new ClassPathXmlApplicationContext(contextLocationList.toArray(new String[0]));
		ComponentFactory componentFactory = (ComponentFactory)context.getBean("componentFactory");
		Object component = componentFactory.getComponent(jobType);
		InvokeComponentHelper helper = new InvokeComponentHelper(component, jobType, variableMap);
		boolean result = true;
		org.hibernate.Session hibernateSession = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).getCurrentSession();
		try
		{					
			hibernateSession.beginTransaction();
			//Invoke and return taskResult
			System.out.println("Invoking " + jobType);
			result = helper.invoke();
			System.out.println("Returned " + result);
			hibernateSession.getTransaction().commit();
		}
		catch(Exception ex)
		{
			if (hibernateSession != null && hibernateSession.isOpen())
			{
				hibernateSession.getTransaction().rollback();
			}
			throw ex;
		}
		finally
		{
			if (hibernateSession != null && hibernateSession.isOpen())
			{
				hibernateSession.close();
			}
		}
		
	}

}
