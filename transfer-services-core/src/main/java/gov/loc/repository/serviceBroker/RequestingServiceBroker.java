package gov.loc.repository.serviceBroker;

public interface RequestingServiceBroker {

	public void sendRequest(ServiceRequest req);
	
	public ServiceRequest findAndAcknowledgeNextServiceRequestWithResponse();

	public String getRequester();
	
}
