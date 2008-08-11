package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;

public interface BagGenerator extends Component {
	
	public static final String COMPONENT_NAME = "baggenerator";
		
	/*
	 * Creates an manifest from the files on disk at the FileLocation, a bagit.txt file if missing, and optionally tag file checksums.
	 * The FileLocation must be bag structured.
	 */
	@JobType(name="generatebag")	
	public void generate(
			@RequestParam(name="filelocationkey") long fileLocationKey,
			@RequestParam(name="mountpath") String mountPath,
			@RequestParam(name="algorithm") String algorithm,
			@RequestParam(name="tagfilefixities") Boolean tagFileFixities,
			@RequestParam(name="requestingagentid") String requestingAgentId)
			throws Exception;
	
	public void generate(FileLocation fileLocation, String mountPath, FixityAlgorithm algorithm,/* Boolean tagFileFixities,*/ Agent requestingAgent) throws Exception;
	
}
