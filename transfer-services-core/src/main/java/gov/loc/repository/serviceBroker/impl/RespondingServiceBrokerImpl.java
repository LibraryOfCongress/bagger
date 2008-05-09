package gov.loc.repository.serviceBroker.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.loc.repository.serviceBroker.RespondingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

@Component("respondingServiceBroker")
public class RespondingServiceBrokerImpl implements RespondingServiceBroker {

	private ServiceRequestDAO dao;
	private String responder;
	private String[] queues;
	private String[] jobTypes;
	
	@Autowired
	public RespondingServiceBrokerImpl(ServiceRequestDAO dao) {
		this.dao = dao;
	}
	
	@Override
	@Transactional
	public ServiceRequest findAndAcknowledgeNextServiceRequest() {
		ServiceRequest req = this.dao.findNextServiceRequest(queues, jobTypes, responder);
		if (req != null)
		{
			req.acknowledgeRequest(responder);
			this.dao.save(req);
		}
		return req;
	}

	@Override
	@Transactional
	public void reportErrorsForAcknowledgedServiceRequestsWithoutResponses() {
		List<ServiceRequest> reqList = this.dao.findAcknowledgedServiceRequestsWithoutResponses(responder);
		for(ServiceRequest req : reqList)
		{
			req.respondFailure("Failure by responder", "This request was found acknowledged, but not responded to by the responder when it started up.  This is probably due to a previous failure by the responder that left the service request incomplete.");
			this.dao.save(req);
		}		
	}

	@Override
	public void sendResponse(ServiceRequest req) {
		this.dao.save(req);
	}

	@Override
	public void setJobTypes(String[] jobTypes) {
		this.jobTypes = jobTypes;

	}

	@Override
	public void setQueues(String[] queues) {
		this.queues = queues;
	}

	@Override
	public void setResponder(String responder) {
		this.responder = responder;
	}

}
