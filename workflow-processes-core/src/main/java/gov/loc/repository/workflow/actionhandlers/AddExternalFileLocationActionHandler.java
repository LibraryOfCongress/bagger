package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

import java.text.MessageFormat;

public class AddExternalFileLocationActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddExternalFileLocationActionHandler.class);

	@Required
	public String externalIdentifierValue;

	@Required
	public String externalIdentifierType;
		
	public String basePath = "/";
		
	@Required
	public String mediaType;	

	public boolean isLCPackageStructured = false;		
	
	public boolean isManaged = false;		
	
	@Required
	public String packageKey;
	
	public String keyVariable;
	
	private ExternalIdentifier identifier;
	private MediaType mediaTypeEnum;
	private Package packge;
	
	public AddExternalFileLocationActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception {
		this.identifier = new ExternalIdentifier(this.externalIdentifierValue, IdentifierType.valueOf(this.externalIdentifierType));
		this.mediaTypeEnum = MediaType.valueOf(this.mediaType);
		this.packge = this.getDAO().loadRequiredPackage(Long.parseLong(this.packageKey));
	}	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		FileLocation externalFileLocation = packge.getFileLocation(identifier);
		if (externalFileLocation != null)
		{
			throw new Exception(MessageFormat.format("External File Location with identifier value {0} and identifier type {1} already found for {2}", this.externalIdentifierValue, this.externalIdentifierType, this.packge.toString()));
		}
		externalFileLocation = this.getFactory().createExternalFileLocation(packge, this.mediaTypeEnum, this.identifier, this.basePath, this.isManaged, this.isLCPackageStructured);
		this.getDAO().save(externalFileLocation);
		log.debug(MessageFormat.format("Adding External File Location with identifier value {0} and identifier type {1} to {2}", this.externalIdentifierValue, this.externalIdentifierType, this.packge.toString()));
		if (keyVariable != null)
		{
			this.setVariable(this.keyVariable, externalFileLocation.getKey().toString());
		}
	}

}
