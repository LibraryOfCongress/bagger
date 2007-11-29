package gov.loc.repository.transfer.components.filemanagement.filters;

import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface FileFilter extends java.io.FileFilter {

	public void setFileLocation(FileLocation fileLocation) throws Exception;
}
