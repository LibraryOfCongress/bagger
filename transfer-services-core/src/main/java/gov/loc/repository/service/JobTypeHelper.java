package gov.loc.repository.service;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.utilities.ConfigurationFactory;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.Configuration;
import org.springframework.beans.FatalBeanException;
import org.springframework.context.ApplicationContext;


public class JobTypeHelper {
	
	@SuppressWarnings("unchecked")
	public static Map<String, String> getJobTypeToBeanIdMap(ApplicationContext context) throws Exception
	{
		Configuration configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
		Map<String, String> jobTypeMap = new HashMap<String, String>();
		//Inspect the available beans to see which jobTypes they support
		Map<String,Component> beanMap = context.getBeansOfType(Component.class);
		Iterator<String> iter = beanMap.keySet().iterator();
		while(iter.hasNext())
		{
			String beanId = (String)iter.next();
			String key = "component." + beanId + ".enabled";
			boolean isEnabled = configuration.getBoolean(key, false);			
			if (isEnabled)
			{
				Object component = beanMap.get(beanId);
				Set<String> jobTypeSet = getJobTypeSet(component);
				for(String jobType : jobTypeSet)
				{
					if (jobTypeMap.containsKey(jobType))
					{
						throw new FatalBeanException("Multiple components can process jobType " + jobType);
					}
					jobTypeMap.put(jobType, beanId);
				}
			}
		}
		return jobTypeMap;
	}
	
	private static Set<String> getJobTypeSet(Object component)
	{
		Set<String> jobTypeList = getJobTypeSet(component.getClass());
		//Have to separately check interfaces since annotations from interfaces aren't inherited
		for(Class<?> clazz : component.getClass().getInterfaces())
		{
			jobTypeList.addAll(getJobTypeSet(clazz));
		}
		return jobTypeList;
	}
	
	private static Set<String> getJobTypeSet(Class<?> clazz)
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
