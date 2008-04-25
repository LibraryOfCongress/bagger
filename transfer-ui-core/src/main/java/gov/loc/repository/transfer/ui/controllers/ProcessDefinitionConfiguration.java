package gov.loc.repository.transfer.ui.controllers;

import java.util.HashMap;
import java.util.Map;

public class ProcessDefinitionConfiguration {
	private Map<String,String> taskInstanceUpdateCommandMap = new HashMap<String, String>();
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
}
