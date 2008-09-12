package gov.loc.repository.serviceBroker;

import gov.loc.repository.exceptions.RequiredEntityNotFound;

public interface RespondingServiceBroker {
	
	public ServiceRequest findAndAcknowledgeNextServiceRequest();
	
	public void sendResponse(ServiceRequest req);
	
	/*
	 * To be called at startup to make sure that no requests were left uncompleted, e.g., by an unexpected shutdown.
	 */
	public void reportErrorsForAcknowledgedServiceRequestsWithoutResponses();

	public String getResponder();
	
	public void setQueues(String[] queues);
	
	public String[] getQueues();
	
	public void setJobTypes(String[] jobTypes);
	
	public String[] getJobTypes();
	
	public ServiceRequest findRequiredServiceRequest(Long key) throws RequiredEntityNotFound;
	
}
