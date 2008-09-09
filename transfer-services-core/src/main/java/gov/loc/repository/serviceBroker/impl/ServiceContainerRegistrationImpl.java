package gov.loc.repository.serviceBroker.impl;

import gov.loc.repository.serviceBroker.ServiceContainerRegistration;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.annotations.Generated;
import org.hibernate.annotations.GenerationTime;
import org.hibernate.annotations.SQLInsert;
import org.hibernate.annotations.SQLUpdate;

@Entity(name="ServiceContainerRegistration")
@Table(name = "service_container_registry")
@SQLInsert(sql="INSERT INTO service_container_registry(beat_count, port,host,timestamp) VALUES(?,?,?,CURRENT_TIMESTAMP)")
@SQLUpdate(sql="UPDATE service_container_registry SET beat_count=?, port=?, timestamp=CURRENT_TIMESTAMP WHERE host=?")
public class ServiceContainerRegistrationImpl implements Serializable, ServiceContainerRegistration {

	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name = "host", nullable = false, length=100)
	private String host;

	@Column(name="port", nullable = false)
	private Integer port;
	
	@Generated(GenerationTime.ALWAYS)
	@Column(name="timestamp", nullable = true, insertable=false, updatable=false)
	private Date timestamp;
	
	@Column(name="beat_count", nullable = false)
	private Long beatCount = 0L;
	
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
		this.beatCount = this.beatCount + 1;
		
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
	
	@Override
	public Long getBeatCount() {
		return this.beatCount;
	}
}
