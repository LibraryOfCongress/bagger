package gov.loc.repository.workflow.jbpm.spring;

import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.access.BeanFactoryReference;
import org.springframework.core.io.Resource;
import org.springmodules.workflow.jbpm31.JbpmUtils;
import org.springmodules.workflow.jbpm31.definition.ProcessDefinitionFactoryBean;

/**
 * FactoryBean which allows customized creation of JbpmConfiguration objects
 * which are binded to the lifecycle of the bean factory container. A
 * BeanFactory aware ObjectFactory can be used by the resulting object for
 * retrieving beans from the application context, delegating to the default
 * implementation for unresolved names. It is possible to use an already defined
 * Hibernate SessionFactory by injecting an approapriate HibernateTemplate - if
 * defined, the underlying session factory will be used by jBPM Persistence
 * Service.
 * 
 * If set to true, createSchema and dropSchema will be executed on factory
 * initialization and destruction, using the contextName property which, by
 * default, is equivalent with JbpmContext.DEFAULT_JBPM_CONTEXT_NAME.
 * 
 * 
 * @see org.jbpm.configuration.ObjectFactory
 * @author Costin Leau
 * 
 */
public class LocalJbpmConfigurationFactoryBean implements InitializingBean, DisposableBean, FactoryBean, BeanFactoryAware, BeanNameAware
{

	private static final Log logger = LogFactory.getLog(LocalJbpmConfigurationFactoryBean.class);

	private JbpmConfiguration jbpmConfiguration;

	private boolean createSchema = false;

	private boolean dropSchema = false;

	private boolean hasPersistenceService;

	private String contextName = JbpmContext.DEFAULT_JBPM_CONTEXT_NAME;

	private Resource[] processDefinitionsResources;

	private ProcessDefinition[] processDefinitions;

	private SessionFactory sessionFactory;

	/**
	 * FactoryLocator
	 */
	private JbpmFactoryLocator factoryLocator = new JbpmFactoryLocator();

	private BeanFactoryReference reference;

	private String factoryKey = JbpmFactoryLocator.class.getName();

	/**
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
		factoryLocator.setBeanFactory(beanFactory);
		reference = factoryLocator.useBeanFactory(factoryKey);
	}

	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setBeanName(java.lang.String)
	 */
	public void setBeanName(String name) {
		factoryLocator.setBeanName(name);
		this.factoryKey = name;
	}

	
	/**
	 * @see org.springframework.beans.factory.DisposableBean#destroy()
	 */
	public void destroy() throws Exception {
		// trigger locator cleanup
		if (reference != null)
		{
			reference.release();
		}

		if (dropSchema && hasPersistenceService) {
			logger.info("dropping schema");
			jbpmConfiguration.dropSchema(contextName);
		}

	}

	/**
	 * @see org.springframework.beans.factory.InitializingBean#afterPropertiesSet()
	 */
	public void afterPropertiesSet() throws Exception {
		jbpmConfiguration = JbpmConfiguration.getInstance();
		JbpmContext context = null;
		try {
			// 2. inject the HB session factory if it is the case
			context = jbpmConfiguration.createJbpmContext(contextName);

			if (sessionFactory != null) {
				logger.info("using given Hibernate session factory");
				context.setSessionFactory(sessionFactory);
			}

			// 3. execute persistence operations
			hasPersistenceService = JbpmUtils.hasPersistenceService(jbpmConfiguration, contextName);

			if (hasPersistenceService) {
				logger.info("persistence service available...");
				if (createSchema) {
					logger.info("creating schema");
					jbpmConfiguration.createSchema(contextName);
				}

				if (processDefinitions != null || processDefinitionsResources != null) {
					if (processDefinitions != null) {
						String toString = Arrays.asList(processDefinitions).toString();
						logger.info("deploying process definitions:" + toString);

						// deploy the ProcessDefinitions
						for (int i = 0; i < processDefinitions.length; i++) {
							context.deployProcessDefinition(processDefinitions[i]);
						}
					}
					if (processDefinitionsResources != null) {
						ProcessDefinitionFactoryBean factory = new ProcessDefinitionFactoryBean();
						String toString = Arrays.asList(processDefinitionsResources).toString();
						logger.info("deploying process definitions (from resources):" + toString);

						for (int i = 0; i < processDefinitionsResources.length; i++) {
							factory.setDefinitionLocation(processDefinitionsResources[i]);
							factory.afterPropertiesSet();
							context.deployProcessDefinition((ProcessDefinition) factory.getObject());
						}
					}
				}
			}

			else {
				logger
						.info("persistence unavailable not available - schema create/drop and process definition deployment disabled");
			}

		}
		finally {
			if (context != null)
				context.close();
		}

	}
	
	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObject()
	 */
	public Object getObject() throws Exception {
		return jbpmConfiguration;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#getObjectType()
	 */
	public Class<?> getObjectType() {
		return JbpmConfiguration.class;
	}

	/**
	 * @see org.springframework.beans.factory.FactoryBean#isSingleton()
	 */
	public boolean isSingleton() {
		return true;
	}

	/**
	 * @return Returns the contextName.
	 */
	public String getContextName() {
		return contextName;
	}

	/**
	 * @param contextName The contextName to set.
	 */
	public void setContextName(String contextName) {
		this.contextName = contextName;
	}

	/**
	 * @return Returns the createSchema.
	 */
	public boolean isCreateSchema() {
		return createSchema;
	}

	/**
	 * @param createSchema The createSchema to set.
	 */
	public void setCreateSchema(boolean createSchema) {
		this.createSchema = createSchema;
	}

	/**
	 * @return Returns the dropSchema.
	 */
	public boolean isDropSchema() {
		return dropSchema;
	}

	/**
	 * @param dropSchema The dropSchema to set.
	 */
	public void setDropSchema(boolean dropSchema) {
		this.dropSchema = dropSchema;
	}

	/**
	 * @return Returns the processDefinitions.
	 */
	public ProcessDefinition[] getProcessDefinitions() {
		return processDefinitions;
	}

	/**
	 * @param processDefinitions The processDefinitions to set.
	 */
	public void setProcessDefinitions(ProcessDefinition[] processDefinitions) {
		this.processDefinitions = processDefinitions;
	}

	/**
	 * @return Returns the sessionFactory.
	 */
	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	/**
	 * @param sessionFactory The sessionFactory to set.
	 */
	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	/**
	 * @return Returns the processDefinitionsResources.
	 */
	public Resource[] getProcessDefinitionsResources() {
		return processDefinitionsResources;
	}

	/**
	 * Used for loading the process definition from resources when the
	 * configuration is created. This method is an alternative to
	 * ProcesssDefinitionFactoryBean since when dealing with sub processes
	 * (inside the definitions), jBPM requires a JbpmContext to be active on its
	 * internal static stack.
	 * 
	 * @param processDefinitionsResources The processDefinitionsResources to
	 * set.
	 */
	public void setProcessDefinitionsResources(Resource[] processDefinitionsResources) {
		this.processDefinitionsResources = processDefinitionsResources;
	}

	/**
	 * @return Returns the factoryLocator.
	 */
	protected JbpmFactoryLocator getFactoryLocator() {
		return factoryLocator;
	}
	
}
