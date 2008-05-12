package gov.loc.repository.serviceBroker;

import gov.loc.repository.serviceBroker.impl.ServiceContainerRegistrationImpl;
import gov.loc.repository.serviceBroker.impl.ServiceRequestImpl;

public class ServiceBrokerFactory {

	public ServiceRequest createServiceRequest(String requester, String correlationKey, String queue, String jobType)
	{
		return new ServiceRequestImpl(requester, correlationKey, queue, jobType);
	}
	
	public ServiceContainerRegistration createServiceContainerRegistration(String serviceUrl)
	{
		return new ServiceContainerRegistrationImpl(serviceUrl);
	}
}
