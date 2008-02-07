package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface Verifier {
	boolean verify(FileLocation fileLocation, String mountPath) throws Exception;
	
}
