package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.events.filelocation.FileCopyEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

import java.util.Calendar;
import java.text.MessageFormat;

public class FileCopyEventActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(FileCopyEventActionHandler.class);

	@Required
	public String srcFileLocationKey;
	
	@Required
	public String destFileLocationKey;
	
	private FileLocation srcFileLocation;
	
	private FileLocation destFileLocation;
	
	public FileCopyEventActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception
	{
		this.srcFileLocation = this.getDAO().loadRequiredFileLocation(Long.parseLong(this.srcFileLocationKey));
		this.destFileLocation = this.getDAO().loadRequiredFileLocation(Long.parseLong(this.destFileLocationKey));
	}
		
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		FileCopyEvent event = this.getFactory().createFileLocationEvent(FileCopyEvent.class, destFileLocation, Calendar.getInstance().getTime(), this.getWorkflowAgent());
		event.setFileLocationSource(srcFileLocation);
		//Success
		if (! "continue".equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		
		log.debug(MessageFormat.format("Adding File Copy Event to {0}.  Event success: {1}", this.destFileLocation.toString(), event.isSuccess()));		
	}

}
