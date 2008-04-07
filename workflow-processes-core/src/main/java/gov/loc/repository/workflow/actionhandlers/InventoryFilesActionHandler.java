package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.transfer.components.fileexamination.FilesOnDiskInventorier;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

public class InventoryFilesActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	@Required
	public String fileLocationKey;
	
	@Required
	public String mountPath;
	
	@Required
	public String algorithm;
	
	public InventoryFilesActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void execute() throws Exception {
		FilesOnDiskInventorier inventorier = this.createObject(FilesOnDiskInventorier.class);
		inventorier.inventory(Long.parseLong(this.fileLocationKey), this.mountPath, this.algorithm, this.getWorkflowAgentId());
	}

}
