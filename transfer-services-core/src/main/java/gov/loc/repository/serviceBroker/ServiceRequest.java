package gov.loc.repository.serviceBroker;

import gov.loc.repository.service.component.ComponentRequest;

import java.util.Date;

public interface ServiceRequest extends ComponentRequest {

	public Long getKey();
		
	public void request(String requester);
	
	public void acknowledgeRequest(String responder);

	public void acknowledgeResponse();

	public void resubmit();

	public String getRequester();

	public String getResponder();

	public String getCorrelationKey();

	public String getQueue();

	public Date getRequestDate();

	public Date getRequestAcknowledgedDate();

	public Date getResponseDate();

	public Date getResponseAcknowledgedDate();
	
	public Boolean isSuspended();
	
	public void suspend();
	
	public void resume();
		
}
