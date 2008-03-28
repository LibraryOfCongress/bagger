package gov.loc.repository.transfer.ui.model;

import org.jbpm.graph.def.Transition;

public class TransitionBean extends AbstractWorkflowBean {

	private Transition transition;
	
	void setTransition(Transition transition)
	{
		this.transition = transition;
	}
	
	public String getId() {
		return transition.getName();
	}

	public String getName() {
		return this.getMessage("transition." + this.transition.getProcessDefinition().getName() + "." + transition.getName(), transition.getName());
	}

}
