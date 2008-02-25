package gov.loc.repository.service;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;


import java.util.HashMap;
import java.util.Map;

@Component("componentFactory")
public class ComponentFactory implements ApplicationContextAware, InitializingBean {

	private ApplicationContext context;
	private Map<String, String> jobTypeMap = new HashMap<String, String>();
	
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
	}

	public void afterPropertiesSet() throws Exception {
		this.jobTypeMap = JobTypeHelper.getJobTypeToBeanIdMap(this.context);		
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
	
}
