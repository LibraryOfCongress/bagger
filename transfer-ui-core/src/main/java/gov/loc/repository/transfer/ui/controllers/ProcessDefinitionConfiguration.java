package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.workflow.continuations.ResponseParameterMapper;

import java.util.HashMap;
import java.util.Map;

public class ProcessDefinitionConfiguration implements ResponseParameterMapper {
	private Map<String,String> taskInstanceUpdateCommandMap = new HashMap<String, String>();
	private Map<String,String> responseParameterMap = new HashMap<String,String>();
	private String resourceBundleBaseName;
	
	
	public void setTaskInstanceUpdateCommandMap(Map<String,String> map) {
		this.taskInstanceUpdateCommandMap = map;
	}
	
	public Map<String,String> getTaskInstanceUpdateCommandMap()
	{
		return this.taskInstanceUpdateCommandMap;
	}

	public void setResourceBundleBaseName(String resourceBundleBaseName) {
		this.resourceBundleBaseName = resourceBundleBaseName;
	}

	public String getResourceBundleBaseName() {
		return resourceBundleBaseName;
	}

	public void setResponseParameterMap(Map<String,String> responseEntryMap) {
		this.responseParameterMap = responseEntryMap;
	}

	public Map<String,String> getResponseParameterMap() {
		return responseParameterMap;
	}
	
	@Override
	public String map(String responseParameterName) {
		return responseParameterMap.get(responseParameterName);
	}
}
