package gov.loc.repository.serviceBroker.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.loc.repository.serviceBroker.ServiceBrokerFactory;
import gov.loc.repository.serviceBroker.ServiceContainerRegistration;
import gov.loc.repository.serviceBroker.ServiceContainerRegistry;
import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

@Component("serviceContainerRegistry")
public class ServiceContainerRegistryImpl implements ServiceContainerRegistry {

	private ServiceRequestDAO dao;
	private ServiceBrokerFactory factory = new ServiceBrokerFactory();
	
	@Autowired
	public ServiceContainerRegistryImpl(ServiceRequestDAO dao) {
		this.dao = dao;
	}
	
	@Override
	public List<String> listServiceContainers() {
		List<String> serviceContainers = new ArrayList<String>();
		List<ServiceContainerRegistration> registrations = this.dao.findServiceContainerRegistrations();
		for(ServiceContainerRegistration registration : registrations)
		{
			serviceContainers.add(registration.getServiceUrl());
		}
		return serviceContainers;
	}

	@Override
	public void register(String serviceUrl) {
		this.dao.save(factory.createServiceContainerRegistration(serviceUrl));

	}

	@Override
	public void unregister(String serviceUrl) {
		this.dao.delete(factory.createServiceContainerRegistration(serviceUrl));

	}

}
