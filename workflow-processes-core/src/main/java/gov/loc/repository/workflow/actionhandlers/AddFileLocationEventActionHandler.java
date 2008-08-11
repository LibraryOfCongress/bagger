package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import static gov.loc.repository.workflow.WorkflowConstants.TRANSITION_CONTINUE;

import java.util.Calendar;
import java.text.MessageFormat;

public class AddFileLocationEventActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddFileLocationEventActionHandler.class);
	private static String CLASS_PREFIX = "gov.loc.repository.packagemodeler.events.filelocation.";
	
	@SuppressWarnings("unchecked")
	private Class eventClass;
	private FileLocation fileLocation;
	
	@Required
	public String fileLocationKey;

	@Required
	public String eventClassName;
	
	public AddFileLocationEventActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception
	{
		if (! eventClassName.contains("."))
		{
			this.eventClassName = CLASS_PREFIX + eventClassName;
		}		
		this.eventClass = Class.forName(eventClassName);
		this.fileLocation = this.dao.loadRequiredFileLocation(Long.parseLong(this.fileLocationKey));
	}
		
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		FileLocationEvent event = this.factory.createFileLocationEvent(this.eventClass, this.fileLocation, Calendar.getInstance().getTime(), this.workflowAgent);

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
		
		log.debug(MessageFormat.format("Adding {0} to {1}.  Event success: {2}", this.eventClassName, this.fileLocation.toString(), event.isSuccess()));		
	}

}
