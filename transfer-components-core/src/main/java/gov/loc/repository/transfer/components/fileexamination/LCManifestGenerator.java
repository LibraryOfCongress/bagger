package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.ModelerAware;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;

public interface LCManifestGenerator extends Component, ModelerAware {
	
	public static final String COMPONENT_NAME = "lcmanifestverifier";
		
	/*
	 * Creates an LC Manifest from the files on disk at the FileLocation.
	 * The FileLocation must be LC package structured.
	 */
	@JobType(name="generatelcmanifest")	
	public void generate(
			@MapParameter(name="filelocationkey") long fileLocationKey,
			@MapParameter(name="mountpath") String mountPath,
			@MapParameter(name="algorithm") String algorithm,
			@MapParameter(name="requestingagentid") String requestingAgentId)
			throws Exception;
	
	public void generate(FileLocation fileLocation, String mountPath, Fixity.Algorithm algorithm, Agent requestingAgent) throws Exception;
	
}
