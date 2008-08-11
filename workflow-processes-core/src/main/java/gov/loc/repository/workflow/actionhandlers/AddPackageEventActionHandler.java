package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import static gov.loc.repository.workflow.WorkflowConstants.*;

import java.util.Calendar;
import java.util.Collection;
import java.text.MessageFormat;

public class AddPackageEventActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddPackageEventActionHandler.class);
	private static String CLASS_PREFIX = "gov.loc.repository.packagemodeler.events.packge.";

	@SuppressWarnings("unchecked")
	private Class eventClass;
	
	private Package packge;
	
	public String eventClassName;
	
	public String packageKey;
	
	public String message;

	public AddPackageEventActionHandler(String actionHandlerConfiguration) {
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
		this.packge = this.dao.loadRequiredPackage(Long.parseLong(this.packageKey));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		PackageEvent event = this.factory.createPackageEvent(this.eventClass, packge, Calendar.getInstance().getTime(), this.workflowAgent);
		event.setRequestingAgent(this.workflowAgent);
		TaskInstance taskInstance = this.executionContext.getTaskInstance();
		if (taskInstance == null)
		{
			//Get the most recently completed taskInstance
			Collection<TaskInstance> taskInstanceCollection = this.executionContext.getTaskMgmtInstance().getTaskInstances();			
			for(TaskInstance checkTaskInstance : taskInstanceCollection)
			{
				if (checkTaskInstance.hasEnded() && (taskInstance == null || checkTaskInstance.getEnd().after(taskInstance.getEnd())))
				{
					taskInstance = checkTaskInstance;
				}
			}
			if (taskInstance == null)
			{
				throw new Exception("Cannot determine which task instance to report event " + eventClassName);
			}
			
		}

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
		//Success
		if (! TRANSITION_CONTINUE.equals((String)this.executionContext.getContextInstance().getTransientVariable(VARIABLE_TRANSITION)))
		{
			event.setSuccess(false);
		}
		if (this.message != null)
		{
			event.setMessage(this.message);
		}
		
		log.debug(MessageFormat.format("Adding event of type {0} to {1}.  Event success: {2}", eventClass.getName(), this.packge.toString(), event.isSuccess()));				
	}

}
