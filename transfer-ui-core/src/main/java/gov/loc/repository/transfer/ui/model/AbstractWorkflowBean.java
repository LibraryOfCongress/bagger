package gov.loc.repository.transfer.ui.model;

import gov.loc.repository.serviceBroker.RequestingServiceBroker;

import java.text.MessageFormat;
import java.util.Locale;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.springframework.context.MessageSource;

public abstract class AbstractWorkflowBean {
	JbpmContext jbpmContext;
	WorkflowBeanFactory factory;
	private Locale locale = new Locale("en", "US");
	private MessageSource messageSource;
	//ServiceRequestDAO serviceRequestDAO;
	RequestingServiceBroker broker;
		
	static final Log log = LogFactory.getLog(AbstractWorkflowBean.class);	
	
	AbstractWorkflowBean() {
	}
	
	public void setWorkflowBeanFactory(WorkflowBeanFactory factory)
	{
		this.factory = factory;
	}
	
	public void setJbpmContext(JbpmContext jbpmContext)
	{
		this.jbpmContext = jbpmContext;
	}
	
	public void setServiceRequestBroker(RequestingServiceBroker broker)
	{
		this.broker = broker;
	}
	
	public abstract String getName();
	
	public abstract String getId();
	
	public void setMessageSource(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}
	
	public void setLocale(Locale locale)
	{
		this.locale = locale;
	}
	
	String getMessage(String code, String defaultMessage)
	{						
		String result = messageSource.getMessage(code.replaceAll(" ", "_"), null, defaultMessage, this.locale);
		log.debug(MessageFormat.format("Lookup code {0} returned {1}", code, result));
		return result;
	}
	
	/*
	public void setServiceRequestDAO(ServiceRequestDAO dao)
	{
		this.serviceRequestDAO = dao;
	}
	*/
	
}
