package gov.loc.repository.serviceBroker.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import gov.loc.repository.service.component.impl.ComponentRequestImpl;
import gov.loc.repository.serviceBroker.ServiceRequest;

@Entity(name="ServiceRequest")
@Table(name = "service_request")
public class ServiceRequestImpl extends ComponentRequestImpl implements ServiceRequest, Serializable {

	private static final long serialVersionUID = 1L;

	@Id @GeneratedValue
	@Column(name = "pkey", nullable = false)
	private Long key;

	@Column(name = "requester", nullable = false, length=50)
	private String requester;
	@Column(name = "responder", nullable = true, length=50)
	private String responder = null;
	@Column(name = "correlation_key", nullable = false)
	private String correlationKey;
	@Column(name = "queue", nullable = false, length=50)
	private String queue;
			
	@Column(name = "request_date", nullable = false)
	private Date requestDate;
	@Column(name = "request_acknowledged_date", nullable = true)
	private Date requestAcknowledgedDate = null;
	@Column(name = "response_date", nullable = true)
	private Date responseDate = null;
	@Column(name = "response_acknowledged_date", nullable = true)
	private Date responseAcknowledgedDate = null;

	@Column(name = "is_suspended", nullable = false)
	private Boolean isSuspended = false;
	
	public ServiceRequestImpl() {
	}
		
	public ServiceRequestImpl(String correlationKey, String queue, String jobType) {
		super(jobType);
		this.correlationKey = correlationKey;
		this.queue = queue;
	}
		
	public Long getKey()
	{
		return this.key;
	}
		
	@Override
	public void request(String requester) {
		this.requester = requester;
		this.requestDate = Calendar.getInstance().getTime();
	}
	
	public void acknowledgeRequest(String responder)
	{
		if (this.isSuspended)
		{
			throw new IllegalStateException("Service Request is suspended.");
		}

		if (this.responder != null)
		{
			throw new IllegalStateException("This request has already been acknowledged.");
		}
		this.responder = responder;
		this.requestAcknowledgedDate = Calendar.getInstance().getTime();
	}
	
	@Override
	public void respondSuccess(boolean isSuccess)
	{
		super.respondSuccess(isSuccess);
		this.responseDate = Calendar.getInstance().getTime();
	}
	
	@Override
	public void respondFailure(String errorMessage, String errorDetail)
	{
		super.respondFailure(errorMessage, errorDetail);
		this.responseDate = Calendar.getInstance().getTime();
	}
	
	public void acknowledgeResponse()
	{
		if (this.isSuspended)
		{
			throw new IllegalStateException("Service Request is suspended.");
		}
		if (this.responseAcknowledgedDate != null)
		{
			throw new IllegalStateException("Response is already acknowledged."); 
		}
		this.responseAcknowledgedDate = Calendar.getInstance().getTime();
	}
	
	public void resubmit()
	{
		this.responder = null;
		this.requestAcknowledgedDate = null;
		this.responseDate = null;
		this.isSuccess = null;
		this.errorMessage = null;
		this.errorDetail = null;
		this.responseAcknowledgedDate = null;
	}

	public String getRequester() {
		return requester;
	}

	public String getResponder() {
		return responder;
	}

	public String getCorrelationKey() {
		return correlationKey;
	}

	public String getQueue() {
		return queue;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public Date getRequestAcknowledgedDate() {
		return requestAcknowledgedDate;
	}

	public Date getResponseDate() {
		return responseDate;
	}

	public Date getResponseAcknowledgedDate() {
		return responseAcknowledgedDate;
	}
	
	@Override
	public String toString() {
		String s = MessageFormat.format("Service Request with key {0}, correlation key {1}, requester {2}, jobType {3} and queue {4}.  Request string entries contains {5}.  Request integer entries contains {6}.  Request boolean entries contains {7}.", this.key, this.correlationKey, this.requester, this.jobType, this.queue, collectionToString(this.requestStringEntries), collectionToString(this.requestIntegerEntries), collectionToString(this.requestBooleanEntries));
		if (responder != null)
		{
			s+= MessageFormat.format(" Responder is {0}.", this.responder);
		}
		if (isSuccess != null)
		{
			s+= MessageFormat.format(" IsSuccess is {0}.", isSuccess);
			if (errorMessage != null)
			{
				s+= MessageFormat.format(" ErrorMessage is {0}. ErrorDetail is {1}.", errorMessage, errorDetail);
			}
			s+= MessageFormat.format(" Response string entries contains {0}.  Response integer entries contains {1}.  Response boolean entries contains {2}.", collectionToString(this.responseStringEntries), collectionToString(this.responseIntegerEntries), collectionToString(this.responseBooleanEntries));
		}		  		  
		return s;
		
	}
				
	@Override
	public Boolean isSuspended() {
		return this.isSuspended;
	}
	
	@Override
	public void resume() {
		this.isSuspended = false;		
	}
	
	@Override
	public void suspend() {
		this.isSuspended = true;		
	}
	
}
