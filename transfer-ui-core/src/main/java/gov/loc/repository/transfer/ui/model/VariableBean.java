package gov.loc.repository.transfer.ui.model;

import org.jbpm.context.def.VariableAccess;

public class VariableBean extends AbstractWorkflowBean{
	private VariableAccess variable;
	private TaskBean taskBean;
	
	public String getId() {
		return this.variable.getVariableName();
	}
	
	public String getName() {
		return this.getMessage("variable." + this.taskBean.getProcessDefinitionBean().getId() + "." + this.getId(), this.getId());
	}

	public boolean isWritable() {
		return this.variable.isWritable();
	}
	
	public boolean isReadable() {
		return this.variable.isReadable();
	}

	public boolean isRequired() {
		return this.variable.isRequired();
	}

	void setVariable(VariableAccess variable) {
		this.variable = variable;
	}
	
	void setTaskBean(TaskBean taskBean)
	{
		this.taskBean = taskBean;
	}
}
