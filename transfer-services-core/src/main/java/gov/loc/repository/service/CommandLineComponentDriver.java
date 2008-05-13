package gov.loc.repository.service;

import gov.loc.repository.serviceBroker.ServiceRequest.ObjectEntry;
import gov.loc.repository.serviceBroker.impl.BooleanEntryImpl;
import gov.loc.repository.serviceBroker.impl.IntegerEntryImpl;
import gov.loc.repository.serviceBroker.impl.StringEntryImpl;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CommandLineComponentDriver {

	public static void main(String[] args) throws Exception {
		String key=null;
		String jobType=null;
		Collection<ObjectEntry> entries = new ArrayList<ObjectEntry>();
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
					if (arg.equalsIgnoreCase("true"))
					{
						entries.add(new BooleanEntryImpl(key, true));
					}
					else if (arg.equalsIgnoreCase("false"))
					{
						entries.add(new BooleanEntryImpl(key, false));
					}
					else
					{
						try
						{
							long l = Long.parseLong(arg);
							entries.add(new IntegerEntryImpl(key, l));
						}
						catch(NumberFormatException ex)
						{
							//Then it's a string
							entries.add(new StringEntryImpl(key, arg));
						}
					}
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
		System.out.println("entries:");
		for(ObjectEntry entry : entries)
		{
			System.out.println(MessageFormat.format("{0} = : {1} [{2}]", entry.getKey(), entry.getValueObject(), entry.getValueObject().getClass().getSimpleName()));
		}
				
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:conf/servicecontainer-context.xml", "classpath*:conf/components-*-context.xml"});
		ComponentFactory componentFactory = (ComponentFactory)context.getBean("componentFactory");
		Object component = componentFactory.getComponent(jobType);
		InvokeComponentHelper helper = new InvokeComponentHelper(component, jobType, entries);
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
