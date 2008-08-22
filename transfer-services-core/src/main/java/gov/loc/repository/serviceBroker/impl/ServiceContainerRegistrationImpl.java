package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="ServiceContainerRegistration")
@Table(name = "service_container_registry")
public class ServiceContainerRegistrationImpl implements Serializable, ServiceContainerRegistration {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "host", nullable = false, length=100)
	private String host;

	@Column(name="port", nullable = false)
	private Integer port;
	
	@Column(name="timestamp", nullable = false)
	private Date timestamp = Calendar.getInstance().getTime();
	
	public ServiceContainerRegistrationImpl() {
	
	}
	
	public ServiceContainerRegistrationImpl(String host, Integer port) {
		this.host = host;
		this.port = port;
	}

	

	public String getServiceUrl() {
		 return MessageFormat.format("service:jmx:hessian://{0}:{1}/", this.host, this.port.toString().replaceAll(",", ""));
	}

	@Override
	public void beat() {
		this.timestamp = Calendar.getInstance().getTime();
		
	}

	@Override
	public String getHost() {
		return this.host;
	}

	@Override
	public Integer getPort() {
		return this.port;
	}

	@Override
	public Date getTimestamp() {
		return this.timestamp;
	}

	@Override
	public void setHost(String host) {
		this.host = host;
		
	}

	@Override
	public void setPort(Integer port) {
		this.port = port;
		
	}
	
	
}
