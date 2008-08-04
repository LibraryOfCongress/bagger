package gov.loc.repository.service.component;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.container.ServiceContainer;

import java.lang.reflect.Method;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import javax.annotation.PostConstruct;

public class ComponentFactory implements ApplicationContextAware {

	private ApplicationContext context;
	private Map<String, String> jobTypeMap = new HashMap<String, String>();
	private Properties configProperties;
	
	private static final Log log = LogFactory.getLog(ServiceContainer.class);
	
	public ComponentFactory(Properties configProperties) {
		this.configProperties = configProperties; 
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}
	
	@PostConstruct
	public void init() throws Exception {
		this.jobTypeMap = this.getJobTypeToBeanIdMap();
	}

	public Collection<String> getJobTypes()
	{
		return this.jobTypeMap.keySet();
	}
	
	public boolean handlesJobType(String jobType)
	{
		return this.jobTypeMap.containsKey(jobType);
	}
	
	public Object getComponent(String jobType) throws Exception
	{
		//Let's make sure we can handle that jobType
		if (! jobTypeMap.containsKey(jobType))
		{
			throw new Exception("Can't handle jobType " + jobType);
		}
		
		String beanId = jobTypeMap.get(jobType);
		if (! this.context.containsBean(beanId))
		{
			throw new Exception("BeanFactory not configured to create bean with beanId " + beanId);
		}
		
		//Create the bean
		return this.context.getBean(beanId);
		
	}
	
	@SuppressWarnings("unchecked")
	public Map<String, String> getJobTypeToBeanIdMap() throws Exception
	{
		Map<String, String> jobTypeMap = new HashMap<String, String>();
		//Inspect the available beans to see which jobTypes they support
		Map<String,Component> beanMap = context.getBeansOfType(Component.class);
		System.out.println("!!!!!!!!!!!!!!!!!!!:" + beanMap.size());
		Iterator<String> iter = beanMap.keySet().iterator();
		while(iter.hasNext())
		{
			String beanId = (String)iter.next();
			Object component = beanMap.get(beanId);
			Set<String> jobTypeSet = getJobTypeSet(component);
			for(String jobType : jobTypeSet)
			{
				if (! configProperties.containsKey(jobType) || (configProperties.containsKey(jobType) && beanId.equals(configProperties.get(jobType))))
				{
					if (jobTypeMap.containsKey(jobType))
					{
						throw new FatalBeanException("Multiple components can process jobType " + jobType);
					}
					log.debug(MessageFormat.format("Adding beanId {0} for jobType {1} tp jobTypeToBeanIdMap", beanId, jobType));
					jobTypeMap.put(jobType, beanId);
				}
				else
				{
					log.debug(MessageFormat.format("Not adding beanId {0} for jobType {1} tp jobTypeToBeanIdMap", beanId, jobType));					
				}
			}
		}
		return jobTypeMap;
	}
	
	private Set<String> getJobTypeSet(Object component)
	{
		Set<String> jobTypeList = getJobTypeSet(component.getClass());
		//Have to separately check interfaces since annotations from interfaces aren't inherited
		for(Class<?> clazz : component.getClass().getInterfaces())
		{
			jobTypeList.addAll(getJobTypeSet(clazz));
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
	
	
}
