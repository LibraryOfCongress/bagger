package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;

import java.text.MessageFormat;
import java.util.Collection;

public class AddCanonicalFilesActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(AddStorageSystemFileLocationActionHandler.class);
	
    @Required
    public String packageKey;

	@Required
	public String fileLocationKey;
		
	private Package packge;
	private FileLocation fileLocation;
    
	public AddCanonicalFilesActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
	}
	
	@Override
	protected void initialize() throws Exception {
        this.packge = this.dao.loadRequiredPackage(Long.parseLong(this.packageKey));
        if (this.packge == null)
        {
            throw new Exception(MessageFormat.format("Package {0} not found", this.packge.getPackageId()));
        }

        this.fileLocation = this.dao.loadRequiredFileLocation(Long.parseLong(this.fileLocationKey));
        if (this.fileLocation == null)
        {
            throw new Exception(MessageFormat.format("File Location {0} not found for package {1}", this.fileLocation.toString(), this.packge.getPackageId()));
        }
    }	
	
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		//Delete any canonical files that exist
		if (! this.packge.getCanonicalFiles().isEmpty())
		{
			log.warn(MessageFormat.format("{0} already has canonical files.  Deleting before adding new ones.", this.packge));
			this.dao.deleteCanonicalFiles(packge);
			
		}
		
	    this.factory.createCanonicalFilesFromFileInstances(this.packge, (Collection<FileInstance>)this.fileLocation.getFileInstances()); 
	    this.dao.save(this.packge);
		log.debug(MessageFormat.format("Canonical files from {0} added for package {1}", this.packge.getPackageId(), this.fileLocation.toString()));
	}
}
