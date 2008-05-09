package gov.loc.repository.service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;


import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.PostConstruct;

@Component("componentFactory")
public class ComponentFactory {

	private ApplicationContext context;
	private Map<String, String> jobTypeMap = new HashMap<String, String>();
	
	@Autowired
	public ComponentFactory(ApplicationContext context) {
		this.context = context;
	}
	
	@PostConstruct
	public void init() throws Exception {
		this.jobTypeMap = JobTypeHelper.getJobTypeToBeanIdMap(this.context);
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
	
}
