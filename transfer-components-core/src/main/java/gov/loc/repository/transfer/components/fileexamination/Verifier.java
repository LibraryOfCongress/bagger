package gov.loc.repository.transfer.components.fileexamination;

import java.io.File;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;

public interface Verifier {
	boolean verify(FileLocation fileLocation, String mountPath) throws Exception;
	
	boolean verify(FileLocation fileLocation, String mountPath, Agent requestingAgent) throws Exception;
	
	boolean verify(File batchDir) throws Exception;
}
