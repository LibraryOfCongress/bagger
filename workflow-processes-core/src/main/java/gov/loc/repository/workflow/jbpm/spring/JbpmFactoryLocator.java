package gov.loc.repository.workflow.jbpm.spring;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.BeanNameAware;
import org.springframework.beans.factory.access.BeanFactoryLocator;
import org.springframework.beans.factory.access.BeanFactoryReference;

/**
 * BeanFactoryLocator used for injecting Spring application context into JBPM.
 * The difference/advantage over the traditional SingletonBeanFactoryLocator is
 * that it does not parse a bean factory definition; it is used internally by
 * the jbpmSessionFactoryBean and it will register the bean factory/application
 * context containing it automatically under the name and and aliases of the
 * bean. If there is only one BeanFactory registered then a null value can be
 * used with setBeanName method. <p/> Note that in most cases, you don't have to
 * use this class directly since it is used internally by
 * LocalJbpmConfigurationFactoryBean.
 * 
 * @author Costin Leau
 * 
 */
public class JbpmFactoryLocator implements BeanFactoryLocator, BeanFactoryAware, BeanNameAware {

	private static final Log logger = LogFactory.getLog(JbpmFactoryLocator.class);

	// default factory name (for nested classes)
	private String factoryName = JbpmFactoryLocator.class.getName();

	// alias/bean name to BeanFactory
	protected static final Map beanFactories = new HashMap();

	// beanfactory to alias/bean name map
	protected static final Map beanFactoriesNames = new HashMap();

	protected static final Map referenceCounter = new HashMap();

	protected static boolean canUseDefaultBeanFactory = true;

	protected static BeanFactory defaultFactory = null;

	/**
	 * @see org.springframework.beans.factory.BeanFactoryAware#setBeanFactory(org.springframework.beans.factory.BeanFactory)
	 */
	@SuppressWarnings("unchecked")
	public void setBeanFactory(final BeanFactory beanFactory) throws BeansException {

		// add the factory as default if possible (if it's the only one)
		synchronized (JbpmFactoryLocator.class) {
			if (canUseDefaultBeanFactory) {
				if (defaultFactory == null) {
					defaultFactory = beanFactory;
					if (logger.isDebugEnabled())
						logger.debug("default beanFactoryReference=" + defaultFactory);
				}
				else {
					if (logger.isDebugEnabled())
						logger.debug("more then one beanFactory - default not possible to determine");
					canUseDefaultBeanFactory = false;
					defaultFactory = null;
				}
			}
		}

		// add name
		addToMap(factoryName, beanFactory);
		Integer counter = (Integer) referenceCounter.get(beanFactory);

		if (counter == null)
			referenceCounter.put(beanFactory, new Integer(0));

		// add aliases
		String[] aliases = beanFactory.getAliases(factoryName);
		List names = new ArrayList(1 + aliases.length);
		names.add(factoryName);

		for (int i = 0; i < aliases.length; i++) {
			addToMap(aliases[i], beanFactory);
			names.add(aliases[i]);
		}

		// append previous found names
		List previousNames = (List) beanFactoriesNames.get(beanFactory);
		if (previousNames != null)
			names.addAll(previousNames);

		beanFactoriesNames.put(beanFactory, names);

	}

	@SuppressWarnings("unchecked")
	protected void addToMap(String fName, BeanFactory factory) {
		if (logger.isDebugEnabled())
			logger.debug("adding key=" + fName + " w/ reference=" + factory);

		synchronized (beanFactories) {
			// override check
			if (beanFactories.containsKey(fName))
			{
				//throw new IllegalArgumentException("a beanFactoryReference already exists for key " + factoryName);
				logger.warn("a beanFactoryReference already exists for key " + factoryName);
			}
			else
			{
				beanFactories.put(fName, factory);
			}
		}
	}

	protected void removeReference(BeanFactory factory) {
		synchronized (referenceCounter) {
			Integer count = (Integer) referenceCounter.get(factory);
			// decrement counter
			int counter = count.intValue();
			counter--;
			if (counter == 0) {
				if (logger.isDebugEnabled())
					logger.debug("removing factory references under key " + factoryName);
				referenceCounter.remove(factory);

				// reset also default beanFactory
				if (referenceCounter.isEmpty()) {
					canUseDefaultBeanFactory = true;
					defaultFactory = null;
				}
				List names = (List) beanFactoriesNames.get(factory);
				beanFactoriesNames.remove(factory);

				synchronized (beanFactories) {
					for (Iterator iter = names.iterator(); iter.hasNext();) {
						beanFactories.remove(iter.next());
					}
				}
			}

			else
				referenceCounter.put(factory, new Integer(counter));
		}
	}

	/**
	 * @see org.springframework.beans.factory.access.BeanFactoryLocator#useBeanFactory(java.lang.String)
	 */
	public BeanFactoryReference useBeanFactory(String factoryKey) throws BeansException {
		// see if there is a default FactoryBean
		BeanFactory factory;

		if (factoryKey == null) {
			if (!canUseDefaultBeanFactory)
				throw new IllegalArgumentException(
						"a non-null factoryKey needs to be specified as there are more then one factoryKeys available ");
			factory = defaultFactory;
		}
		else {
			factory = (BeanFactory) beanFactories.get(factoryKey);
			if (factory == null)
			{
				//throw new IllegalArgumentException("there is no beanFactory under key " + factoryKey);
				logger.warn("there is no beanFactory under key " + factoryKey);
				return null;
			}
		}

		// increment counter
		synchronized (referenceCounter) {
			Integer counter = (Integer) referenceCounter.get(factory);
			referenceCounter.put(factory, new Integer(counter.intValue() + 1));
		}

		final BeanFactory finalFactory = factory;

		// simple implementation
		return new BeanFactoryReference() {
			private BeanFactory fact = finalFactory;

			public BeanFactory getFactory() {
				if (this.fact == null)
					throw new IllegalArgumentException("beanFactory already released");
				return this.fact;
			}

			public void release() throws FatalBeanException {
				if (fact != null) {
					removeReference(fact);
					// remove the factory reference
					this.fact = null;
				}
			}
		};
	}

	/**
	 * @see org.springframework.beans.factory.BeanNameAware#setTargetBean(java.lang.String)
	 */
	public void setBeanName(String name) {
		factoryName = name;
	}

}
