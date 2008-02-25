package gov.loc.repository.service;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import gov.loc.repository.utilities.ConfigurationFactory;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.jms.listener.DefaultMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component("componentContainer")
public class ComponentContainer implements ApplicationContextAware, InitializingBean {
	
	private static final Log log = LogFactory.getLog(ComponentContainer.class);

	private List<DefaultMessageListenerContainer> containerList = new ArrayList<DefaultMessageListenerContainer>();
	private ApplicationContext context;
	private String[] queueArray;
	private Set<String> jobTypeList;
	
	boolean isStarted = false;
	
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		
		this.context = context;
	}
	
	public Set<String> getJobTypeList()
	{
		return this.jobTypeList;
	}
	
	public String[] getQueues()
	{
		return this.queueArray;
	}
	
	public int getScheduledConsumersCount()
	{
		int count = 0;
		for(DefaultMessageListenerContainer container : this.containerList)
		{
			count += container.getScheduledConsumerCount();
		}
		
		return count;
	}
	
	public void afterPropertiesSet() throws Exception {
		Configuration configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
		queueArray = configuration.getStringArray("jms.queues");
		jobTypeList = JobTypeHelper.getJobTypeToBeanIdMap(this.context).keySet();
		
		String messageSelector = this.generateMessageSelector(jobTypeList);
		
		for(String queue : queueArray)
		{
			log.debug("Creating container for " + queue);
			DefaultMessageListenerContainer container = (DefaultMessageListenerContainer)context.getBean("jmsContainer");
			container.setDestinationName(queue);
			container.setMessageSelector(messageSelector);
			containerList.add(container);
		}

	}
		
	public void start()
	{
		for(DefaultMessageListenerContainer container : this.containerList)
		{
			log.debug("Starting container for " + container.getDestinationName());
			container.start();
		}
		isStarted = true;
	}
	
	public void stop()
	{
		for(DefaultMessageListenerContainer container : this.containerList)
		{
			log.debug("Stopping container for " + container.getDestinationName());
			container.stop();
		}
		isStarted = false;
	}
	
	public boolean isStarted()
	{
		return isStarted;
	}
	
	private String generateMessageSelector(Set<String> jobTypeList)
	{
		String messageSelector = "";
		if (jobTypeList != null)
		{
			for(String jobType : jobTypeList)
			{
				if (messageSelector.length() != 0)
				{
					messageSelector += " OR ";
				}
				messageSelector += "jobType = '" + jobType + "'";
			}
		}
		return messageSelector;
	}

}
