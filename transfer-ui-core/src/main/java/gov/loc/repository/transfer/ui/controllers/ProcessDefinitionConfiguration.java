package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.commands.TaskInstanceUpdateCommand;

import java.util.HashMap;
import java.util.Map;

public class ProcessDefinitionConfiguration {
	private Map<String,TaskInstanceUpdateCommand> taskInstanceUpdateCommandMap = new HashMap<String, TaskInstanceUpdateCommand>();
	private String resourceBundleBaseName;
	
	
	public void setTaskInstanceUpdateCommandMap(Map<String,TaskInstanceUpdateCommand> map) {
		this.taskInstanceUpdateCommandMap = map;
	}
	
	public Map<String,TaskInstanceUpdateCommand> getTaskInstanceUpdateCommandMap()
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
