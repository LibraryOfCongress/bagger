package gov.loc.repository.workflow.jbpm.taskmgmt.impl;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.TaskInstanceFactory;
import gov.loc.repository.workflow.jbpm.taskmgmt.exe.ModifiedTaskInstance;

public class TaskInstanceFactoryImpl implements TaskInstanceFactory {
	
	private static final long serialVersionUID = 1L;

	public org.jbpm.taskmgmt.exe.TaskInstance createTaskInstance(ExecutionContext arg0) {
		return new ModifiedTaskInstance();
	}

}
