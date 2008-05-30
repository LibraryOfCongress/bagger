package gov.loc.repository.transfer.components.fileexamination;

import java.util.Map;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.MapParameter;

public interface LCManifestGenerator extends Component {
	
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
	
	public void setCommandMap(Map<String,String> commandMap);
}
