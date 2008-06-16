package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.agents.System;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

import java.text.MessageFormat;

public class AddStorageSystemFileLocationActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddStorageSystemFileLocationActionHandler.class);
	
	public boolean isLCPackageStructured = true;		
	
	public boolean isManaged = true;		

	@Required
	public String basePath;
	
	@Required
	public String storageSystemId;
		
	@Required
	public String packageKey;

	public String keyVariable;
	
	private System storageSystem;
	private Package packge;
	
	public AddStorageSystemFileLocationActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception {
		this.storageSystem = this.dao.findRequiredAgent(System.class, this.storageSystemId);
		this.packge = this.dao.loadRequiredPackage(Long.parseLong(this.packageKey));
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		FileLocation fileLocation = packge.getFileLocation(this.storageSystemId, this.basePath);
		if (fileLocation != null)
		{
			log.warn(MessageFormat.format("Storage System File Location with storage system id {0} and basepath {1} already found for {2}", this.storageSystemId, this.basePath, this.packge.toString()));
		}
		else
		{
			fileLocation = this.factory.createStorageSystemFileLocation(packge, this.storageSystem, this.basePath, this.isManaged, this.isLCPackageStructured);
			this.dao.save(fileLocation);
			log.debug(MessageFormat.format("Storage System File Location with storage system id {0} and basepath {1} added for {2}", this.storageSystemId, this.basePath, this.packge.toString()));
		}
		
		if (keyVariable != null)
		{
			this.setVariable(keyVariable, fileLocation.getKey().toString());
		}
	}

}
