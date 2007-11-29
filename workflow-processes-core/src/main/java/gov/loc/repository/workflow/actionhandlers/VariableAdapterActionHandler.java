package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.text.MessageFormat;

public class VariableAdapterActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L; 
	private static final Log log = LogFactory.getLog(VariableAdapterActionHandler.class);
	
	@ConfigurationField
	public String fromVariableName;
	@ConfigurationField
	public String toVariableName;
	
	@ContextVariable(configurationFieldName="fromVariableName")
	public String fromValue;
	
		
	@Override
	protected void execute() throws Exception {
		log.debug(MessageFormat.format("Copying {0} to {1} with value {3}", this.fromVariableName, this.toVariableName, this.fromValue));
		this.executionContext.setVariable(this.toVariableName, this.fromValue);
	}
}
