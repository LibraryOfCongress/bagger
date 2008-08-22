package gov.loc.repository.serviceBroker.dao;

import java.util.List;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
import gov.loc.repository.serviceBroker.ServiceRequest;

public interface ServiceRequestDAO {
	
	public void save(ServiceRequest req);
	
	public List<ServiceRequest> findServiceRequests(boolean includeRequestAcknowledged, boolean includeResponded, boolean includeResponseAcknowledged);
	
	public ServiceRequest findNextServiceRequest(String[] queues, String[] jobTypes, String responder);
	
	public List<ServiceRequest> findAcknowledgedServiceRequestsWithoutResponses(String responder);
	
	public ServiceRequest findNextServiceRequestWithResponse(String requester);
	
	public List<ServiceRequest> findServiceRequests(String requester, String correlationKey);
	
	public ServiceRequest findServiceRequest(Long key);
	
	public void save(ServiceContainerRegistration registration);
	
	public void delete(ServiceContainerRegistration registration);
	
	public List<ServiceContainerRegistration> findServiceContainerRegistrations(Long latency);
}
