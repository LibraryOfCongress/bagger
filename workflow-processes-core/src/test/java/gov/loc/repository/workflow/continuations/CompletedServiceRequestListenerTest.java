package gov.loc.repository.workflow.continuations;

import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.impl.ServiceRequestImpl;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.continuations.CompletedServiceRequestListener.State;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;

@RunWith(JMock.class)
public class CompletedServiceRequestListenerTest {
	Mockery context = new JUnit4Mockery();
	private CompletedServiceRequestListener listener;
	
	@Test
	public void testListen() throws Exception
	{
		//RequestingServiceBroker
		final RequestingServiceBroker broker = context.mock(RequestingServiceBroker.class);
		final ServiceRequest req1 = new ServiceRequestImpl("1", "jobqueue", "test");
		req1.request("foo");
		req1.acknowledgeRequest("bar");
		req1.respondSuccess(true);
		final ServiceRequest req2 = new ServiceRequestImpl("2", "jobqueue", "test");
		req2.request("foo");
		req2.acknowledgeRequest("bar");
		req2.respondFailure(new Exception("Ooops"));
				
		context.checking(new Expectations() {{
			one(broker).findAndAcknowledgeNextServiceRequestWithResponse();
			will(returnValue(req1));
			one(broker).findAndAcknowledgeNextServiceRequestWithResponse();
			will(returnValue(req2));
			allowing(broker).findAndAcknowledgeNextServiceRequestWithResponse();
			will(returnValue(null));			
		}});
	
		final SimpleContinuationController controller = context.mock(SimpleContinuationController.class);
		context.checking(new Expectations() {{
			one(controller).invoke(with(equal(1L)), with(equal(true)));
			one(controller).invoke(with(equal(2L)), with(any(String.class)), with(any(String.class)));
		}});		
		
		//ContinuationController
		listener = new CompletedServiceRequestListener(broker, controller);
		listener.start();
		assertTrue(State.STARTED == listener.getState() || State.STARTING == listener.getState());
		Thread.sleep(2000L);
		listener.stop();
		assertTrue(State.STOPPED == listener.getState() || State.STOPPING == listener.getState());
	}
	
}
