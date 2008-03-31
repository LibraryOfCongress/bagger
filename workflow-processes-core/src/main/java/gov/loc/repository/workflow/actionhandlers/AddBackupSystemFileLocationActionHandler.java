package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.text.MessageFormat;

public class AddBackupSystemFileLocationActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddBackupSystemFileLocationActionHandler.class);

	@ConfigurationField
	public String basePathVariable;
	
	@ConfigurationField
	public String storageSystemIdVariable;

	@ContextVariable(configurationFieldName="basePathVariable")
	public String backupPackageLocation;
	
	@ContextVariable(configurationFieldName="backupSystemIdVariable")
	public String backupStorageService;

	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;

	@ConfigurationField(isRequired=false)
	public boolean isLCPackageStructured = false;		
	
	@ConfigurationField(isRequired=false)
	public boolean isManaged = false;
	
	private System storageSystem;
	
	@Override
	protected void initialize() throws Exception {
		this.storageSystem = this.getDAO().findRequiredAgent(System.class, this.backupStorageService);
		log.debug(MessageFormat.format("[FOO] Backup system set to {0}", this.storageSystem.toString()));
		if (this.storageSystem == null)
		{
			throw new Exception(MessageFormat.format("[FOO] Could not find backupStorageService {0} via findRequiredAgent()", this.backupStorageService));
		}
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);
		FileLocation fileLocation = packge.getFileLocation(this.backupStorageService, this.backupPackageLocation);
		if (fileLocation != null)
		{
			throw new Exception(MessageFormat.format("[FOO] Backup System File Location with storage system id {0} and basepath {1} is found for package {2} from repository {3}", this.backupStorageService, this.backupPackageLocation, this.packageId, this.repositoryId));
		}
		this.getFactory().createStorageSystemFileLocation(packge, this.storageSystem, this.backupPackageLocation, this.isManaged, this.isLCPackageStructured);
		this.getDAO().save(packge);
		log.debug(MessageFormat.format("[FOO] Backup System File Location with storage system id {0} and basepath {1} added for package {2} from repository {3}", this.backupStorageService, this.backupPackageLocation, this.packageId, this.repositoryId));		
	}

}
