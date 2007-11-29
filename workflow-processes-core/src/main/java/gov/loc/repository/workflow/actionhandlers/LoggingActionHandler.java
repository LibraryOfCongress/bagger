package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;

@Transitions(transitions={"continue"})
public class LoggingActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L; 
	
	@ConfigurationField
	public String message;
	public boolean wait = false;
			
	@Override
	protected void execute() throws Exception {
		this.reportingLog.info(message);
		if (! wait)
		{
			this.executionContext.leaveNode("continue");
		}

	}

}
