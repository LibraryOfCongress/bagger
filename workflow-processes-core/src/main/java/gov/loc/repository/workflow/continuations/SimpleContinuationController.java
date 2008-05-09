package gov.loc.repository.workflow.continuations;

/*
 * A controller for continuing a Process Instance that is in a wait state.
 * <p>An example of its use is by a servlet that accepts http messages that function as callbacks for an asynchronous action.
 */
public interface SimpleContinuationController {

	/**
	 * Invokes the continuation by instantiating the appropriate jBPM context based on the tokenInstanceId and setting variables.
	 * If success, then the process continues along the configured success transition.
	 * If failure, then the process continues along the configured failure transition or throws an exception (in the context of the process instance).
	 * @param tokenId
	 * @param success
	 * @throws Exception if process instance is not found or required variables are not provided.
	 */
	public void invoke(Long tokenId, Boolean success) throws Exception;

	/**
	 * Invokes the continuation by instantiating the appropriate jBPM context based on the tokenInstanceId and throwing an exception.
	 * @param tokenId
	 * @param error
	 * @param errorDetail
	 * @throws Exception if process instance is not found.
	 */
	public void invoke(Long tokenId, String error, String errorDetail) throws Exception;
			
}
