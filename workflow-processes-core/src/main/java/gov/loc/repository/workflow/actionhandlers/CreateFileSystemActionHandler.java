package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.transfer.components.filemanagement.ZFSFileSystemCreator;
import gov.loc.repository.workflow.AbstractPackageModelerAwareHandler;
import gov.loc.repository.workflow.actionhandlers.annotations.Required;
import java.text.MessageFormat;

public class CreateFileSystemActionHandler extends AbstractPackageModelerAwareHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(CreateFileSystemActionHandler.class);

    @Required
    public String mountPath;
    
    @Required
    public Long size;
    
	public CreateFileSystemActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);
	}
		
	@Override
	protected void execute() throws Exception {
		
		ZFSFileSystemCreator creator = this.createObject(ZFSFileSystemCreator.class);
		log.debug(MessageFormat.format("Creating filesystem {0}", mountPath));
		creator.create(mountPath, size);
		
	}
}
