package gov.loc.repository.workflow.continuations;

import java.util.Map;

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
	 * @param parameterMap
	 * @throws Exception if process instance is not found or required variables are not provided.
	 */
	public void invoke(long tokenId, boolean success, Map<String,String> parameterMap) throws Exception;

	/**
	 * Invokes the continuation by instantiating the appropriate jBPM context based on the tokenInstanceId and throwing an exception.
	 * @param tokenId
	 * @param error
	 * @param errorDetail
	 * @throws Exception if process instance is not found.
	 */
	public void invoke(long tokenId, String error, String errorDetail) throws Exception;
	
	
	/**
	 * Set the set of required parameters and the mapping of those parameters to context variables.
	 * <p>Required parameters must be present in the parameterMap and will be written to the ExecutionContext with the mapped variable name.
	 * @param requiredParameterMap A list of the variable names.
	 */
	public void setRequiredParameters(Map<String,String> requiredParameterMap);

	/**
	 * Set the set of optional parameters and the mapping of those parameters to context variables.
	 * <p>If present in the parameterMap, optional parameters will be written to the ExecutionContext with the mapped variable name.
	 * @param optionalParameterMap A list of the variable names.
	 */
	public void setOptionalParameters(Map<String,String> optionalParameterMap);

	public void setSuccessTransition(String transitionName);
	
	/*
	 * Set the name of the transition to follow if failure is reported.  Set to null to have an exception thrown when failure is reported.
	 */
	public void setFailureTransition(String transitionName); 
	
}
