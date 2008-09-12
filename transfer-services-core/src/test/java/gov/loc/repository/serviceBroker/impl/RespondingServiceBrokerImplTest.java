package gov.loc.repository.serviceBroker.impl;

import static org.junit.Assert.*;
import gov.loc.repository.serviceBroker.AbstractServiceBrokerTest;
import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RespondingServiceBrokerImplTest extends AbstractServiceBrokerTest {

	@Autowired
	private RespondingServiceBroker broker;
	
	@Autowired
	private ServiceRequestDAO dao;
	
	@Before
	public void setup()
	{
		broker.setJobTypes(new String[] {JOBTYPE_1});
		broker.setQueues(new String[] {QUEUE_1});
	}
	
	@Test
	public void testFindAndAcknowledgeNextServiceRequest() {
		assertTrue(this.dao.findServiceRequests(false, false, false).isEmpty());
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.request(REQUESTER_1);
		this.dao.save(req);
		assertFalse(this.dao.findServiceRequests(false, false, false).isEmpty());
		
		req = this.broker.findAndAcknowledgeNextServiceRequest();
		assertNotNull(req);
		assertEquals(RESPONDER_1, req.getResponder());
		
		assertNull(this.broker.findAndAcknowledgeNextServiceRequest());
		assertTrue(this.dao.findServiceRequests(false, false, false).isEmpty());
	}

	@Test
	public void testReportErrorsForAcknowledgedServiceRequestsWithoutResponses() {
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.request(REQUESTER_1);
		req.acknowledgeRequest(RESPONDER_1);
		this.dao.save(req);
		assertEquals(1, dao.findAcknowledgedServiceRequestsWithoutResponses(RESPONDER_1).size());
		
		broker.reportErrorsForAcknowledgedServiceRequestsWithoutResponses();
		
		req = this.dao.findServiceRequest(req.getKey());
		assertNotNull(req);
		assertFalse(req.isSuccess());
	}

	@Test
	public void testSendResponse() {
		assertTrue(this.dao.findServiceRequests(true, true, true).isEmpty());
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.request(REQUESTER_1);
		req.acknowledgeRequest(RESPONDER_1);
		req.respondSuccess(true);
		broker.sendResponse(req);
		
		assertEquals(1, this.dao.findServiceRequests(true, true, true).size());
	}

}
