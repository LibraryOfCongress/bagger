package gov.loc.repository.workflow;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;
import org.jbpm.graph.def.ActionHandler;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.ExceptionHelper;
import gov.loc.repository.workflow.actionhandlers.ActionHandlerException;
import gov.loc.repository.workflow.actionhandlers.ServiceInvocationHandler;
import gov.loc.repository.workflow.jbpm.instantiation.FieldInstantiator;
import gov.loc.repository.workflow.jbpm.spring.JbpmFactoryLocator;
import gov.loc.repository.workflow.utilities.HandlerHelper;
import static gov.loc.repository.workflow.WorkflowConstants.*;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.text.MessageFormat;
import java.util.Calendar;


/**
 * A base implementation of ActionHandler that simplifies unit testing by separating initialization from execution.
 * <p>For testing a ProcessDefinition, just the execute method can be overriden with testing behavior.
 *
 */
public abstract class AbstractHandler implements ActionHandler, DecisionHandler
{
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(AbstractHandler.class);
	
	private static final int MAX_VAR_LENGTH = 255;
	
	//protected ExecutionContext executionContext; 
	protected HandlerHelper helper;
	protected Log reportingLog;
	protected Calendar start;
	protected ExecutionContext executionContext;
	protected String actionHandlerConfiguration;
	protected BeanFactory springContext = null;

	protected PlatformTransactionManager txManager = null;
	private TransactionStatus status = null;
	
	public AbstractHandler(String actionHandlerConfiguration) {
		this.actionHandlerConfiguration = actionHandlerConfiguration;
	}
	
	@Override
	public final String decide(ExecutionContext executionContext) throws Exception {
		this.executionContext = executionContext;
		String decision = null;
		try
		{
			this.preinitialize();
			this.initialize();			
			decision = this.decide();
			this.postaction();
			
		}
		catch(Exception ex)
		{
			this.handleException(ex);
		}
		log.debug("Decision is " + decision);
		return decision;
	}
	
	/**
	 * Make the decision.
	 * @throws Exception
	 */
	protected String decide() throws Exception
	{
		throw new Exception("Decide must be overrided");
	}
	
	public void setBeanFactory(BeanFactory beanFactory)
	{
		this.springContext = beanFactory;
	}
	
	private void preinitialize() throws Exception
	{
		FieldInstantiator instantiator = new FieldInstantiator();
		instantiator.configure(this, this.actionHandlerConfiguration, executionContext);
		
		if (this.springContext == null)
		{
			this.springContext = this.retrieveBeanFactory();
		}
					
		this.helper = new HandlerHelper(executionContext, this.getConfiguration(), this);
		this.reportingLog = LogFactory.getLog(this.getLoggerName());		

		if (this.executionContext != null)
		{
			this.helper.checkRequiredTransitions();
			this.helper.replacePlaceholdersInFields();
			this.helper.checkRequiredFields();
		}
		
		this.commonInitialize();
		this.start = Calendar.getInstance();		
		
		if (this.txManager != null)
		{
			status = txManager.getTransaction(new DefaultTransactionDefinition());
		}
		
	}
	
	private void postaction() throws Exception
	{
		if (status != null)
		{
			txManager.commit(status);
		}
		
	}
	
	/**
	 * Calls initialize() and then execute().
	 * @throws Exception
 	 */	
	public final void execute(ExecutionContext executionContext) throws Exception
	{
		this.executionContext = executionContext;
		try
		{
			this.preinitialize();
			this.initialize();			
			this.execute();
			this.postaction();
		}
		catch(Exception ex)
		{
			this.handleException(ex);
		}
	}

	private void handleException(Exception ex) throws Exception
	{
		Long processInstanceId = null;
		Long tokenId = null;
		String nodeName = null;
		String actionName = null;
								
		if (status != null)
		{
			log.debug("Rolling back transaction");
			txManager.rollback(status);
		}
		
		if (executionContext != null)
		{
			if (executionContext.getProcessInstance() != null)
			{
				processInstanceId = executionContext.getProcessInstance().getId();
			}
			
			if (executionContext.getToken() != null)
			{
				tokenId = executionContext.getToken().getId();
				executionContext.getToken().suspend();
				if (this.springContext != null && this.springContext.containsBean("requestServiceBroker"))
				{
					RequestingServiceBroker broker = (RequestingServiceBroker)this.springContext.getBean("requestServiceBroker");
					broker.suspend(Long.toString(tokenId));
				}

			}
			
			if (executionContext.getNode() != null)
			{
				nodeName = executionContext.getNode().getName();
			}
			
			if (executionContext.getAction() != null)
			{
				actionName = executionContext.getAction().getName();
			}
			
			//Add to token's contextVariables
			if (executionContext.getContextInstance() != null)
			{
				executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_NODENAME, nodeName);
				executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME, actionName);
				executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, safeVariable(ex.getMessage()));
				executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_DETAIL, safeVariable(ExceptionHelper.stackTraceToString(ex)));
			}
		}
		//Log the error
		log.error(MessageFormat.format("Process instance {0}, token {1} threw an exception.  Current node is {2}.  Current action is {3}.", processInstanceId, tokenId, nodeName, actionName), ex);

		//Try taking a troubleshoot transition
		if (executionContext != null && executionContext.getNode() != null && executionContext.getNode().hasLeavingTransition(TRANSITION_TROUBLESHOOT))
		{
			executionContext.leaveNode(TRANSITION_TROUBLESHOOT);
		}
		//Try suspending
		else if (executionContext != null && executionContext.getToken() != null)
		{
			tokenId = executionContext.getToken().getId();
			executionContext.getToken().suspend();
			if (this.springContext != null && this.springContext.containsBean("requestServiceBroker"))
			{
				RequestingServiceBroker broker = (RequestingServiceBroker)this.springContext.getBean("requestServiceBroker");
				broker.suspend(Long.toString(tokenId));
			}
		}
		//Throw exception
		else
		{
				
			throw new ActionHandlerException(processInstanceId, tokenId, nodeName, actionName, ex);
		}
	}
	
	private String safeVariable(String value)
	{
		if (value == null || value.length() <= MAX_VAR_LENGTH)
		{
			return value;
		}
		return value.substring(0, MAX_VAR_LENGTH-1);
	}
	
	protected void leave(String transitionName) throws Exception
	{
		log.debug("Requested to leave node via " + transitionName);
		if (this.executionContext == null)
		{
			return;
		}
		
		//Make sure that transition is OK
		this.helper.checkTransition(transitionName);
		this.executionContext.leaveNode(transitionName);
	}
	
	public void commonInitialize() throws Exception
	{		
	}
		
	protected Configuration getConfiguration() throws Exception
	{
		return ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
	
	protected void setVariable(String name, Object value)
	{
		if (this.executionContext != null)
		{
			this.executionContext.getContextInstance().setVariable(name, value);
		}
	}
	
	/**
	 * Execute the action performed by the ActionHandler.
	 * @throws Exception
	 */
	protected void execute() throws Exception
	{
		throw new Exception("Execute must be overrided");

	}

	/**
	 * Initialization of the ActionHandler.
	 * <p>This should include reading any variables from ExecutionContext and verifying that expected Transitions exist.
	 */
	protected void initialize() throws Exception
	{
	}
		
	@SuppressWarnings("unchecked")
	public final <T> T createObject(Class<T> clazz) throws Exception
	{		
		String key = "none";
		Long tokenId = 0L;
		if (this.executionContext != null)
		{
			key = this.executionContext.getProcessDefinition().getName();
			if (this.executionContext.getAction() != null && this.executionContext.getAction().getName() != null)
			{
				key += "." + this.executionContext.getAction().getName().replaceAll(" ", "_");
			}
			tokenId = this.executionContext.getToken().getId();
		}
		key += "." + clazz.getSimpleName();		
		log.debug("Key base is " + key);
		String factoryMethodKey = key + ".factorymethod";
		String queueNameKey = key + ".queue";
		String simpleBeanNameKey = clazz.getSimpleName() + ".bean";
		String beanNameKey = key + ".bean";
				
		if (this.getConfiguration().containsKey(factoryMethodKey))
		{
			String factoryMethodName = this.getConfiguration().getString(factoryMethodKey);
			log.debug(MessageFormat.format("Configuration key {0} has value {1}", factoryMethodKey, factoryMethodName));
			int i = factoryMethodName.lastIndexOf(".");
			String className = factoryMethodName.substring(0, i);
			String methodName = factoryMethodName.substring(i+1);
			log.debug(MessageFormat.format("Creating object {0} with class {1} and method {2}", clazz.getSimpleName(), className, methodName));
			Class factoryClazz = Class.forName(className);
			Method method = factoryClazz.getMethod(methodName, (Class[])null);
			return (T)method.invoke((Object)null, (Object[])null);
		}
		else if (this.getConfiguration().containsKey(queueNameKey))
		{			
			String queueName = this.getConfiguration().getString(queueNameKey);
			log.debug(MessageFormat.format("Configuration key {0} has value {1}", queueNameKey, queueName));
			RequestingServiceBroker broker = null;
			if (springContext != null && springContext.containsBean("requestServiceBroker"))
			{
				broker = (RequestingServiceBroker)springContext.getBean("requestServiceBroker");
			}
			return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new ServiceInvocationHandler(queueName, tokenId, broker));
		}
		else if (this.getConfiguration().containsKey(beanNameKey))
		{
			String beanName = this.getConfiguration().getString(beanNameKey);
			log.debug(MessageFormat.format("Configuration key {0} has value {1}", beanNameKey, beanName));
			if (this.springContext == null || ! this.springContext.containsBean(beanName))
			{
				throw new Exception(MessageFormat.format("ApplicationContext is null or bean not found for bean name {0}", beanName));
			}
			return (T)this.springContext.getBean(beanName);
		}
		else if (this.getConfiguration().containsKey(simpleBeanNameKey))
		{
			String beanName = this.getConfiguration().getString(simpleBeanNameKey);
			log.debug(MessageFormat.format("Configuration key {0} has value {1}", simpleBeanNameKey, beanName));
			if (this.springContext == null || ! this.springContext.containsBean(beanName))
			{
				throw new Exception(MessageFormat.format("ApplicationContext is null or bean not found for bean name {0}", beanName));
			}
			return (T)this.springContext.getBean(beanName);
		}		
		else
		{
			String methodName = "create" + clazz.getSimpleName();
			log.debug(MessageFormat.format("Creating object {0} with method {1}", clazz.getSimpleName(), methodName));
			Method method = this.getClass().getMethod(methodName, (Class[])null);
			return (T)method.invoke(this, (Object[])null);
		}
	}
	
	private String getLoggerName()
	{
		String reportingLoggerName = "workflow";		
		if (this.executionContext == null)
		{
			reportingLoggerName += ".null_executioncontext";
		}
		else
		{
			if (executionContext.getProcessDefinition() != null && executionContext.getProcessDefinition().getName() != null)
			{
				reportingLoggerName += "." + executionContext.getProcessDefinition().getName();
			}
			else
			{
				reportingLoggerName += ".no_processdefinitionname";
			}
			if (executionContext.getNode() != null && executionContext.getNode().getName() != null)
			{
				reportingLoggerName += "." + executionContext.getNode().getName();
			}
			else
			{
				reportingLoggerName += ".no_nodename";
			}
			if (executionContext.getAction() != null && executionContext.getAction().getName() != null)
			{
				reportingLoggerName += "." + executionContext.getAction().getName();
			}
			else
			{
				reportingLoggerName += ".no_actionname";
			}
			
		}
		log.debug("Reporting logger name is " + reportingLoggerName);
		return reportingLoggerName;		
	}
	
	protected String getWorkflowAgentId()
	{
		return helper.getRequiredConfigString("agent.workflow.id");
	}
	
	protected BeanFactory retrieveBeanFactory() {
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
	
}
