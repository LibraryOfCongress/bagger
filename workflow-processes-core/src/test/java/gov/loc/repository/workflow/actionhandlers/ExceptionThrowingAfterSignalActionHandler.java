package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.workflow.AbstractHandler;

public class ExceptionThrowingAfterSignalActionHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	public ExceptionThrowingAfterSignalActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void execute() throws Exception {
		this.executionContext.leaveNode("continue2");		
		throw new Exception("Oops.  An exception occurred.");
	}

}
