package gov.loc.repository.service;

import gov.loc.repository.service.ServiceContainer.State;
import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceBrokerFactory;
import gov.loc.repository.serviceBroker.ServiceRequest;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/services-test-context.xml","classpath:conf/servicecontainer-context.xml"})
public class ServiceContainerTest {

	private static final String QUEUE = "jobqueue";
	private static final String JOBTYPE = "test";
	
	@Autowired
	private ServiceContainer container;
	
	@Autowired
	private RequestingServiceBroker requestingBroker;
	
	private ServiceBrokerFactory factory = new ServiceBrokerFactory();
		
	@Before
	public void createMessages() throws Exception
	{
		for(int i=0; i < 20; i++)
		{
			ServiceRequest req = factory.createServiceRequest(Integer.toString(i), QUEUE, JOBTYPE);
			req.addString("message", "foo");
			req.addBoolean("istrue", true);
			req.addInteger("key", 1L);
			this.requestingBroker.sendRequest(req);
		}		
	}

	
	@Test(timeout=10000)
	public void testProcessRequests() throws Exception
	{
		container.start();
		assertTrue(container.getState() == State.STARTING || container.getState() == State.STARTED);

		int i = 0;
		while(i < 20)
		{
			ServiceRequest req = requestingBroker.findAndAcknowledgeNextServiceRequestWithResponse();
			if (req != null)
			{
				i++;
				assertTrue(req.isSuccess());
			}			
		}
		
		container.shutdown();		
		assertTrue(container.getState() == State.SHUTTINGDOWN || container.getState() == State.SHUTDOWN);
	}
	
}
