package gov.loc.repository.transfer.components.filemanagement.impl;

import java.io.File;

import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.transfer.components.filemanagement.filters.FileFilter;

public class DummyFileFilter implements FileFilter {

	public void setFileLocation(FileLocation fileLocation) throws Exception {

	}

	public boolean accept(File file) {
		if (file.getName().startsWith("b"))
		{
			return false;
		}
		return true;
	}

}
