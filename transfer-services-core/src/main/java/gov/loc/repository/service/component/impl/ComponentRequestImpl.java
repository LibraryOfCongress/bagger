package gov.loc.repository.service.component.impl;

import java.io.Serializable;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import javax.persistence.Column;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.MappedSuperclass;

import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import gov.loc.repository.service.component.ComponentRequest;
import gov.loc.repository.service.component.impl.BooleanEntryImpl;
import gov.loc.repository.service.component.impl.IntegerEntryImpl;
import gov.loc.repository.service.component.impl.StringEntryImpl;
import gov.loc.repository.utilities.ExceptionHelper;

@MappedSuperclass
public class ComponentRequestImpl implements ComponentRequest, Serializable {

	private static final long serialVersionUID = 1L;

	@Column(name = "job_type", nullable = false, length=50)
	protected String jobType;
	
	@CollectionOfElements(targetElement=StringEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="request_string_entries", joinColumns = @JoinColumn(name="request_key"))
	protected Collection<StringEntry> requestStringEntries = new ArrayList<StringEntry>();

	@CollectionOfElements(targetElement=IntegerEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="request_integer_entries", joinColumns = @JoinColumn(name="request_key"))
	protected Collection<IntegerEntry> requestIntegerEntries = new ArrayList<IntegerEntry>();

	@CollectionOfElements(targetElement=BooleanEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="request_boolean_entries", joinColumns = @JoinColumn(name="request_key"))
	protected Collection<BooleanEntry> requestBooleanEntries = new ArrayList<BooleanEntry>();

	@CollectionOfElements(targetElement=StringEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="response_string_entries", joinColumns = @JoinColumn(name="request_key"))
	protected Collection<StringEntry> responseStringEntries = new ArrayList<StringEntry>();

	@CollectionOfElements(targetElement=IntegerEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="response_integer_entries", joinColumns = @JoinColumn(name="request_key"))
	protected Collection<IntegerEntry> responseIntegerEntries = new ArrayList<IntegerEntry>();

	@CollectionOfElements(targetElement=BooleanEntryImpl.class, fetch=FetchType.EAGER)
	@Fetch(FetchMode.SUBSELECT)
	@JoinTable(name="response_boolean_entries", joinColumns = @JoinColumn(name="request_key"))
	protected Collection<BooleanEntry> responseBooleanEntries = new ArrayList<BooleanEntry>();
		
	@Column(name = "is_success", nullable = true)
	protected Boolean isSuccess = null;
	@Column(name = "error_message", nullable = true)
	protected String errorMessage = null;
	@Lob
	@Column(name = "error_detail", nullable = true)
	protected String errorDetail = null;
	
	public ComponentRequestImpl() {
	}
		
	public ComponentRequestImpl(String jobType) {
		this.jobType = jobType;		
	}

	@Override
	public void addRequestString(String key, String value)
	{
		this.requestStringEntries.add(new StringEntryImpl(key, value));
	}

	@Override
	public Collection<StringEntry> getRequestStringEntries() {
		return Collections.unmodifiableCollection(this.requestStringEntries);
	}

	@Override
	public void addRequestInteger(String key, Long value)
	{
		this.requestIntegerEntries.add(new IntegerEntryImpl(key, value));
	}
	
	@Override
	public Collection<IntegerEntry> getRequestIntegerEntries() {
		return Collections.unmodifiableCollection(this.requestIntegerEntries);
	}
	
	@Override
	public void addRequestBoolean(String key, Boolean value)
	{
		this.requestBooleanEntries.add(new BooleanEntryImpl(key, value));
	}

	@Override
	public Collection<BooleanEntry> getRequestBooleanEntries() {
		return Collections.unmodifiableCollection(this.requestBooleanEntries);
	}

	@Override
	public void addResponseString(String key, String value)
	{
		this.responseStringEntries.add(new StringEntryImpl(key, value));
	}

	@Override
	public Collection<StringEntry> getResponseStringEntries() {
		return Collections.unmodifiableCollection(this.responseStringEntries);
	}

	@Override
	public void addResponseInteger(String key, Long value)
	{
		this.responseIntegerEntries.add(new IntegerEntryImpl(key, value));
	}
	
	@Override
	public Collection<IntegerEntry> getResponseIntegerEntries() {
		return Collections.unmodifiableCollection(this.responseIntegerEntries);
	}
				
	@Override
	public void addResponseBoolean(String key, Boolean value)
	{
		this.responseBooleanEntries.add(new BooleanEntryImpl(key, value));
	}

	@Override
	public Collection<BooleanEntry> getResponseBooleanEntries() {
		return Collections.unmodifiableCollection(this.responseBooleanEntries);
	}
	
	@Override
	public void respondSuccess(boolean isSuccess)
	{
		if (this.isSuccess != null)
		{
			throw new IllegalStateException("This request has already been responded to.");
		}
		this.isSuccess = isSuccess;		
	}
	
	@Override
	public void respondFailure(String errorMessage, String errorDetail)
	{
		if (this.isSuccess != null)
		{
			throw new IllegalStateException("This request has already been responded to.");
		}
		this.isSuccess = false;
		this.errorMessage = clean(errorMessage, 255);
		this.errorDetail = errorDetail;		
	}
	
	private String clean(String string, int length)
	{		
		if (string == null || string.length() <= length)
		{
			return string;
		}
		return string.substring(0, length-1);
	}
	
	@Override
	public void respondFailure(Throwable exception)
	{
		this.respondFailure(exception.getMessage(), ExceptionHelper.stackTraceToString(exception));
	}

	@Override
	public void setJobType(String jobType) {
		this.jobType = jobType;		
	}
	
	@Override
	public String getJobType() {
		return jobType;
	}

	@Override
	public Boolean isSuccess() {
		return isSuccess;
	}

	@Override
	public String getErrorMessage() {
		return errorMessage;
	}

	@Override
	public String getErrorDetail() {
		return errorDetail;
	}

	@Override
	public Collection<ObjectEntry> getRequestEntries() {
		Collection<ObjectEntry> entries = new ArrayList<ObjectEntry>();
		entries.addAll(this.requestStringEntries);
		entries.addAll(this.requestIntegerEntries);
		entries.addAll(this.requestBooleanEntries);
		return Collections.unmodifiableCollection(entries);
	}

	@Override
	public Collection<ObjectEntry> getResponseEntries() {
		Collection<ObjectEntry> entries = new ArrayList<ObjectEntry>();
		entries.addAll(this.responseStringEntries);
		entries.addAll(this.responseIntegerEntries);
		entries.addAll(this.responseBooleanEntries);
		return Collections.unmodifiableCollection(entries);
	}
	
	
	@Override
	public String toString() {
		return MessageFormat.format("Component Request with jobType {0}.  Request string entries contains {1}.  Request integer entries contains {2}.  Request boolean entries contains {3}.  Response string entries contains {4}.  Response integer entries contains {5}.  Response boolean entries contains {6}", this.jobType, collectionToString(this.requestStringEntries), collectionToString(this.requestIntegerEntries), collectionToString(this.requestBooleanEntries),collectionToString(this.responseStringEntries), collectionToString(this.responseIntegerEntries), collectionToString(this.responseBooleanEntries));
	}
	
	@SuppressWarnings("unchecked")
	protected String collectionToString(Collection entries)
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
				
}
