package gov.loc.repository.service;

import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gov.loc.repository.utilities.ConfigurationFactory;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ComponentContainerDriver {


	public static void main(String[] args) throws Exception {
		
		List<URL> contextUrlList = ConfigurationFactory.findWildcardResourceList("services-context-*.xml");
		List<String> contextLocationList = new ArrayList<String>();
		for(URL url : contextUrlList)
		{
			String contextLocation = url.toString().substring(url.toString().lastIndexOf("/") + 1);
			contextLocationList.add(contextLocation);
		}
		
		ApplicationContext context = new ClassPathXmlApplicationContext(contextLocationList.toArray(new String[0]));
		ComponentContainer container = (ComponentContainer)context.getBean("componentContainer");
		
		String[] queueArray = container.getQueues();
		System.out.println("Queues:");
		for(String queue : queueArray)
		{
			System.out.println(queue);
		}
		
		Set<String> jobTypeList = container.getJobTypeList(); 
		System.out.println("Job Types:");
		for(String jobType : jobTypeList)
		{
			System.out.println(jobType);
		}
		
		System.out.println("Starting");
		container.start();
				
		ComponentContainerDriver driver = new ComponentContainerDriver();
		ShutdownHook shutdownHook = driver.new ShutdownHook(container);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        
        while(true)
        {
        	System.out.println(MessageFormat.format("Running ({0} active consumers)", container.getScheduledConsumersCount()));
        	Thread.sleep(15000);
        }
	}

	public class ShutdownHook extends Thread {
		private ComponentContainer container;
		
		public ShutdownHook(ComponentContainer container) {
			this.container = container;
		}
		
	    public void run() {
	        System.out.println("Stopping");
	        container.stop();
	    }
	}
}
