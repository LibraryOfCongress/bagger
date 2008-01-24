package gov.loc.repository.console.workflow.beans;

import org.jbpm.context.def.VariableAccess;

public class VariableBean extends AbstractWorkflowBean{
	private VariableAccess variable;
	
	public String getName() {
		return this.variable.getVariableName();
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

	public void setVariable(VariableAccess variable) {
		this.variable = variable;
	}
		
}
