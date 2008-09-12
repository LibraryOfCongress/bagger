package gov.loc.repository.transfer.components.filemanagement.impl;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.transfer.components.filemanagement.Transporter;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.CopyDescription;
import gov.loc.repository.transfer.components.filemanagement.impl.ConfigurableCopier.DirectoryCopier;

public class ArchivalRemoteDirectoryCopier implements DirectoryCopier {

	private static final Log log = LogFactory.getLog(ArchivalRemoteDirectoryCopier.class);
	
	private String keyFile;
	private String stagingBasePath;
	private String remoteUser;
    
	public ArchivalRemoteDirectoryCopier(String keyFile, String stagingBasePath, String remoteUser) {
		this.keyFile = keyFile;
		this.stagingBasePath = stagingBasePath;
		this.remoteUser = remoteUser;
	}
	
	@Override
	public void copy(CopyDescription copyDescription) {
        log.debug(MessageFormat.format("Performing copy from {0} to {1}", copyDescription.srcPath, copyDescription.destCopyToPath));

		String remoteHost = ((StorageSystemFileLocation)copyDescription.srcFileLocation).getStorageSystem().getHost();
        Transporter transport = new Transporter(this.keyFile, this.stagingBasePath);
       
		transport.pullAndArchive(remoteUser, remoteHost, copyDescription);
	}
}
