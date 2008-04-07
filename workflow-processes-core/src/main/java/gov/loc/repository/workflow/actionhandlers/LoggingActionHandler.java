package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;
import static gov.loc.repository.workflow.WorkflowConstants.TRANSITION_CONTINUE;

@Transitions(transitions={TRANSITION_CONTINUE})
public class LoggingActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L; 
	
	@Required
	public String message;
	
	public boolean wait = false;

	public LoggingActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void execute() throws Exception {
		this.reportingLog.info(message);
		if (! wait)
		{
			this.leave(TRANSITION_CONTINUE);
		}

	}

}
