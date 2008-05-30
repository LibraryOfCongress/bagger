package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.events.filelocation.IngestEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import gov.loc.repository.packagemodeler.agents.System;
import static gov.loc.repository.workflow.WorkflowConstants.TRANSITION_CONTINUE;

import java.util.Calendar;
import java.text.MessageFormat;

public class AddIngestPackageEventActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddIngestPackageEventActionHandler.class);

	@Required
	public String fileLocationKey;
	
	@Required
	public String repositorySystemId;
	
	private FileLocation fileLocation;
	
	private System repositorySystem;
	
	public AddIngestPackageEventActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
	
	@Override
	protected void initialize() throws Exception {
		this.fileLocation = this.dao.loadRequiredFileLocation(Long.parseLong(this.fileLocationKey));
		this.repositorySystem = this.dao.findRequiredAgent(System.class, this.repositorySystemId);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		IngestEvent event = this.factory.createFileLocationEvent(IngestEvent.class, this.fileLocation, Calendar.getInstance().getTime(), this.workflowAgent);

		TaskInstance taskInstance = this.executionContext.getTaskInstance();		

		//EventStart
		if (taskInstance.getStart() != null)
		{
			Calendar start = Calendar.getInstance();
			start.setTime(taskInstance.getStart());
			event.setEventStart(start.getTime());
		}
		//EventEnd
		if (taskInstance.getEnd() != null)
		{
			Calendar end = Calendar.getInstance();
			end.setTime(taskInstance.getEnd());
			event.setEventEnd(end.getTime());
		}
		//PerformingAgent
		event.setPerformingAgent(this.dao.findRequiredAgent(Person.class, taskInstance.getActorId()));
		
		//RequestingAgent
		event.setRequestingAgent(this.workflowAgent);
		
		//Success
		if (! TRANSITION_CONTINUE.equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		event.setRepositorySystem(this.repositorySystem);
		
		log.debug(MessageFormat.format("Adding Ingest Event to {0}.  Event success: {1}", this.fileLocation.toString(), event.isSuccess()));		
	}
}
