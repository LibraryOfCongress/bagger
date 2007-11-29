package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.events.packge.PackageEvent;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.util.Calendar;
import java.util.Collection;
import java.text.MessageFormat;

public class AddPackageEventActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddPackageEventActionHandler.class);
	private Class eventClass;
	
	@ConfigurationField
	public String eventClassName;
	
	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;
	
	
	@Override
	protected void initialize() throws Exception
	{
		this.eventClass = Class.forName(eventClassName);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		

		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);
		PackageEvent event = this.getFactory().createPackageEvent(this.eventClass, packge, Calendar.getInstance().getTime(), this.getWorkflowAgent());
		event.setReportingAgent(this.getWorkflowAgent());
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
		event.setPerformingAgent(this.getDAO().findRequiredAgent(Person.class, taskInstance.getActorId()));
		//Success
		if (! "continue".equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}

		log.debug(MessageFormat.format("Adding event of type {0} to package {1}.  Event success: {2}", eventClass.getName(), this.packageId, event.isSuccess()));				
	}

}
