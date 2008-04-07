package gov.loc.repository.workflow.actionhandlers;

import org.jbpm.graph.def.ActionHandler;

public class MockingActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	public MockingActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void execute() throws Exception {
		this.createObject(ActionHandler.class).execute(this.executionContext);
		executionContext.leaveNode("continue");
	}

	public ActionHandler createActionHandler() throws Exception
	{
		throw new Exception("ActionHandler mock not provided");
	}
}
