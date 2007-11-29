package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.agents.Person;
import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.util.Calendar;
import java.text.MessageFormat;

public class AddExternalFileLocationEventActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddExternalFileLocationEventActionHandler.class);
	private Class eventClass;
	private ExternalIdentifier identifier;
	
	@ContextVariable(name="externalIdentifierValue")
	public String externalIdentifierValue;

	@ContextVariable(name="externalIdentifierType")
	public String externalIdentifierType;
	
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
		this.identifier = new ExternalIdentifier(this.externalIdentifierValue, IdentifierType.valueOf(this.externalIdentifierType));
	}
		
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);				
		FileLocation externalFileLocation = packge.getFileLocation(identifier);
		if (externalFileLocation == null)
		{
			throw new Exception(MessageFormat.format("External File Location with identifier value {0} and identifier type {1} is not found for package {2} from repository {3}", this.externalIdentifierValue, this.externalIdentifierType, this.packageId, this.repositoryId));
		}
		FileLocationEvent event = this.getFactory().createFileLocationEvent(this.eventClass, externalFileLocation, Calendar.getInstance().getTime(), this.getWorkflowAgent());

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
		event.setPerformingAgent(this.getDAO().findRequiredAgent(Person.class, taskInstance.getActorId()));
		//Success
		if (! "continue".equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		
		log.debug(MessageFormat.format("Adding Verify Event to package {0}.  Event success: {1}", this.packageId, event.isSuccess()));		
	}

}
