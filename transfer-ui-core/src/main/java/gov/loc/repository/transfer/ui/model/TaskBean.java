package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.def.Task;

public class TaskBean extends AbstractWorkflowBean {
	private Task task;
	
	void setTask(Task task)
	{
		this.task = task;
	}
	
	public String getId() {
		return this.task.getName();
	}
	
	public String getName()
	{
		return this.getMessage("task." + this.getProcessDefinitionBean().getId() + "." + this.getId(), this.getId());
	}
	
	public ProcessDefinitionBean getProcessDefinitionBean()
	{
		return this.factory.createProcessDefinitionBean(this.task.getProcessDefinition());
	}
	
	@SuppressWarnings("unchecked")
	public List<TransitionBean> getLeavingTransitionBeanList()
	{
		List<TransitionBean> transitionBeanList = new ArrayList<TransitionBean>();
		Iterator<Transition> iter = this.task.getTaskNode().getLeavingTransitions().iterator();
		while (iter.hasNext())
		{
			TransitionBean transitionBean = this.factory.createTransitionBean(iter.next());
			transitionBeanList.add(transitionBean);
		}
		return transitionBeanList;
	}

	@SuppressWarnings("unchecked")
	public boolean hasLeavingTransition(String transition)
	{
		Iterator<Transition> iter = this.task.getTaskNode().getLeavingTransitions().iterator();
		while (iter.hasNext())
		{
			if (iter.next().getName().equals(transition))
			{
				return true;
			}
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<VariableBean> getVariableBeanList()
	{
		List<VariableBean> variableBeanList = new ArrayList<VariableBean>();
		if (this.task.getTaskController() != null)
		{
			Iterator<VariableAccess> iter = this.task.getTaskController().getVariableAccesses().iterator();
			while(iter.hasNext())
			{
				VariableBean variableBean = this.factory.createVariableBean(iter.next(), this);
				variableBeanList.add(variableBean);			
			}
		}
		return variableBeanList;
	}
}
