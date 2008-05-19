package gov.loc.repository.workflow.continuations.impl;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import static gov.loc.repository.workflow.WorkflowConstants.*;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.jbpm.spring.ContextService;

@Component("continuationController")
public class SimpleContinuationControllerImpl implements
		SimpleContinuationController {

	private static final Log log = LogFactory.getLog(SimpleContinuationControllerImpl.class);	
	
	protected static JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
	
	private String successTransition = TRANSITION_CONTINUE;
	
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
				//Add variables describing error
				ExecutionContext executionContext = new ExecutionContext(token);
				executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, "Service request returned failure");
				//Suspend token
				token.suspend();
				//Suspend service requests
				this.suspendServiceRequests(jbpmContext, token);
			}
			
		}
		finally
		{
			jbpmContext.close();
		}
	}
	
	private void suspendServiceRequests(JbpmContext jbpmContext, Token token)
	{
		ApplicationContext springContext = ((ContextService)jbpmContext.getServices().getService("springContext")).getContext();
		if (springContext.containsBean("requestServiceBroker"))
		{
			RequestingServiceBroker broker = (RequestingServiceBroker)springContext.getBean("requestServiceBroker");
			log.debug("Suspending service requests for " + token.getId());
			broker.suspend(Long.toString(token.getId()));
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
			//Add variables describing error
			ExecutionContext executionContext = new ExecutionContext(token);
			executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, error);
			executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_DETAIL, errorDetail);
			//Suspend token
			token.suspend();
			//Suspend service requests
			this.suspendServiceRequests(jbpmContext, token);
			
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
