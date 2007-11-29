package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;
import gov.loc.repository.transfer.components.remote.GenericHttpClient;
import gov.loc.repository.transfer.components.remote.impl.GenericHttpClientImpl;

import java.util.Map;
import java.util.List;
import java.util.HashMap;
import java.util.ArrayList;

@Transitions(transitions={"continue"})
public class SimpleHttpSyncActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L; 

	private Map<String,String> parameterMap = new HashMap<String,String>();
	
	/**
	 * A map of additional http parameters that can be provided by the ProcessDefinition.
	 */
	public Map<String,String> additionalParameterMap = new HashMap<String,String>();
	
	/**
	 * A list of variable names that will be added as http parameters.
	 */
	public List<String> variableList = new ArrayList<String>();
	
	@ConfigurationField
	public String baseUrl;
	
	@Override
	protected void initialize() throws Exception {
		//Check variables
		for(String name : variableList)
		{
			String value = (String)helper.getRequiredVariable(name);
			parameterMap.put(name, value);
		}
		
		//Add any additional parameters
		parameterMap.putAll(this.additionalParameterMap);
	}
	
	@Override
	protected void execute() throws Exception {
		GenericHttpClient client = this.createObject(GenericHttpClient.class);
		if (client.execute(baseUrl, parameterMap))
		{
			this.executionContext.leaveNode("continue");
		}
		else
		{
			throw new Exception("Http client returned false");
		}
	}

	public GenericHttpClient createGenericHttpClient() throws Exception
	{
		return new GenericHttpClientImpl();
	}
}
