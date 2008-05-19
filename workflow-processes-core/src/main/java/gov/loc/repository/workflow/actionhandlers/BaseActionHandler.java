package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

import org.hibernate.Session;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.def.ActionHandler;
import org.springframework.context.ApplicationContext;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;
import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.ExceptionHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;
import gov.loc.repository.workflow.WorkflowConstants;
import gov.loc.repository.workflow.jbpm.instantiation.FieldInstantiator;
import gov.loc.repository.workflow.jbpm.spring.ContextService;
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
public abstract class BaseActionHandler implements ActionHandler
{
	private static final Log log = LogFactory.getLog(BaseActionHandler.class);
	
	//protected ExecutionContext executionContext; 
	protected HandlerHelper helper;
	protected Log reportingLog;
	protected Calendar start;
	protected PackageModelDAO dao;
	protected ModelerFactory factory;
	protected ExecutionContext executionContext;
	protected String actionHandlerConfiguration;
	protected ApplicationContext springContext = null;
	
	public BaseActionHandler(String actionHandlerConfiguration) {
		this.actionHandlerConfiguration = actionHandlerConfiguration;
	}

	public void setApplicationContext(ApplicationContext context)
	{
		//This is to allow testing with a null executionContext;
		this.springContext = context;
	}
	
	/**
	 * Calls initialize() and then execute().
	 * @throws Exception
 	 */	
	public final void execute(ExecutionContext executionContext) throws Exception
	{
		try
		{
			FieldInstantiator instantiator = new FieldInstantiator();
			instantiator.configure(this, this.actionHandlerConfiguration, executionContext);
			
			if (executionContext != null && executionContext.getJbpmContext() != null)
			{
				ContextService contextService = ((ContextService)executionContext.getJbpmContext().getServices().getService("springContext"));
				if (contextService != null)
				{
					springContext = contextService.getContext();
				}
			}
			else
			{
				log.warn("Not operating in jbpmContext, so can't get beans from Spring application context");
			}
			
			Session session = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).openSession();
			try
			{
				this.executionContext = executionContext;
				this.helper = new HandlerHelper(executionContext, this.getConfiguration(), this);
				this.reportingLog = LogFactory.getLog(this.getLoggerName());		
	
				session.beginTransaction();
				factory = this.createObject(ModelerFactory.class);
				dao = this.createObject(PackageModelDAO.class);
				dao.setSession(session);			
	
				if (this.executionContext != null)
				{
					this.helper.checkRequiredTransitions();
					this.helper.replacePlaceholdersInFields();
					this.helper.checkRequiredFields();
				}
						
				this.initialize();
				this.start = Calendar.getInstance();		
				this.execute();
				session.getTransaction().commit();
			}
			catch(Exception ex)
			{
				if (session != null && session.isOpen())
				{
					session.getTransaction().rollback();
				}
				throw ex;
			}
			finally
			{
				if (session != null && session.isOpen())
				{
					session.close();
				}
			}
		}
		catch(Exception ex)
		{
			Long processInstanceId = null;
			Long tokenId = null;
			String nodeName = null;
			String actionName = null;
			
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
					if (this.springContext != null && this.springContext.containsBean("requestServiceBroker"));
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
				this.executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_NODENAME, nodeName);
				this.executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME, actionName);
				this.executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION, ex.getMessage());
				this.executionContext.getContextInstance().createVariable(VARIABLE_LAST_EXCEPTION_DETAIL, ExceptionHelper.stackTraceToString(ex));
			}
						
			//Log the error
			log.error(MessageFormat.format("Process instance {0}, token {1} threw an exception.  Current node is {2}.  Current action is {3}.", processInstanceId, tokenId, nodeName, actionName), ex);
			
			throw new ActionHandlerException(processInstanceId, tokenId, nodeName, actionName, ex);
		}
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
	protected abstract void execute() throws Exception;

	/**
	 * Initialization of the ActionHandler.
	 * <p>This should include reading any variables from ExecutionContext and verifying that expected Transitions exist.
	 */
	protected void initialize() throws Exception
	{
	}

	public ModelerFactory getFactory()
	{
		return this.factory;
	}
	
	public PackageModelDAO getDAO()
	{
		return this.dao;
	}
	
	public PackageModelDAO createPackageModelDAO() throws Exception
	{
		return new PackageModelDAOImpl();
	}	

	public ModelerFactory createModelerFactory() throws Exception
	{
		return new ModelerFactoryImpl();
	}	
	
	
	@SuppressWarnings("unchecked")
	protected final <T> T createObject(Class<T> clazz) throws Exception
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
			return (T)Proxy.newProxyInstance(clazz.getClassLoader(), new Class[] {clazz}, new ServiceInvocationHandler(queueName, tokenId, (RequestingServiceBroker)springContext.getBean("requestServiceBroker")));
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
	
	protected Agent getWorkflowAgent() throws Exception
	{
		return this.getDAO().findRequiredAgent(Agent.class, this.getWorkflowAgentId());
	}

}
