package gov.loc.repository.workflow.decisionhandlers;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;

@Transitions(transitions={"continue", "retry"})
public class CheckPackageExistsDecisionHandler extends BaseDecisionHandler {

	private static final long serialVersionUID = 1L;

	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;
			
	@Override
	protected String decide() throws Exception {
		Package packge = this.getDAO().findPackage(Package.class, this.repositoryId, this.packageId);

		if (packge == null || (packge.getProcessInstanceId() != null && packge.getProcessInstanceId() == this.executionContext.getProcessInstance().getId()))
		{
			return "continue";
		}
		this.executionContext.getContextInstance().setVariable("message", "The package already exists.");
		return "retry";
	}	
	
}
