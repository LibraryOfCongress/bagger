package gov.loc.repository.serviceBroker.dao.impl;

import java.util.Calendar;
import java.util.Date;

import gov.loc.repository.serviceBroker.AbstractServiceBrokerTest;
import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
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
		ServiceRequest req1 = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req1.request(REQUESTER_1);
		broker.save(req1);
		ServiceRequest req2 = this.serviveBrokerFactory.createServiceRequest("2", QUEUE_1, JOBTYPE_1);
		req2.request(REQUESTER_1);
		req2.acknowledgeRequest(RESPONDER_1);
		broker.save(req2);
		ServiceRequest req3 = this.serviveBrokerFactory.createServiceRequest("3", QUEUE_1, JOBTYPE_1);
		req3.request(REQUESTER_1);
		req3.acknowledgeRequest(RESPONDER_1);
		req3.respondSuccess(true);
		broker.save(req3);
		ServiceRequest req4 = this.serviveBrokerFactory.createServiceRequest("4", QUEUE_1, JOBTYPE_1);
		req4.request(REQUESTER_1);
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
	public void testFindNextServiceRequest()
	{
		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));

		ServiceRequest req1 = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_2, JOBTYPE_1);
		req1.request(REQUESTER_1);
		broker.save(req1);		

		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));

		ServiceRequest req2 = this.serviveBrokerFactory.createServiceRequest("2", QUEUE_1, JOBTYPE_2);
		req2.request(REQUESTER_1);
		broker.save(req2);
		
		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));
		
		ServiceRequest req5 = this.serviveBrokerFactory.createServiceRequest("5", QUEUE_1, JOBTYPE_1);
		req5.request(REQUESTER_1);
		req5.suspend();
		broker.save(req5);
		
		assertNull(broker.findNextServiceRequest(new String[] {QUEUE_1}, new String[] {JOBTYPE_1}, RESPONDER_1));
		
		ServiceRequest req3 = this.serviveBrokerFactory.createServiceRequest("3", QUEUE_1, JOBTYPE_1);
		req3.request(REQUESTER_1);
		broker.save(req3);
		
		ServiceRequest req4 = this.serviveBrokerFactory.createServiceRequest("4", QUEUE_1, JOBTYPE_1);
		req4.request(REQUESTER_1);
		broker.save(req4);
		
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
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_2, JOBTYPE_1);
		req.request(REQUESTER_1);
		req.acknowledgeRequest(RESPONDER_1);
		broker.save(req);
		
		assertEquals(1, broker.findAcknowledgedServiceRequestsWithoutResponses(RESPONDER_1).size());
	}
	
	@Test
	public void testFindNextServiceRequestWithResponse()
	{
		assertNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));
		ServiceRequest req1 = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_2, JOBTYPE_1);
		req1.request(REQUESTER_1);
		req1.acknowledgeRequest(RESPONDER_1);
		req1.respondSuccess(true);
		req1.suspend();
		broker.save(req1);
				
		assertNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));

		ServiceRequest req2 = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_2, JOBTYPE_1);
		req2.request(REQUESTER_1);
		req2.acknowledgeRequest(RESPONDER_1);
		req2.respondSuccess(true);
		broker.save(req2);
		
		assertNotNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));
		
		req2.acknowledgeResponse();
		broker.save(req2);
		
		assertNull(broker.findNextServiceRequestWithResponse(REQUESTER_1));
	}
	
	@Test
	public void testFindServiceRequestsByCorrelationKey()
	{
		assertTrue(broker.findServiceRequests(REQUESTER_1, "1").isEmpty());
		
		ServiceRequest req1 = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req1.request(REQUESTER_1);
		broker.save(req1);
		assertEquals(1, broker.findServiceRequests(REQUESTER_1, "1").size());

		ServiceRequest req2 = this.serviveBrokerFactory.createServiceRequest("1", QUEUE_1, JOBTYPE_1);
		req2.request(REQUESTER_1);
		broker.save(req2);
		assertEquals(2, broker.findServiceRequests(REQUESTER_1, "1").size());
		
		ServiceRequest req3 = this.serviveBrokerFactory.createServiceRequest("2", QUEUE_1, JOBTYPE_1);
		req3.request(REQUESTER_1);
		broker.save(req3);
		assertEquals(2, broker.findServiceRequests(REQUESTER_1, "1").size());
				
	}
	
	@Test
	public void testFindServiceRequest()
	{
		ServiceRequest req = this.serviveBrokerFactory.createServiceRequest("1", JOBTYPE_1, JOBTYPE_2);
		req.request(REQUESTER_1);
		broker.save(req);
		
		assertNotNull(req.getKey());
		assertNotNull(broker.findServiceRequest(req.getKey()));

	}
	
	@Test
	public void testRegistration() throws Exception
	{
		assertTrue(broker.findServiceContainerRegistrations(null).isEmpty());
		ServiceContainerRegistration registration = this.serviveBrokerFactory.createServiceContainerRegistration(HOST_1, 9874);
		assertEquals("service:jmx:hessian://" + HOST_1 + ":9874/", registration.getServiceUrl());
		broker.save(registration);
		assertEquals(1, broker.findServiceContainerRegistrations(null).size());
		assertEquals(1, broker.findServiceContainerRegistrations(300000L).size());
		Thread.sleep(5000);		
		assertEquals(0, broker.findServiceContainerRegistrations(3000L).size());
		assertEquals(HOST_1, broker.findServiceContainerRegistrations(null).get(0).getHost());
		//Make sure can save twice without throwing error
		broker.save(this.serviveBrokerFactory.createServiceContainerRegistration(HOST_1, 9874));
		
		broker.save(this.serviveBrokerFactory.createServiceContainerRegistration(HOST_2, 9874));
		assertEquals(2, broker.findServiceContainerRegistrations(null).size());
		
		broker.delete(registration);
		assertEquals(1, broker.findServiceContainerRegistrations(null).size());
		
		//Make sure can delete twice without throwing error		
		broker.delete(registration);				
	}
}
