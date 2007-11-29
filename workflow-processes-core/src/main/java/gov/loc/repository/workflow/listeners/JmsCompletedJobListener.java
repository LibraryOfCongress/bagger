package gov.loc.repository.workflow.listeners;

import java.util.Map;

public interface JmsCompletedJobListener {

	public abstract void start() throws Exception;

	public boolean isStarted();
	
	public abstract void stop();

	public abstract void setRequiredParameters(
			Map<String, Map<String,String>> requiredParameters);

	public abstract void setOptionalParameters(
			Map<String, Map<String,String>> optionalParameters);

	public abstract void setDefaultSuccessTransition(
			String defaultSuccessTransition);

	public abstract void setDefaultFailureTransition(
			String defaultFailureTransition);

	public abstract void setSuccessTransitions(
			Map<String, String> successTransitions);

	public abstract void setFailureTransitions(
			Map<String, String> failureTransitions);

}