package gov.loc.repository.workflow.decisionhandlers;

import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import gov.loc.repository.workflow.actionhandlers.annotations.Transitions;
import static gov.loc.repository.workflow.WorkflowConstants.*;

@Transitions(transitions={TRANSITION_CONTINUE, TRANSITION_RETRY})
public class CheckStorageSystemFileLocationExistsDecisionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;

	@Required
	public String basePath;
	
	@Required
	public String storageSystemId;
		
	
	private System storageSystem;
	
	public CheckStorageSystemFileLocationExistsDecisionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception {
		this.storageSystem = this.dao.findRequiredAgent(System.class, this.storageSystemId);
	}	
		
	@Override
	protected String decide() throws Exception {
		if (this.dao.findStorageSystemFileLocation(this.storageSystem, basePath) != null)
		{
			this.executionContext.getContextInstance().setVariable("message", "The basepath already exists.");
			return TRANSITION_RETRY;
		}
		return TRANSITION_CONTINUE;
	}	
	
}
