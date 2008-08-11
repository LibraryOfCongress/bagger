package gov.loc.repository.workflow.jbpm.taskmgmt.exe;

import static org.junit.Assert.assertEquals;

import gov.loc.repository.workflow.AbstractHandler;

public class TaskActionTestActionHandler extends AbstractHandler {

	private static final long serialVersionUID = 1L;

	public TaskActionTestActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void initialize() throws Exception {
		assertEquals("continue", this.executionContext.getContextInstance().getTransientVariable("transition"));
		assertEquals("bar", this.executionContext.getContextInstance().getVariable("foo"));
		assertEquals("bar", (String)helper.getContextVariable("foo"));
	}
	
	@Override
	protected void execute() throws Exception {
		assertEquals("continue", this.executionContext.getContextInstance().getTransientVariable("transition"));
		assertEquals("bar", this.executionContext.getContextInstance().getVariable("foo"));
	}

}
