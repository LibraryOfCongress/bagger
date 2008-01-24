package gov.loc.repository.console.workflow.beans;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.def.Task;

public class TaskBean extends AbstractWorkflowBean {
	private Task task;
	
	public void setTask(Task task)
	{
		this.task = task;
	}
	
	public String getName()
	{
		return this.task.getName();
	}
	
	public List<String> getLeavingTransitionList()
	{
		List<String> transitionList = new ArrayList<String>();
		Iterator iter = this.task.getTaskNode().getLeavingTransitions().iterator();
		while (iter.hasNext())
		{
			Transition transition = (Transition)iter.next();
			transitionList.add(transition.getName());
		}
		return transitionList;
	}

	public List<VariableBean> getVariableBeanList()
	{
		List<VariableBean> variableBeanList = new ArrayList<VariableBean>();
		if (this.task.getTaskController() != null)
		{
			Iterator iter = this.task.getTaskController().getVariableAccesses().iterator();
			while(iter.hasNext())
			{
				VariableBean variableBean = new VariableBean();
				variableBean.setVariable((VariableAccess)iter.next());
				variableBean.setJbpmContext(jbpmContext);
				variableBeanList.add(variableBean);			
			}
		}
		return variableBeanList;
	}
}
