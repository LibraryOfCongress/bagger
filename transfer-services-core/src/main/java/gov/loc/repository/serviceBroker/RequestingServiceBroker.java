package gov.loc.repository.serviceBroker;

public interface RequestingServiceBroker {

	public void setRequester(String requester);
	
	public void sendRequest(ServiceRequest req);
	
	public ServiceRequest findAndAcknowledgeNextServiceRequestWithResponse();
	
}
