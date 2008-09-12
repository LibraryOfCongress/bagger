package gov.loc.repository.service.drivers;

import gov.loc.repository.service.component.ComponentFactory;
import gov.loc.repository.service.component.ComponentInvoker;
import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class DelegationDriver {

	private static final Log log = LogFactory.getLog(DelegationDriver.class);
	
	public static void main(String[] args) throws Exception {
		if (args.length != 1)
		{
			System.err.println("Requires a single service request key");
			log.error("Requires a single service request key");
			System.exit(1);
		}
		Long key = Long.parseLong(args[0]);
		ApplicationContext context = new ClassPathXmlApplicationContext(new String[] {"classpath:conf/delegate-context.xml", "classpath*:conf/components-*-context.xml"});
		ComponentFactory componentFactory = (ComponentFactory)context.getBean("componentFactory");
		RespondingServiceBroker broker = (RespondingServiceBroker)context.getBean("respondingServiceBroker");
		ServiceRequest req = broker.findRequiredServiceRequest(key);
		if (req.getResponseDate() != null)
		{
			System.err.println("Request has already been responded to.");
			log.error("Request has already been responded to.");
			System.exit(1);
		}
		
		
		Object component = null;
		try {
			component = componentFactory.getComponent(req.getJobType());
		} catch (Exception ex) {
			req.respondFailure(ex);
		}
		if (component != null)
		{
			ComponentInvoker helper = new ComponentInvoker();
			//Invoke and return taskResult
			log.info("Received request: " + req);
			System.out.println("Starting " + req);
			helper.invoke(component, req);
		}
		
		log.info("Responding to request: " + req);
		System.out.println("Responding " + req);
		broker.sendResponse(req);
		
	}

}
