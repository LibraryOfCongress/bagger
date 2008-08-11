package gov.loc.repository.transfer.components.fileexamination;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.RequestParam;
import gov.loc.repository.service.annotations.Result;

public interface BagVerifier extends Component {
	
	public static final String COMPONENT_NAME = "bagverifier";
		
	/*
	 * Checks the bag is valid and complete.
	 * A VerifyAgainstManifestEvent is recorded.
	 * The FileLocation must be bag structured.
	 * @return true if the verification is successful. 
	 */
	@JobType(name="verifybag")	
	public void verify(
			@RequestParam(name="filelocationkey") long fileLocationKey,
			@RequestParam(name="mountpath") String mountPath,
			@RequestParam(name="tagmanifests") Boolean verifyTagManifests,
			@RequestParam(name="requestingagentid") String requestingAgentId)
			throws Exception;
	
	public void verify(FileLocation fileLocation, String mountPath, Agent requestingAgent) throws Exception;
	
	@Result(jobType="verifybag")
	public boolean verifyResult();
	
}
