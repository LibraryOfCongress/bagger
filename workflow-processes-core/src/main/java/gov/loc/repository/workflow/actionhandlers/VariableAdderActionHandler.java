package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;

public class VariableAdderActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L; 
	
	@ConfigurationField
	public String variableName;
	@ConfigurationField
	public String variableValue;
	
		
	@Override
	protected void execute() throws Exception {
		this.executionContext.setVariable(this.variableName, this.variableValue);
	}
}
