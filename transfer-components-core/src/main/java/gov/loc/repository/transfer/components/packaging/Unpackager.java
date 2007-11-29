package gov.loc.repository.transfer.components.packaging;

import java.io.File;

public interface Unpackager {
	void unpackage(File sourceFile, File destinationDirectory) throws Exception;	
}
