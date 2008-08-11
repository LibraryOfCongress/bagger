package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.AbstractServiceBrokerTest;
import gov.loc.repository.serviceBroker.ServiceRequest;

import org.junit.Test;

import static org.junit.Assert.*;

public class ServiceRequestImplTest extends AbstractServiceBrokerTest {
	
	@Test
	public void testServiceRequest()
	{		
		//A typical request/response pattern
		ServiceRequest req = serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.addRequestString("key1", "value1");
		req.addRequestString("key2", "value2");
		req.addRequestString("key3", null);
		req.addRequestInteger("key4", 1L);
		req.addRequestInteger("key5", 2L);
		req.addRequestInteger("key6", null);
		req.addRequestBoolean("key7", true);
		req.addRequestBoolean("key8", false);
		req.addRequestBoolean("key9", null);		
		req.request(REQUESTER_1);
		
		template.save(req);
		template.refresh(req);
		
		assertEquals(REQUESTER_1, req.getRequester());
		assertEquals("1", req.getCorrelationKey());
		assertEquals(QUEUE_1, req.getQueue());
		assertEquals(JOBTYPE_1, req.getJobType());
		assertNotNull(req.getRequestDate());
		assertEquals(3, req.getRequestStringEntries().size());
		assertEquals(3, req.getRequestIntegerEntries().size());
		assertEquals(3, req.getRequestBooleanEntries().size());
						
		req.acknowledgeRequest(RESPONDER_1);		
		template.saveOrUpdate(req);
		
		assertEquals(RESPONDER_1, req.getResponder());
		assertNotNull(req.getRequestAcknowledgedDate());
		
		req.respondFailure(new Exception("Darn"));
		req.addResponseString("respkey1", "value1");
		req.addResponseString("respkey2", "value2");
		req.addResponseString("respkey3", null);
		req.addResponseInteger("respkey4", 1L);
		req.addResponseInteger("respkey5", 2L);
		req.addResponseInteger("respkey6", null);
		req.addResponseBoolean("respkey7", true);
		req.addResponseBoolean("respkey8", false);
		req.addResponseBoolean("respkey9", null);		
		
		template.saveOrUpdate(req);
		
		assertFalse(req.isSuccess());
		assertNotNull(req.getErrorMessage());
		assertNotNull(req.getErrorDetail());
		assertEquals(3, req.getResponseStringEntries().size());
		assertEquals(3, req.getResponseIntegerEntries().size());
		assertEquals(3, req.getResponseBooleanEntries().size());
		
		req.acknowledgeResponse();		
		template.saveOrUpdate(req);
		
		assertNotNull(req.getResponseAcknowledgedDate());
		
	}
	
	@Test(expected=IllegalStateException.class)
	public void testSuspendRequestAcknowledge()
	{		
		//A typical request/response pattern
		ServiceRequest req = serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.request(REQUESTER_1);
		
		template.save(req);
		template.refresh(req);

		req.suspend();
		
		template.saveOrUpdate(req);
								
		req.acknowledgeRequest(RESPONDER_1);		
				
	}

	@Test(expected=IllegalStateException.class)
	public void testSuspendResponseAcknowledge()
	{		
		//A typical request/response pattern
		ServiceRequest req = serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.request(REQUESTER_1);
		
		template.save(req);
		template.refresh(req);

		req.acknowledgeRequest(RESPONDER_1);
		req.respondSuccess(true);
		template.saveOrUpdate(req);
		
		req.suspend();
		
		template.saveOrUpdate(req);
		
		req.acknowledgeResponse();
			
	}
	
}
