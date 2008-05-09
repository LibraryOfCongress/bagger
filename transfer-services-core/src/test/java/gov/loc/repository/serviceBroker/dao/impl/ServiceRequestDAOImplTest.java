package gov.loc.repository.serviceBroker.dao.impl;

import gov.loc.repository.serviceBroker.AbstractServiceBrokerTest;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.*;

public class ServiceRequestDAOImplTest extends AbstractServiceBrokerTest {

	@Autowired
	private ServiceRequestDAO broker;
	
	@Test
	public void testFindServiceRequests()
	{
		assertTrue(broker.findServiceRequests(true, true, true).isEmpty());
		ServiceRequest req1 = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", QUEUE_1, JOBTYPE_1);
		broker.save(req1);
		ServiceRequest req2 = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "2", QUEUE_1, JOBTYPE_1);
		req2.acknowledgeRequest(RESPONDER_1);
		broker.save(req2);
		ServiceRequest req3 = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "3", QUEUE_1, JOBTYPE_1);
		req3.acknowledgeRequest(RESPONDER_1);
		req3.respondSuccess(true);
		broker.save(req3);
		ServiceRequest req4 = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "4", QUEUE_1, JOBTYPE_1);
		req4.acknowledgeRequest(RESPONDER_1);
		req4.respondSuccess(true);
		req4.acknowledgeResponse();
		broker.save(req4);
		
		assertEquals(4, broker.findServiceRequests(true, true, true).size());
		assertEquals(3, broker.findServiceRequests(true, true, false).size());
		assertEquals(2, broker.findServiceRequests(true, false, false).size());
		assertEquals(1, broker.findServiceRequests(false, false, false).size());
		
	}

	@Test
	public void testFindAndAcknowledgeNextServiceRequest()
	{
		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));
		
		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", QUEUE_2, JOBTYPE_1));		

		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));

		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "2", QUEUE_1, JOBTYPE_2));
		
		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));
		
		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "3", QUEUE_1, JOBTYPE_1));
		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "4", QUEUE_1, JOBTYPE_1));
		
		ServiceRequest req = broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1);
		assertNotNull(req);
		assertEquals("3", req.getCorrelationKey());
		//assertEquals(RESPONDER_1, req.getResponder());
		/*
		req = broker.findAndAcknowledgeNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1);
		assertNotNull(req);
		assertEquals("4", req.getCorrelationKey());
		assertEquals(RESPONDER_1, req.getResponder());
		
		assertNull(broker.findAndAcknowledgeNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));
		*/
	}
	
	@Test
	public void testFindAcknowledgedServiceRequestsWithoutResponses()
	{
		assertTrue(broker.findAcknowledgedServiceRequestsWithoutResponses(RESPONDER_1).isEmpty());
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", QUEUE_2, JOBTYPE_1);
		req.acknowledgeRequest(RESPONDER_1);
		broker.save(req);
		
		assertEquals(1, broker.findAcknowledgedServiceRequestsWithoutResponses(RESPONDER_1).size());
	}
	
	@Test
	public void testFindNextServiceRequestWithResponse()
	{
		assertNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", QUEUE_2, JOBTYPE_1);
		req.acknowledgeRequest(RESPONDER_1);
		req.respondSuccess(true);
		broker.save(req);
		
		assertNotNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));
		
		req.acknowledgeResponse();
		broker.save(req);
		
		assertNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));
	}
	
	@Test
	public void testFindServiceRequestsByCorrelationKey()
	{
		assertTrue(broker.findServiceRequests("1").isEmpty());
		
		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", QUEUE_1, JOBTYPE_1));
		assertEquals(1, broker.findServiceRequests("1").size());

		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", QUEUE_1, JOBTYPE_1));
		assertEquals(2, broker.findServiceRequests("1").size());
		
		broker.save(this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "2", QUEUE_1, JOBTYPE_1));
		assertEquals(2, broker.findServiceRequests("1").size());
				
	}
	
	@Test
	public void testFindServiceRequest()
	{
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest(REQUESTER_1, "1", JOBTYPE_1, JOBTYPE_2);
		broker.save(req);
		
		assertNotNull(req.getKey());
		assertNotNull(broker.findServiceRequest(req.getKey()));

	}
}
