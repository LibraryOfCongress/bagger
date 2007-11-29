package gov.loc.repository.workflow.jbpm.taskmgmt.exe;

import static org.junit.Assert.assertEquals;

import gov.loc.repository.workflow.actionhandlers.BaseActionHandler;

public class TaskActionTestActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	@Override
	protected void initialize() throws Exception {
		assertEquals("continue", this.executionContext.getContextInstance().getTransientVariable("transition"));
		assertEquals("bar", this.executionContext.getContextInstance().getVariable("foo"));
		assertEquals("bar", (String)helper.getRequiredVariable("foo"));
	}
	
	@Override
	protected void execute() throws Exception {
		assertEquals("continue", this.executionContext.getContextInstance().getTransientVariable("transition"));
		assertEquals("bar", this.executionContext.getContextInstance().getVariable("foo"));
	}

}
