package gov.loc.repository.workflow.actionhandlers;

//import static gov.loc.repository.workflow.constants.NdnpFixtureConstants.NDNP_NORMALIZED_PACKAGE_ID1;
//import static gov.loc.repository.workflow.constants.NdnpFixtureConstants.NDNP_REPOSITORY_ID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.agents.Person;
//import gov.loc.repository.packagemodeler.batch.Batch;
//import gov.loc.repository.packagemodeler.events.filelocation.FileLocationEvent;
import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.events.filelocation.IngestEvent;
import gov.loc.repository.packagemodeler.events.filelocation.InventoryFromFilesOnDiskEvent;
//import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
//import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.util.Calendar;
import java.util.Iterator;
import java.text.MessageFormat;

public class InventoryFilesOnDiskEventActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(InventoryFilesOnDiskEventActionHandler.class);
	private Class eventClass;

	@ConfigurationField
	public String eventClassName;

	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;

	@ContextVariable(name="externalIdentifierValue")
	public String externalIdentifierValue;
	
	@ContextVariable(name="externalIdentifierType")
	public String externalIdentifierType;

	@ContextVariable(name="externalFileLocationMountPath")
	public String externalFileLocationMountPath;

	
	@Override
	protected void initialize() throws Exception
	{
		this.eventClass = Class.forName(eventClassName);
	}
		
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		
		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);				
		FileLocation inventoryFileLocation = packge.getFileLocation(new ExternalIdentifier(externalIdentifierValue, externalIdentifierType));
		if (inventoryFileLocation == null)
		{
			throw new Exception(MessageFormat.format("Inventory File Location with identifier value {0} and identifier type {1} is not found for package {2} from repository {3}", this.externalIdentifierValue, this.externalIdentifierType, this.packageId, this.repositoryId));
		}

		InventoryFromFilesOnDiskEvent event = (InventoryFromFilesOnDiskEvent) this.getFactory().createFileLocationEvent(this.eventClass, inventoryFileLocation, Calendar.getInstance().getTime(), this.getWorkflowAgent());

		//Success
		if (! "continue".equals((String)this.executionContext.getContextInstance().getTransientVariable("transition")))
		{
			event.setSuccess(false);
		}
		
		log.debug(MessageFormat.format("Adding Inventory Event to package {0}.  Event success: {1}", this.packageId, event.isSuccess()));		
	}
}
