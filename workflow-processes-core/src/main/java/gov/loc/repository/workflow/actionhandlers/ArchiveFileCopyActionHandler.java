package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.transfer.components.filemanagement.ArchivalRemoteBagCopier;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

public class ArchiveFileCopyActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	
	@Required
	public String srcFileLocationKey;
	
	@Required
	public String destFileLocationKey;
	
	@Required
	public String algorithm;
	
	@Required
	public String user;
		
	@Required
	public String group;
		
	public String srcMountPath = null;
	
	public String destMountPath = null;

	public ArchiveFileCopyActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
		
	@Override
	protected void execute() throws Exception {
		ArchivalRemoteBagCopier copier = this.createObject(ArchivalRemoteBagCopier.class);
		copier.copy(Long.parseLong(this.srcFileLocationKey), this.srcMountPath, Long.parseLong(this.destFileLocationKey), this.destMountPath, this.getWorkflowAgentId(), this.algorithm, this.user, this.group);
	}

}