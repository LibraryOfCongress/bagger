package gov.loc.repository.workflow.decisionhandlers;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;
import static gov.loc.repository.workflow.WorkflowConstants.*;

@Transitions(transitions={TRANSITION_CONTINUE, TRANSITION_RETRY})
public class CheckPackageExistsDecisionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;

	@Required
	public String packageId;
	
	@Required
	public String repositoryId;
	
	public CheckPackageExistsDecisionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected String decide() throws Exception {
		Package packge = this.dao.findPackage(Package.class, this.repositoryId, this.packageId);
		if (packge == null || (packge.getProcessInstanceId() != null && packge.getProcessInstanceId() == this.executionContext.getProcessInstance().getId()))
		{
			return TRANSITION_CONTINUE;
		}
		String packageId = packge.getPackageId();
		String normalizedPackageId = packageId.replaceAll("_\\d{8}_", "_");
		if (packageId.equals(normalizedPackageId)) {
		}
		this.executionContext.getContextInstance().setVariable("message", "The package already exists.");
		return TRANSITION_RETRY;
	}	
	
}
