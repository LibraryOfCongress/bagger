package gov.loc.repository.serviceBroker;

import gov.loc.repository.serviceBroker.impl.ServiceContainerRegistrationImpl;
import gov.loc.repository.serviceBroker.impl.ServiceRequestImpl;

public class ServiceBrokerFactory {

	public ServiceRequest createServiceRequest(String correlationKey, String queue, String jobType)
	{
		return new ServiceRequestImpl(correlationKey, queue, jobType);
	}
	
	public ServiceContainerRegistration createServiceContainerRegistration(String serviceUrl)
	{
		return new ServiceContainerRegistrationImpl(serviceUrl);
	}
}
