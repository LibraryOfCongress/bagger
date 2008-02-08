package gov.loc.repository.service;

import java.util.HashMap;
import java.util.Map;

public class TaskResult {
	public boolean isSuccess;
	public String error;
	public String errorDetail;
	public Map<String,String> variableMap = new HashMap<String, String>();
}
