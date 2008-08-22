package gov.loc.repository.service.drivers;

import java.text.MessageFormat;

import gov.loc.repository.service.container.ServiceContainer;
import gov.loc.repository.service.container.ServiceContainer.State;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class ServiceContainerDriver {

	public static void main(String[] args) throws Exception {
		
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:conf/servicecontainer-context.xml", "classpath*:conf/components-*-context.xml"});
		ServiceContainer container = (ServiceContainer)context.getBean("serviceContainer");
		
		String[] queues = container.getQueues();
		System.out.println("Queues:");
		for(String queue : queues)
		{
			System.out.println(queue);
		}
		
		String[] jobTypes = container.getJobTypes(); 
		System.out.println("Job Types:");
		for(String jobType : jobTypes)
		{
			System.out.println(jobType);
		}
		
		container.start();
				
		ServiceContainerDriver driver = new ServiceContainerDriver();
		ShutdownHook shutdownHook = driver.new ShutdownHook(container);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        
        while(container.getState() != State.SHUTDOWN)
        {
        	System.out.println(MessageFormat.format("{0} ({1} active requests)", container.getState(), container.getActiveServiceRequestCount()));
        	Thread.sleep(10000);
        }
	}

	public class ShutdownHook extends Thread {
		private ServiceContainer container;
		
		public ShutdownHook(ServiceContainer container) {
			this.container = container;
		}
		
	    public void run() {
	        System.out.println("Shutting down");
	        container.shutdown();
	        
	    }
	}
}
