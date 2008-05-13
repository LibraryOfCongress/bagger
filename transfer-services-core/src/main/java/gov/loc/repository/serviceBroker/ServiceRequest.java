package gov.loc.repository.serviceBroker;

import java.util.Date;
import java.util.Map;

public interface ServiceRequest {

	public Long getKey();

	public void addString(String key, String value);

	public Map<String,String> getStringMap();

	public void addInteger(String key, Long value);

	public Map<String,Long> getIntegerMap();

	public void addBoolean(String key, Boolean value);

	public Map<String,Boolean> getBooleanMap();

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
	
	public Map<String,Object> getVariableMap();

}
