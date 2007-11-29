package gov.loc.repository.workflow.actionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;

import org.hibernate.Session;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.def.ActionHandler;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.workflow.utilities.HandlerHelper;
import gov.loc.repository.workflow.utilities.ConfigurationHelper;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
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
	protected Map<String, String> factoryMethodMap = new HashMap<String, String>();
	protected Calendar start;
	protected PackageModelDAO dao;
	protected ModelerFactory factory;
	protected ExecutionContext executionContext;
	
	/**
	 * Calls initialize() and then execute().
	 * @throws Exception
 	 */	
	public final void execute(ExecutionContext executionContext) throws Exception
	{
		Session session = HibernateUtil.getSessionFactory().openSession();
		try
		{
			session.beginTransaction();
			factory = this.createObject(ModelerFactory.class);
			dao = this.createObject(PackageModelDAO.class);
			dao.setSession(session);			

			this.executionContext = executionContext;
			this.helper = new HandlerHelper(executionContext, this.getConfiguration(), this);
			this.reportingLog = LogFactory.getLog(this.getLoggerName());		
			if (this.executionContext != null)
			{
				this.helper.checkConfigurationFields();
				this.helper.initializeContextVariables();
				this.helper.initializeIndirectContextVariables();
				this.helper.checkRequiredTransitions();
			}
			this.helper.replacePropertiesInFields();
					
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

	protected Configuration getConfiguration()
	{
		return ConfigurationHelper.getConfiguration();
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
		/*
		if (log.isDebugEnabled())
		{
			if (this.factoryMethodMap.isEmpty())
			{
				log.debug("factoryMethodMap is empty");
			}
			for(String key : this.factoryMethodMap.keySet())
			{
				log.debug(MessageFormat.format("factoryMethodMap has {0},{1}", key, this.factoryMethodMap.get(key)));
			}
		}
		*/
		if (factoryMethodMap.containsKey(clazz.getSimpleName()))
		{
			String fullMethodName = factoryMethodMap.get(clazz.getSimpleName());
			int i = fullMethodName.lastIndexOf(".");
			String className = fullMethodName.substring(0, i);
			String methodName = fullMethodName.substring(i+1);
			log.debug(MessageFormat.format("Creating object {0} with class {1} and method {2}", clazz.getSimpleName(), className, methodName));
			Class factoryClazz = Class.forName(className);
			Method method = factoryClazz.getMethod(methodName, (Class[])null);
			return (T)method.invoke((Object)null, (Object[])null);
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
