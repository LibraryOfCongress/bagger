package gov.loc.repository.serviceBroker.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.Table;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.MapKey;

import gov.loc.repository.serviceBroker.ServiceRequest;
import gov.loc.repository.utilities.ExceptionHelper;

@Entity(name="ServiceRequest")
@Table(name = "service_request")
public class ServiceRequestImpl implements ServiceRequest, Serializable {

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
	@Column(name = "job_type", nullable = false, length=50)
	private String jobType;
	
	@CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(name="string_map", joinColumns = @JoinColumn(name="request_key"))
	@MapKey(columns = @Column(name = "key", length = 50))
	@Column(name = "value", nullable = true)
	private Map<String,String> stringMap = new HashMap<String, String>();
	@CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(name="integer_map", joinColumns = @JoinColumn(name="request_key"))
	@MapKey(columns = @Column(name = "key", length = 50))
	@Column(name = "value", nullable = true)
	private Map<String,Long> integerMap = new HashMap<String, Long>();
	@CollectionOfElements(fetch=FetchType.EAGER)
	@JoinTable(name="boolean_map", joinColumns = @JoinColumn(name="request_key"))
	@MapKey(columns = @Column(name = "key", length = 50))
	@Column(name = "value", nullable = true)
	private Map<String,Boolean> booleanMap = new HashMap<String, Boolean>();
	
	@Column(name = "request_date", nullable = false)
	private Date requestDate;
	@Column(name = "request_acknowledged_date", nullable = true)
	private Date requestAcknowledgedDate = null;
	@Column(name = "response_date", nullable = true)
	private Date responseDate = null;
	@Column(name = "response_acknowledged_date", nullable = true)
	private Date responseAcknowledgedDate = null;
	@Column(name = "is_success", nullable = true)
	private Boolean isSuccess = null;
	@Column(name = "error_message", nullable = true)
	private String errorMessage = null;
	@Lob
	@Column(name = "error_detail", nullable = true)
	private String errorDetail = null;

	public ServiceRequestImpl() {
	}
		
	public ServiceRequestImpl(String correlationKey, String queue, String jobType) {
		this.correlationKey = correlationKey;
		this.queue = queue;
		this.jobType = jobType;		
	}
		
	public Long getKey()
	{
		return this.key;
	}
	
	public void addString(String key, String value)
	{
		this.stringMap.put(key, value);
	}
	
	public Map<String,String> getStringMap()
	{
		return Collections.unmodifiableMap(this.stringMap);
	}

	public void addInteger(String key, Long value)
	{
		this.integerMap.put(key, value);
	}
	
	public Map<String,Long> getIntegerMap()
	{
		return Collections.unmodifiableMap(this.integerMap);
	}
				
	public void addBoolean(String key, Boolean value)
	{
		this.booleanMap.put(key, value);
	}
	
	public Map<String,Boolean> getBooleanMap()
	{
		return Collections.unmodifiableMap(this.booleanMap);
	}

	@Override
	public void request(String requester) {
		this.requester = requester;
		this.requestDate = Calendar.getInstance().getTime();
	}
	
	public void acknowledgeRequest(String responder)
	{
		if (this.responder != null)
		{
			throw new IllegalStateException("This request has already been acknowledged.");
		}
		this.responder = responder;
		this.requestAcknowledgedDate = Calendar.getInstance().getTime();
	}
	
	public void respondSuccess(boolean isSuccess)
	{
		if (this.isSuccess != null)
		{
			throw new IllegalStateException("This request has already been responded to.");
		}
		this.isSuccess = isSuccess;
		this.responseDate = Calendar.getInstance().getTime();
	}
	
	public void respondFailure(String errorMessage, String errorDetail)
	{
		if (this.isSuccess != null)
		{
			throw new IllegalStateException("This request has already been responded to.");
		}
		this.isSuccess = false;
		this.responseDate = Calendar.getInstance().getTime();
		this.errorMessage = errorMessage;
		this.errorDetail = errorDetail;		
	}
	
	public void respondFailure(Throwable exception)
	{
		this.respondFailure(exception.getMessage(), ExceptionHelper.stackTraceToString(exception));
	}

	public void acknowledgeResponse()
	{
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

	public String getJobType() {
		return jobType;
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

	public Boolean isSuccess() {
		return isSuccess;
	}

	public String getErrorMessage() {
		return errorMessage;
	}

	public String getErrorDetail() {
		return errorDetail;
	}
	
	@Override
	public Map<String, Object> getVariableMap() {
		Map<String,Object> variableMap = new HashMap<String, Object>();
		variableMap.putAll(this.stringMap);
		variableMap.putAll(this.booleanMap);
		variableMap.putAll(this.integerMap);
		return variableMap;
	}
	
	@Override
	public String toString() {
		return MessageFormat.format("Service Request with key {0}, requester {1}, jobType {2} and queue {3}.  String map contains {4}.  Integer map contains {5}.  Boolean map contains {6}", this.key, this.requester, this.jobType, this.queue, this.stringMap, this.integerMap, this.booleanMap);
	}
}
