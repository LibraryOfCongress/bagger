package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.text.MessageFormat;

public class AddStorageSystemFileLocationActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddStorageSystemFileLocationActionHandler.class);

	@ConfigurationField
	public String basePathVariable;
	
	@ContextVariable(configurationFieldName="basePathVariable")
	public String basePath;

	@ConfigurationField
	public String storageSystemIdVariable;
		
	@ContextVariable(configurationFieldName="storageSystemIdVariable", isRequired=false)
	public String storageSystemId = null;
		
	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;

	@ConfigurationField(isRequired=false)
	public boolean isLCPackageStructured = true;		
	
	@ConfigurationField(isRequired=false)
	public boolean isManaged = true;		
		
	private System storageSystem;
	
	@Override
	protected void initialize() throws Exception {
		this.storageSystem = this.getDAO().findRequiredAgent(System.class, this.storageSystemId);
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);				
		FileLocation fileLocation = packge.getFileLocation(this.storageSystemId, this.basePath);
		if (fileLocation != null)
		{
			throw new Exception(MessageFormat.format("Storage System File Location with storage system id {0} and basepath {1} is found for package {2} from repository {3}", this.storageSystemId, this.basePath, this.packageId, this.repositoryId));
		}
		this.getFactory().createStorageSystemFileLocation(packge, this.storageSystem, this.basePath, this.isManaged, this.isLCPackageStructured);
		this.getDAO().save(packge);
		log.debug(MessageFormat.format("Storage System File Location with storage system id {0} and basepath {1} added for package {2} from repository {3}", this.storageSystemId, this.basePath, this.packageId, this.repositoryId));		
	}

}
