package gov.loc.repository.serviceBroker;

public interface RespondingServiceBroker {
	
	public void setResponder(String responder);
		
	public void setQueues(String[] queues);
	
	public void setJobTypes(String[] jobTypes);
	
	public ServiceRequest findAndAcknowledgeNextServiceRequest();
	
	public void sendResponse(ServiceRequest req);
	
	/*
	 * To be called at startup to make sure that no requests were left uncompleted, e.g., by an unexpected shutdown.
	 */
	public void reportErrorsForAcknowledgedServiceRequestsWithoutResponses();

}
