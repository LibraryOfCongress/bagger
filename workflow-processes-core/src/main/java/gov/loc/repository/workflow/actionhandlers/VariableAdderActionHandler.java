package gov.loc.repository.workflow.actionhandlers;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.workflow.actionhandlers.annotations.Required;

public class VariableAdderActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L; 
	private static final Log log = LogFactory.getLog(VariableAdderActionHandler.class);	

	@Required
	public String variableName;
	@Required
	public String variableValue;

	public VariableAdderActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
		
	@Override
	protected void execute() throws Exception {
		log.debug(MessageFormat.format("Setting {0} to {1}", this.variableName, this.variableValue));
		this.setVariable(this.variableName, this.variableValue);
	}
}
