package gov.loc.repository.serviceBroker;

import java.util.Collection;
import java.util.Date;

public interface ServiceRequest {

	public Long getKey();

	public void addString(String key, String value);

	public Collection<StringEntry> getStringEntries();
	
	public void addInteger(String key, Long value);

	public Collection<IntegerEntry> getIntegerEntries();
	
	public void addBoolean(String key, Boolean value);

	public Collection<BooleanEntry> getBooleanEntries();
	
	public Collection<ObjectEntry> getEntries();
		
	public void request(String requester);
	
	public void acknowledgeRequest(String responder);

	public void respondSuccess(boolean isSuccess);

	public void respondFailure(String errorMessage, String errorDetail);

	public void respondFailure(Throwable exception);

	public void acknowledgeResponse();

	public void resubmit();

	public String getRequester();

	public String getResponder();

	public String getCorrelationKey();

	public String getQueue();

	public String getJobType();

	public Date getRequestDate();

	public Date getRequestAcknowledgedDate();

	public Date getResponseDate();

	public Date getResponseAcknowledgedDate();

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
