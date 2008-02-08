package gov.loc.repository.service;

import java.util.Map;

public interface RequestMessage {
	public String getJobType() throws Exception;
	
	public Map<String,Object> getVariableMap() throws Exception;
	
}
