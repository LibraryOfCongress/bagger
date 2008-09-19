package gov.loc.repository.workflow.continuations.impl;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import static gov.loc.repository.workflow.WorkflowConstants.*;
import gov.loc.repository.workflow.continuations.ResponseParameterMapper;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.jbpm.spring.JbpmFactoryLocator;

@Component("continuationController")
public class SimpleContinuationControllerImpl implements
		SimpleContinuationController {
	
	private static final Log log = LogFactory.getLog(SimpleContinuationControllerImpl.class);	
	
	protected JbpmConfiguration jbpmConfiguration;
	protected Map<String, ResponseParameterMapper> responseParameterMapperMap = new HashMap<String, ResponseParameterMapper>();
	
	private String continueTransition = TRANSITION_CONTINUE;
	protected String troubleshootTransition = TRANSITION_TROUBLESHOOT;
		
	@Autowired
	public SimpleContinuationControllerImpl(JbpmConfiguration jbpmConfiguration) {
		this.jbpmConfiguration = jbpmConfiguration;
	}
	
	@Autowired(required=false)
	public void setResponseParameterMapperMap(Map<String, ResponseParameterMapper> responseParameterMapperMap)
	{
		this.responseParameterMapperMap.putAll(responseParameterMapperMap);
	}
	
	public void addResponseParameterMapper(String processDefinitionName, ResponseParameterMapper mapper)
	{
		this.responseParameterMapperMap.put(processDefinitionName, mapper);
	}
	
	
	@Override
	public void invoke(Long tokenInstanceId, Map<String,Object> responseParameterMap, Boolean success) throws Exception
	{		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(tokenInstanceId);
			if (token == null)
			{
				throw new Exception(MessageFormat.format("Token for token instance {0} not found", tokenInstanceId));
			}
			
			ExecutionContext executionContext = new ExecutionContext(token);			
			this.addContextVariables(executionContext, responseParameterMap);
			//Continue along the appropriate transition
			if (success && this.hasTransition(token, continueTransition))
			{
				log.debug(MessageFormat.format("Taking {0} transition for token {1}", continueTransition, token.getId()));
				token.signal(continueTransition);
			}
			else if (! success && this.hasTransition(token, troubleshootTransition))
			{
				log.debug(MessageFormat.format("Taking {0} transition for token {1}", troubleshootTransition, token.getId()));
				//Add variables describing error			
				executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, "Service request returned failure");
				token.signal(troubleshootTransition);
				
			}
			else
			{
				if (success && ! this.hasTransition(token, continueTransition))
				{
					executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, "Service request returned success, but no continue transition found");
				}
				//Suspend token
				token.suspend();
				//Suspend service requests
				this.suspendServiceRequests(jbpmContext, token);
			}
			jbpmContext.save(token);
		}
		finally
		{
			jbpmContext.close();
		}
	}
	
	
	private void addContextVariables(ExecutionContext executionContext, Map<String,Object> responseParameterMap)
	{
		ResponseParameterMapper mapper = this.responseParameterMapperMap.get(executionContext.getProcessDefinition().getName());
		if (mapper != null)
		{
			for(String key : responseParameterMap.keySet())
			{
				String contextVariableName = mapper.map(key);
				if (contextVariableName != null)
				{
					executionContext.setVariable(contextVariableName, responseParameterMap.get(key));
				}
			}
		}
	}
	
	private void suspendServiceRequests(JbpmContext jbpmContext, Token token)
	{
		BeanFactory beanFactory = this.retrieveBeanFactory();
		if (beanFactory != null && beanFactory.containsBean("requestServiceBroker"))
		{
			RequestingServiceBroker broker = (RequestingServiceBroker)beanFactory.getBean("requestServiceBroker");
			log.debug("Suspending service requests for token " + token.getId());
			broker.suspend(Long.toString(token.getId()));
		}
	}
	
	private BeanFactory retrieveBeanFactory() {
		final String factoryKey = "jbpmConfiguration";
		BeanFactoryLocator factoryLocator = new JbpmFactoryLocator();
		BeanFactoryReference factory = factoryLocator.useBeanFactory(factoryKey);
		if (factory == null)
		{
			log.warn("Spring application context not available");
			return null;
			//throw new IllegalArgumentException("no beanFactory found under key=" + factoryKey);
		}

		try {
			return factory.getFactory();
		}
		finally {
			factory.release();
		}
	}

	@Override
	public void invoke(Long tokenInstanceId, Map<String,Object> contextVariableMap, String error, String errorDetail) throws Exception {
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
			
			this.addContextVariables(executionContext, contextVariableMap);
						
			executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, clean(error));
			executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_DETAIL, clean(errorDetail));
			
			if (this.hasTransition(token, troubleshootTransition))
			{
				log.debug(MessageFormat.format("Taking {0} transition for token {1}", troubleshootTransition, token.getId()));
				//Add variables describing error			
				token.signal(troubleshootTransition);			
			}
			else
			{
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

	private String clean(String str)
	{
		if (str.length() > 255)
		{
			return str.substring(0, 254);
		}
		return str;
	}
	
	@SuppressWarnings("unchecked")
	public boolean hasTransition(Token token, String transitionName)
	{
		
		Set<Transition> transitions = token.getAvailableTransitions();
		for(Transition transition : transitions)
		{
			if (transitionName.equals(transition.getName()))
			{
				log.debug(MessageFormat.format("Token {0} has transition {1}", token.getId(), transitionName));
				return true;
			}
		}
		log.debug(MessageFormat.format("Token {0} does not have transition {1}", token.getId(), transitionName));
		return false;
	}

	public void setContinueTransition(String continueTransition) {
		this.continueTransition = continueTransition;
	}

	public void setTroubleshootTransition(String troubleshootTransition) {
		this.troubleshootTransition = troubleshootTransition;
	}
	
}
