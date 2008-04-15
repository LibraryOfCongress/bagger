package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromFilesOnDiskEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import static gov.loc.repository.workflow.WorkflowConstants.TRANSITION_CONTINUE;

import java.util.Calendar;
import java.text.MessageFormat;

public class InventoryFilesOnDiskEventActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(IngestPackageEventActionHandler.class);

    @Required
    public String fileLocationKey;
    
	private FileLocation fileLocation;

	public InventoryFilesOnDiskEventActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void initialize() throws Exception {
		this.fileLocation = this.getDAO().loadRequiredFileLocation(Long.parseLong(this.fileLocationKey));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		InventoryFromFilesOnDiskEvent event = this.getFactory().createFileLocationEvent(InventoryFromFilesOnDiskEvent.class, this.fileLocation, Calendar.getInstance().getTime(), this.getWorkflowAgent());

		//PerformingAgent
		event.setPerformingAgent(this.getWorkflowAgent());
		//Success
		if (! TRANSITION_CONTINUE.equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		
		log.debug(MessageFormat.format("Adding Inventory Event to {0}.  Event success: {1}", this.fileLocation.toString(), event.isSuccess()));		
	}
}
