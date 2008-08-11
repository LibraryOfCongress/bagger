package gov.loc.repository.serviceBroker.impl;

import static org.junit.Assert.*;
import gov.loc.repository.serviceBroker.AbstractServiceBrokerTest;
import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

public class RequestingServiceBrokerImplTest extends AbstractServiceBrokerTest {

	@Autowired
	private RequestingServiceBroker broker;
	
	@Autowired
	private ServiceRequestDAO dao;
		
	@Test
	public void testFindAndAcknowledgeNextServiceRequestWithResponse() {
		assertNull(broker.findAndAcknowledgeNextServiceRequestWithResponse());
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req.request(REQUESTER_1);
		req.acknowledgeRequest(RESPONDER_1);
		req.respondSuccess(true);
		this.dao.save(req);
		
		req = broker.findAndAcknowledgeNextServiceRequestWithResponse();
		assertNotNull(req);
		assertEquals("1", req.getCorrelationKey());
		assertNotNull(req.getResponseAcknowledgedDate());
		
		assertNull(broker.findAndAcknowledgeNextServiceRequestWithResponse());
	}

	@Test
	public void testSendRequest() {
		assertTrue(dao.findServiceRequests(true, true, true).isEmpty());
		this.broker.sendRequest(this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1));
		assertEquals(1, dao.findServiceRequests(true, true, true).size());
		
	}

}
