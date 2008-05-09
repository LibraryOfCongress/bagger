package gov.loc.repository.workflow.continuations.impl;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.stereotype.Component;

import gov.loc.repository.workflow.continuations.SimpleContinuationController;

@Component("continuationController")
public class SimpleContinuationControllerImpl implements
		SimpleContinuationController {

	private static final Log log = LogFactory.getLog(SimpleContinuationControllerImpl.class);	
	
	protected static JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
	
	private String successTransition = "continue";
	
	@Override
	public void invoke(Long tokenInstanceId, Boolean success) throws Exception
	{		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(tokenInstanceId);
			if (token == null)
			{
				throw new Exception(MessageFormat.format("Token for token instance {0} not found", tokenInstanceId));
			}
								
			//Continue along the appropriate transition
			if (success)
			{
				log.debug(MessageFormat.format("Taking success transition {0} for token {1}", this.successTransition, token.getId()));
				token.signal(this.successTransition);
			}
			else
			{
				Exception ex = new Exception("Service returned failure");
				token.getNode().raiseException(ex, new ExecutionContext(token));
			}
			
		}
		finally
		{
			jbpmContext.close();
		}
	}

	@Override
	public void invoke(Long tokenInstanceId, String error, String errorDetail) throws Exception {
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

	public void setSuccessTransition(String transitionName) {
		this.successTransition = transitionName;
		
	}

}
