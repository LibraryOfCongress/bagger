package gov.loc.repository.transfer.ui.springframework;

import gov.loc.repository.transfer.ui.controllers.ProcessDefinitionConfiguration;

import java.util.ArrayList;
import java.util.Map;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

public class ResourceBundleMessageSource extends
		org.springframework.context.support.ResourceBundleMessageSource implements ApplicationContextAware, InitializingBean {

	ApplicationContext context;
	String[] baseNameArray;
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context = context;
		
	}

	@SuppressWarnings("unchecked")
	public void afterPropertiesSet() throws Exception {
		ArrayList<String> baseNameList = new ArrayList<String>();
		if (this.baseNameArray != null)
		{
			for(String baseName : this.baseNameArray)
			{
				baseNameList.add(baseName);
			}
		}
		Map<String, ProcessDefinitionConfiguration> beanMap = this.context.getBeansOfType(ProcessDefinitionConfiguration.class);
		for(ProcessDefinitionConfiguration processDefinitionConfiguration : beanMap.values())
		{
			if (processDefinitionConfiguration.getResourceBundleBaseName() != null)
			{
				baseNameList.add(processDefinitionConfiguration.getResourceBundleBaseName());
			}
		}
		setBasenames(baseNameList.toArray(new String[0]));		
	}	
	
}
