package gov.loc.repository.workflow.actionhandlers;

import static gov.loc.repository.workflow.constants.FixtureConstants.QUEUE_1;
import static org.junit.Assert.*;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.transfer.components.test.TestComponent;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;

import java.lang.reflect.Proxy;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/servicerequestbroker-context.xml"})
public class ServiceInvocationHandlerTest {
	DummyActionHandler actionHandler= new DummyActionHandler(null);

	Configuration workflowConfig = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	Configuration servicesConfig = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
	
	@Autowired
	RespondingServiceBroker broker;

	
	@Before
	public void setup() throws Exception
	{
		broker.setResponder("TestResponder");
		broker.setJobTypes(new String[] {"test"});
		broker.setQueues(new String[]{QUEUE_1});
		
		workflowConfig.clearProperty("none.TestComponent.queue");
		workflowConfig.addProperty("none.TestComponent.queue", QUEUE_1);		
	}
	
	@Test
	public void testInvoke() throws Exception {

		assertNull(broker.findAndAcknowledgeNextServiceRequest());
				
		TestComponent testComponent = actionHandler.createObject(TestComponent.class);
		assertTrue(Proxy.getInvocationHandler(testComponent) instanceof ServiceInvocationHandler);
		testComponent.test("foo", true, 1L);
		
		Thread.sleep(250);

		ServiceRequest req = broker.findAndAcknowledgeNextServiceRequest();
		assertNotNull(req);
		
		assertEquals("0", req.getCorrelationKey());
		assertEquals("test", req.getJobType());
		assertEquals("foo", req.getStringMap().get("message"));
		assertTrue(req.getBooleanMap().get("istrue"));
		assertEquals(Long.valueOf(1L), req.getIntegerMap().get("key"));
		
	}

}
