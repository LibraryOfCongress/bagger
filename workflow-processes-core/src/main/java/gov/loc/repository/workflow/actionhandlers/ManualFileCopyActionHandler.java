package gov.loc.repository.workflow.actionhandlers;

import java.io.FileFilter;
import java.util.Calendar;

import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

public class ManualFileCopyActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	@ConfigurationField
	public String sourcePackageLocationVariable;
	@ConfigurationField
	public String sourceStorageServiceVariable;
	@ConfigurationField
	public String destinationPackageLocationVariable;
	@ConfigurationField
	public String destinationStorageServiceVariable;
	public String filterClassName = null;
	
	
	
	@ContextVariable(configurationFieldName="sourcePackageLocationVariable")
	public String sourceDirectoryName;
	@ContextVariable(configurationFieldName="sourceStorageServiceVariable")
	public String sourceStorageService;
	@ContextVariable(configurationFieldName="destinationPackageLocationVariable")
	public String destinationDirectoryName;
	@ContextVariable(configurationFieldName="destinationStorageServiceVariable")
	public String destinationStorageService;
		
	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;
		
	@Override
	protected void execute() throws Exception {
		/*
		FileCopyEvent event = this.getBatchModelDAO().createEvent(FileCopyEvent.class, this.repositoryId, this.packageId, Calendar.getInstance(), this.getWorkflowAgentId());
		TaskInstance taskInstance = this.executionContext.getTaskInstance();		
		//EventStart
		if (taskInstance.getStart() != null)
		{
			Calendar start = Calendar.getInstance();
			start.setTime(taskInstance.getStart());
			event.setEventStart(start);
		}
		//EventEnd
		if (taskInstance.getEnd() != null)
		{
			Calendar end = Calendar.getInstance();
			end.setTime(taskInstance.getEnd());
			event.setEventEnd(end);
		}
		//PerformingAgent
		event.setPerformingAgent(this.getBatchModelDAO().findAgent(Person.class, taskInstance.getActorId()));
		//Success
		if (! "continue".equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		
		FileFilter filter = null;
		if (this.filterClassName != null)
		{
			Object obj = Class.forName(this.filterClassName).newInstance();
			if (! FileFilter.class.isInstance(obj))
			{
				throw new Exception(this.filterClassName + " is not a FileFilter");
			}
			filter = (FileFilter)obj;
		}
		
		this.getBatchModelDAO().cloneFileInstances(this.repositoryId, this.packageId, this.sourceStorageService, this.sourceDirectoryName, this.destinationStorageService, this.destinationDirectoryName, event, filter);
		*/
	}	
	
}
