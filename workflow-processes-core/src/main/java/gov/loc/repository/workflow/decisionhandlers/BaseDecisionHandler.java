package gov.loc.repository.workflow.decisionhandlers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.apache.commons.configuration.ConfigurationException;

import org.hibernate.Session;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.node.DecisionHandler;

import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;
import gov.loc.repository.workflow.utilities.HandlerHelper;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.HashMap;
import java.text.MessageFormat;
import java.net.URL;
import java.util.Calendar;

public abstract class BaseDecisionHandler implements DecisionHandler
{
	private static final Log log = LogFactory.getLog(BaseDecisionHandler.class);
	protected static Configuration configuration;
	static
	{
		try
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			URL url = BaseDecisionHandler.class.getClassLoader().getResource("workflow.core.cfg.xml");
			if (url != null)
			{
				log.debug("Loading configuration: " + url.toString());
			}
			else
			{
				log.error("workflow.cfg.xml is missing.");
			}
			builder.setURL(url);
			configuration = builder.getConfiguration(true);
		}
		catch(ConfigurationException ex)
		{
			log.error("Error loading configuration", ex);
			throw new RuntimeException();
		}
	}
	
	//protected ExecutionContext executionContext; 
	protected HandlerHelper helper;
	protected Log reportingLog;
	protected Map<String, String> factoryMethodMap = new HashMap<String, String>();
	protected Calendar start;
	protected PackageModelDAO dao;
	protected ExecutionContext executionContext;
	
	/**
	 * Calls initialize() and then execute().
	 * @throws Exception
 	 */	
	public final String decide(ExecutionContext executionContext) throws Exception
	{
		String decision = null;
		Session session = HibernateUtil.getSessionFactory(DatabaseRole.DATA_WRITER).openSession();
		try
		{
			session.beginTransaction();
			dao = this.createObject(PackageModelDAO.class);
			dao.setSession(session);			

			this.executionContext = executionContext;
			this.helper = new HandlerHelper(executionContext, configuration, this);
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
			decision = this.decide();
			log.debug("Decide returned " + decision);
			this.helper.checkTransition(decision);
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
		return decision;
	}
	
	/**
	 * Make the decision.
	 * @throws Exception
	 */
	protected abstract String decide() throws Exception;

	/**
	 * Initialization of the ActionHandler.
	 * <p>This should include reading any variables from ExecutionContext and verifying that expected Transitions exist.
	 */
	protected void initialize() throws Exception
	{
	}
			
	public PackageModelDAO getDAO()
	{
		return this.dao;
	}
	
	public PackageModelDAO createPackageModelDAO() throws Exception
	{
		return new PackageModelDAOImpl();
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
	
}
