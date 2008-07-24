package gov.loc.repository.transfer.components.filemanagement.impl;

import static gov.loc.repository.transfer.components.ComponentConstants.TRANSPORT_USERNAME;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.filemanagement.Transporter;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.DirectoryCopier;

public class TwoStepRemoteDirectoryCopier implements DirectoryCopier {

	private static final Log log = LogFactory.getLog(TwoStepRemoteDirectoryCopier.class);
	
	private String keyFile;
    
	public TwoStepRemoteDirectoryCopier(String keyFile) {
		this.keyFile = keyFile;
	}
	
	@Override
	public void copy(CopyDescription copyDescription) {
        log.debug(MessageFormat.format("Performing copy from {0} to {1}", copyDescription.srcPath, copyDescription.destCopyToPath));

		String remoteHost = ((StorageSystemFileLocation)copyDescription.destFileLocation).getStorageSystem().getHost();
        Transporter transport = new Transporter(this.keyFile);
       
		transport.pullAndArchive(TRANSPORT_USERNAME, remoteHost, copyDescription);
	}
}
