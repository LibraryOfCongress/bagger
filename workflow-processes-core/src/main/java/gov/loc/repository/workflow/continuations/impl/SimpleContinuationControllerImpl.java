package gov.loc.repository.workflow.continuations.impl;

import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.exe.ProcessInstance;

import gov.loc.repository.workflow.continuations.SimpleContinuationController;

public class SimpleContinuationControllerImpl implements
		SimpleContinuationController {

	private static final Log log = LogFactory.getLog(SimpleContinuationControllerImpl.class);	
	
	private Map<String,String> optionalParameterMap = new HashMap<String,String>();
	private Map<String,String> requiredParameterMap = new HashMap<String,String>();
	protected static JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
	
	private String successTransition = "continue";
	private String failureTransition = null;
	
	public void invoke(long tokenInstanceId, boolean success, Map<String, String> parameterMap) throws Exception
	{
		if (parameterMap == null)
		{
			parameterMap = new HashMap<String,String>();
		}
		
		//Make sure all requiredVariables are provided
		for(String parameterName : requiredParameterMap.keySet())
		{
			if (! parameterMap.containsKey(parameterName))
			{
				throw new Exception(MessageFormat.format("Required parameter {0} not found in supplied parameterMap", parameterName));
			}
		}
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(tokenInstanceId);
			if (token == null)
			{
				throw new Exception(MessageFormat.format("Token for token instance {0} not found", tokenInstanceId));
			}
			ProcessInstance processInstance = token.getProcessInstance();
			
			//Set the variables
			for(String parameterName : requiredParameterMap.keySet())
			{
				processInstance.getContextInstance().setVariable(requiredParameterMap.get(parameterName), parameterMap.get(parameterName));
			}
			for(String parameterName : optionalParameterMap.keySet())
			{
				if (parameterMap.containsKey(parameterName))
				{
					processInstance.getContextInstance().setVariable(optionalParameterMap.get(parameterName), parameterMap.get(parameterName));
				}
			}
						
			//Continue along the appropriate transition
			if (success)
			{
				log.debug(MessageFormat.format("Taking success transition {0} for token {1}", this.successTransition, token.getId()));
				token.signal(this.successTransition);
			}
			else if (this.failureTransition != null)
			{
				log.debug(MessageFormat.format("Taking failure transition {0} for token {1}", this.failureTransition, token.getId()));
				token.signal(this.failureTransition);
			}
			else
			{
				Exception ex = new Exception("Service returned false");
				token.getNode().raiseException(ex, new ExecutionContext(token));
			}
			
		}
		finally
		{
			jbpmContext.close();
		}
	}

	public void setOptionalParameters(
			Map<String, String> optionalParameterMap) {
		this.optionalParameterMap = optionalParameterMap;

	}

	public void setRequiredParameters(Map<String,String> requiredParameterMap) {
		this.requiredParameterMap = requiredParameterMap;

	}

	public void invoke(long tokenInstanceId, String error, String errorDetail) throws Exception {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{

			Token token = jbpmContext.getToken(tokenInstanceId);
			if (token == null)
			{
				throw new Exception(MessageFormat.format("Token for token instance {0} not found", tokenInstanceId));
			}
			Exception ex = new Exception(error);
			if (errorDetail != null)
			{
				log.error(MessageFormat.format("Service returned an error: {0}.  Error detail: {1}", error, errorDetail));
			}
			token.getNode().raiseException(ex, new ExecutionContext(token));
			
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

	public void setFailureTransition(String transitionName) {
		this.failureTransition = transitionName;
		
	}

	public void setSuccessTransition(String transitionName) {
		this.successTransition = transitionName;
		
	}

}
