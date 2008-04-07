package gov.loc.repository.workflow.actionhandlers;

import gov.loc.repository.transfer.components.fileexamination.LCManifestGenerator;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

public class GenerateLCManifestActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	@Required
	public String algorithm;
	
	@Required
	public String fileLocationKey;
	
	public String mountPath;
	
	public GenerateLCManifestActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void execute() throws Exception {
		LCManifestGenerator generator = this.createObject(LCManifestGenerator.class);
		generator.generate(Long.parseLong(this.fileLocationKey), this.mountPath, this.algorithm, this.getWorkflowAgentId());
	}

}
