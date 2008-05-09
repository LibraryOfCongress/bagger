package gov.loc.repository.service;

import gov.loc.repository.service.ServiceContainer.State;

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
		
		System.out.println("Starting");
		container.start();
				
		ServiceContainerDriver driver = new ServiceContainerDriver();
		ShutdownHook shutdownHook = driver.new ShutdownHook(container);
        Runtime.getRuntime().addShutdownHook(shutdownHook);
        
        while(container.getState() != State.STOPPED)
        {
        	System.out.println("Running.");
        	Thread.sleep(5000);
        }
	}

	public class ShutdownHook extends Thread {
		private ServiceContainer container;
		
		public ShutdownHook(ServiceContainer container) {
			this.container = container;
		}
		
	    public void run() {
	        System.out.println("Stopping");
	        container.stop();
	    }
	}
}
