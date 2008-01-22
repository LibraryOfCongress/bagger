package gov.loc.repository.workflow.actionhandlers;

import java.text.MessageFormat;

import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.transfer.components.fileexamination.FilesOnDiskInventorier;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

public class InventoryFilesOnExternalFileLocation extends BaseActionHandler {

	private static final long serialVersionUID = 1L;

	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;	
	
	@ConfigurationField
	public String externalIdentifierValueVariable;

	@ConfigurationField
	public String externalIdentifierTypeVariable;
	
	@ConfigurationField
	public String mountPathVariable;

	@ContextVariable(configurationFieldName="externalIdentifierValueVariable")
	public String externalIdentifierValue;

	@ContextVariable(configurationFieldName="externalIdentifierTypeVariable")	
	public String externalIdentifierType;
	
	@ContextVariable(configurationFieldName="mountPathVariable")
	public String mountPath;

	private Long fileLocationKey;
	
	@Override
	protected void initialize() throws Exception {
		Package packge = this.dao.findRequiredPackage(Package.class, this.repositoryId, this.packageId);
		FileLocation externalFileLocation = packge.getFileLocation(new ExternalIdentifier(this.externalIdentifierValue, IdentifierType.valueOf(this.externalIdentifierType)));
		if (externalFileLocation == null)
		{
			throw new Exception(MessageFormat.format("External file location with identifier value {0} and identifier {1} for {2} is not found", this.externalIdentifierValue, this.externalIdentifierType, packge.toString()));
		}
		this.fileLocationKey = externalFileLocation.getKey();
	}	
	
	@Override
	protected void execute() throws Exception {
		FilesOnDiskInventorier inventorier = this.createObject(FilesOnDiskInventorier.class);
		inventorier.inventory(this.fileLocationKey, this.mountPath, this.getWorkflowAgentId());
	}

}
