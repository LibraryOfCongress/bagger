package gov.loc.repository.serviceBroker;

import java.util.Date;

public interface ServiceContainerRegistration {

	void setHost(String host);
	
	String getHost();
	
	void setPort(Integer port);
	
	Integer getPort();
	
	void beat();
	
	Date getTimestamp();
	
	String getServiceUrl();
	
}