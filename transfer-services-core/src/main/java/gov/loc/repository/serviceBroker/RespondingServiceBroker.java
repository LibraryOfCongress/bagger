package gov.loc.repository.serviceBroker;

public interface RespondingServiceBroker {
	
	public ServiceRequest findAndAcknowledgeNextServiceRequest();
	
	public void sendResponse(ServiceRequest req);
	
	/*
	 * To be called at startup to make sure that no requests were left uncompleted, e.g., by an unexpected shutdown.
	 */
	public void reportErrorsForAcknowledgedServiceRequestsWithoutResponses();

	public String getResponder();
	
	public String[] getQueues();
	
	public String[] getJobTypes();
	
}
