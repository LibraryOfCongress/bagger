package gov.loc.repository.service.impl;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.ListableBeanFactory;

import gov.loc.repository.service.CallableAdapter;
import gov.loc.repository.service.RequestMessage;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.service.TaskFactory;
import gov.loc.repository.service.TaskResult;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.utilities.ConfigurationFactory;

public class SpringTaskFactory implements TaskFactory, BeanFactoryAware{

	private static final Log log = LogFactory.getLog(SpringTaskFactory.class);
		
	private ListableBeanFactory factory = null;
	private Map<String,String> jobTypeMap = new HashMap<String, String>();
	Configuration configuration;

	public SpringTaskFactory() throws Exception {
		configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
	}
	
	@SuppressWarnings("unchecked")
	public Callable<TaskResult> newTask(RequestMessage requestMessage) throws Exception {
		String jobType = requestMessage.getJobType();
		//Let's make sure we can handle that jobType
		if (! jobTypeMap.containsKey(jobType))
		{
			throw new Exception("Can't handle jobType " + jobType);
		}
		
		String beanId = jobTypeMap.get(jobType);
		if (! this.factory.containsBean(beanId))
		{
			throw new Exception("BeanFactory not configured to create bean with beanId " + beanId);
		}
		
		//Create the bean
		Object bean = this.factory.getBean(beanId);
		return new CallableAdapter(bean, jobType, requestMessage.getVariableMap());
	}
	
	@SuppressWarnings("unchecked")
	public void setBeanFactory(BeanFactory factory) throws BeansException {
		
		if (this.factory != null)
		{
			throw new FatalBeanException("Can only supply a BeanFactory once");
		}
		if (!(factory instanceof ListableBeanFactory))
		{
			throw new FatalBeanException("Supplied BeanFactory isn't listable");
		}
		this.factory = (ListableBeanFactory)factory;
						
		//Inspect the available beans to see which jobTypes they support
		Map beanMap = this.factory.getBeansOfType(Component.class);
		Iterator iter = beanMap.keySet().iterator();
		while(iter.hasNext())
		{
			String beanId = (String)iter.next();
			String key = "service." + beanId + ".enabled";
			boolean isEnabled = configuration.getBoolean(key, false);
			log.debug(MessageFormat.format("{0} is {1}", key, isEnabled));
			if (isEnabled)
			{
				Object component = beanMap.get(beanId);
				Set<String> jobTypeSet = this.getJobTypeSet(component);
				for(String jobType : jobTypeSet)
				{
					if (this.jobTypeMap.containsKey(jobType))
					{
						throw new FatalBeanException("Multiple components can process jobType " + jobType);
					}
					this.jobTypeMap.put(jobType, beanId);
				}
			}
		}
	}
	
	private Set<String> getJobTypeSet(Object component)
	{
		Set<String> jobTypeList = this.getJobTypeSet(component.getClass());
		//Have to separately check interfaces since annotations from interfaces aren't inherited
		for(Class<?> clazz : component.getClass().getInterfaces())
		{
			jobTypeList.addAll(this.getJobTypeSet(clazz));
		}
		return jobTypeList;
	}
	
	private Set<String> getJobTypeSet(Class<?> clazz)
	{
		Set<String> jobTypeSet = new HashSet<String>();
		for(Method method : clazz.getDeclaredMethods())
		{
			JobType jobTypeAnnot = (JobType)method.getAnnotation(JobType.class);
			if (jobTypeAnnot != null)
			{
				jobTypeSet.add(jobTypeAnnot.name());
			}
				
		}
		return jobTypeSet;
	}
	
	public Set<String> getJobTypeList() {
		return this.jobTypeMap.keySet();
	}	
}
