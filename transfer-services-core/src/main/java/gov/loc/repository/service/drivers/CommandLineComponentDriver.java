package gov.loc.repository.service.drivers;

import gov.loc.repository.service.component.ComponentFactory;
import gov.loc.repository.service.component.ComponentRequest;
import gov.loc.repository.service.component.ComponentInvoker;
import gov.loc.repository.service.component.ComponentRequest.ObjectEntry;
import gov.loc.repository.service.component.impl.ComponentRequestImpl;

import java.text.MessageFormat;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class CommandLineComponentDriver {

	public static void main(String[] args) throws Exception {
		String key=null;
		ComponentRequest req = new ComponentRequestImpl();
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
					if (arg.startsWith("[") && arg.endsWith("]"))
					{
						//Strip and treat as a string
						//This allows something like [664] to be treated as a string
						req.addRequestString(key, arg.substring(1, arg.length()-1));
						
					}
					else if (arg.equalsIgnoreCase("true"))
					{
						req.addRequestBoolean(key, true);
					}
					else if (arg.equalsIgnoreCase("false"))
					{
						req.addRequestBoolean(key, false);
					}
					else
					{
						try
						{
							long l = Long.parseLong(arg);
							req.addRequestInteger(key, l);
						}
						catch(NumberFormatException ex)
						{
							//Then it's a string
							req.addRequestString(key, arg);
						}
					}
					key = null;
				}
				else if (req.getJobType() == null)
				{
					req.setJobType(arg);
				}
				else
				{
					System.err.println(MessageFormat.format("JobType is already defined as {0} and there is no key for value {1}", req.getJobType(), arg));
					return;
				}
			}
		}
		if (req.getJobType() == null)
		{
			System.err.println("jobType is not defined");
			return;
		}
		System.out.println("jobType:  " + req.getJobType());
		System.out.println("request entries:");
		for(ObjectEntry entry : req.getRequestEntries())
		{
			System.out.println(MessageFormat.format("{0} = : {1} [{2}]", entry.getKey(), entry.getValueObject(), entry.getValueObject().getClass().getSimpleName()));
		}
				
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:conf/service-context.xml", "classpath*:conf/components-*-context.xml"});
		ComponentFactory componentFactory = (ComponentFactory)context.getBean("componentFactory");
		Object component = componentFactory.getComponent(req.getJobType());
		ComponentInvoker helper = new ComponentInvoker();
		//Invoke and return taskResult
		System.out.println("Invoking " + req.getJobType());
		helper.invoke(component, req);
		System.out.println("Returned " + req.isSuccess());
		
		System.out.println("response entries:");
		for(ObjectEntry entry : req.getResponseEntries())
		{
			System.out.println(MessageFormat.format("{0} = : {1} [{2}]", entry.getKey(), entry.getValueObject(), entry.getValueObject().getClass().getSimpleName()));
		}
		
	}

}
