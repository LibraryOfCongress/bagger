package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;
import gov.loc.repository.workflow.actionhandlers.annotations.ContextVariable;

import java.text.MessageFormat;

public class AddExternalFileLocationActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddExternalFileLocationActionHandler.class);
	
	@ContextVariable(name="externalIdentifierValue")
	public String externalIdentifierValue;

	@ContextVariable(name="externalIdentifierType")
	public String externalIdentifierType;
		
	@ContextVariable(name="externalFileLocationBasePath", isRequired=false)
	public String basePath = "/";
		
	@ConfigurationField
	public String mediaType;	

	@ConfigurationField(isRequired=false)
	public boolean isLCPackageStructured = false;		
	
	@ConfigurationField(isRequired=false)
	public boolean isManaged = false;		
		
	@ContextVariable(name="packageId")
	public String packageId;
	
	@ContextVariable(name="repositoryId")
	public String repositoryId;

	private ExternalIdentifier identifier;
	private MediaType mediaTypeEnum;
	
	@Override
	protected void initialize() throws Exception {
		this.identifier = new ExternalIdentifier(this.externalIdentifierValue, IdentifierType.valueOf(this.externalIdentifierType));
		this.mediaTypeEnum = MediaType.valueOf(this.mediaType);
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		Package packge = this.getDAO().findRequiredPackage(Package.class, this.repositoryId, this.packageId);				
		FileLocation externalFileLocation = packge.getFileLocation(identifier);
		if (externalFileLocation != null)
		{
			throw new Exception(MessageFormat.format("External File Location with identifier value {0} and identifier type {1} is found for package {2} from repository {3}", this.externalIdentifierValue, this.externalIdentifierType, this.packageId, this.repositoryId));
		}
		FileLocation fileLocation = this.getFactory().createExternalFileLocation(packge, this.mediaTypeEnum, this.identifier, this.basePath, this.isManaged, this.isLCPackageStructured);
		fileLocation.setManaged(false);
		this.getDAO().save(packge);
		log.debug(MessageFormat.format("Adding External File Location with identifier value {0} and identifier type {1} to package {2} from repository {3}", this.externalIdentifierValue, this.externalIdentifierType, this.packageId, this.repositoryId));		
	}

}
