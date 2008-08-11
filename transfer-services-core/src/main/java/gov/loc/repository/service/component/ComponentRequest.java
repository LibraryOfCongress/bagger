package gov.loc.repository.service.component;

import java.util.Collection;

public interface ComponentRequest {

	public void setJobType(String jobType);
	
	public String getJobType();
	
	public void addRequestString(String key, String value);

	public Collection<StringEntry> getRequestStringEntries();
	
	public void addRequestInteger(String key, Long value);

	public Collection<IntegerEntry> getRequestIntegerEntries();
	
	public void addRequestBoolean(String key, Boolean value);

	public Collection<BooleanEntry> getRequestBooleanEntries();
	
	public Collection<ObjectEntry> getRequestEntries();

	public void addResponseString(String key, String value);

	public Collection<StringEntry> getResponseStringEntries();
	
	public void addResponseInteger(String key, Long value);

	public Collection<IntegerEntry> getResponseIntegerEntries();
	
	public void addResponseBoolean(String key, Boolean value);

	public Collection<BooleanEntry> getResponseBooleanEntries();
	
	public Collection<ObjectEntry> getResponseEntries();
		
	public void respondSuccess(boolean isSuccess);

	public void respondFailure(String errorMessage, String errorDetail);

	public void respondFailure(Throwable exception);

	public Boolean isSuccess();

	public String getErrorMessage();

	public String getErrorDetail();
		
	public interface StringEntry extends ObjectEntry
	{		
		public String getValue();
	}
	
	public interface BooleanEntry extends ObjectEntry
	{
		public Boolean getValue();
	}
	
	public interface IntegerEntry extends ObjectEntry
	{	
		public Long getValue();
	}

	public interface ObjectEntry
	{
		public String getKey();
		
		public Object getValueObject();
		
	}
	
}
