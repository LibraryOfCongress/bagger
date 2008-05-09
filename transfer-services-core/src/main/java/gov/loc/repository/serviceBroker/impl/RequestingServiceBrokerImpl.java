package gov.loc.repository.serviceBroker.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

@Component("requestServiceBroker")
public class RequestingServiceBrokerImpl implements RequestingServiceBroker {

	private ServiceRequestDAO dao;
	private String requester;
	
	@Autowired
	public RequestingServiceBrokerImpl(ServiceRequestDAO dao) {
		this.dao = dao;
	}
	
	@Override
	@Transactional
	public ServiceRequest findAndAcknowledgeNextServiceRequestWithResponse() {
		ServiceRequest req = dao.findNextServiceRequestWithResponse(this.requester);
		if (req != null)
		{
			req.acknowledgeResponse();
			dao.save(req);
		}
		return req;
	}

	@Override
	public void sendRequest(ServiceRequest req) {
		this.dao.save(req);
	}

	@Override
	public void setRequester(String requester) {
		this.requester = requester;
	}
	
}
