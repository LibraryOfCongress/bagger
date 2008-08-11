package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="ServiceContainerRegistration")
@Table(name = "service_container_registry")
public class ServiceContainerRegistrationImpl implements Serializable, ServiceContainerRegistration {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "service_url", nullable = false, length=100)
	private String serviceUrl;

	public ServiceContainerRegistrationImpl() {
	
	}
	
	public ServiceContainerRegistrationImpl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}
	
	public void setServiceUrl(String serviceUrl) {
		this.serviceUrl = serviceUrl;
	}

	public String getServiceUrl() {
		return serviceUrl;
	}
	
	
}
