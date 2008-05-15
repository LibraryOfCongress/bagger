package gov.loc.repository.serviceBroker.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

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
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

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
	
	@CollectionOfElements(targetElement=StringEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="string_entries", joinColumns = @JoinColumn(name="request_key"))
	private Collection<StringEntry> stringEntries = new ArrayList<StringEntry>();

	@CollectionOfElements(targetElement=IntegerEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="integer_entries", joinColumns = @JoinColumn(name="request_key"))
	private Collection<IntegerEntry> integerEntries = new ArrayList<IntegerEntry>();

	@CollectionOfElements(targetElement=BooleanEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="boolean_entries", joinColumns = @JoinColumn(name="request_key"))
	private Collection<BooleanEntry> booleanEntries = new ArrayList<BooleanEntry>();
	
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

	@Column(name = "is_suspended", nullable = false)
	private Boolean isSuspended = false;
	
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
		this.stringEntries.add(new StringEntryImpl(key, value));
	}

	@Override
	public Collection<StringEntry> getStringEntries() {
		return Collections.unmodifiableCollection(this.stringEntries);
	}

	public void addInteger(String key, Long value)
	{
		this.integerEntries.add(new IntegerEntryImpl(key, value));
	}
	
	@Override
	public Collection<IntegerEntry> getIntegerEntries() {
		return Collections.unmodifiableCollection(this.integerEntries);
	}
				
	public void addBoolean(String key, Boolean value)
	{
		this.booleanEntries.add(new BooleanEntryImpl(key, value));
	}

	@Override
	public Collection<BooleanEntry> getBooleanEntries() {
		return Collections.unmodifiableCollection(this.booleanEntries);
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
	public Collection<ObjectEntry> getEntries() {
		Collection<ObjectEntry> entries = new ArrayList<ObjectEntry>();
		entries.addAll(this.stringEntries);
		entries.addAll(this.integerEntries);
		entries.addAll(this.booleanEntries);
		return Collections.unmodifiableCollection(entries);
	}
		
	@Override
	public String toString() {
		return MessageFormat.format("Service Request with key {0}, requester {1}, jobType {2} and queue {3}.  String entries contains {4}.  Integer entries contains {5}.  Boolean entries contains {6}", this.key, this.requester, this.jobType, this.queue, collectionToString(this.stringEntries), collectionToString(this.integerEntries), collectionToString(this.booleanEntries));
	}
	
	@SuppressWarnings("unchecked")
	private String collectionToString(Collection entries)
	{
		String s = "{";
		for(Object obj : entries)
		{
			ObjectEntry entry = (ObjectEntry)obj;
			if (s.length() != 1)
			{
				s+= ", ";
			}
			s+= entry.getKey() + "=" + entry.getValueObject();
		}		
		s+= "}";
		
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
