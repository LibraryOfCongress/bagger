package gov.loc.repository.workflow.actionhandlers;
import gov.loc.repository.service.Component;
import gov.loc.repository.service.annotations.JobType;
import gov.loc.repository.service.annotations.MapParameter;
import gov.loc.repository.serviceBroker.RequestingServiceBroker;
import gov.loc.repository.serviceBroker.ServiceBrokerFactory;
import gov.loc.repository.serviceBroker.ServiceRequest;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class ServiceInvocationHandler implements InvocationHandler {

	//private static ApplicationContext context = new ClassPathXmlApplicationContext("classpath:conf/servicerequestbroker-context.xml");
	private ServiceBrokerFactory factory = new ServiceBrokerFactory();
	
	private static final Log log = LogFactory.getLog(ServiceInvocationHandler.class);	
		
	private String queueName;
	private Long tokenId;
	private RequestingServiceBroker broker;
	
	public ServiceInvocationHandler(String queueName, Long tokenId, RequestingServiceBroker broker) {
		this.queueName = queueName;
		this.tokenId = tokenId;
		this.broker = broker;
	}	
	
	public Object invoke(Object object, Method method, Object[] args)
			throws Throwable {
		if (! (object instanceof Component))
		{
			throw new Exception("Object is not a component");
		}
		JobType jobTypeAnnot = (JobType)method.getAnnotation(JobType.class);
		if (jobTypeAnnot == null)
		{
			throw new Exception("Method is not annotated with JobType");
		}
		
		ServiceRequest req = this.factory.createServiceRequest(this.tokenId.toString(), queueName, jobTypeAnnot.name());
		
		for(int i=0; i < args.length; i++)
		{
			Class<?> paramType = method.getParameterTypes()[i];
			String paramName = null;
			for(Annotation annot : method.getParameterAnnotations()[i])
			{
				if (annot instanceof MapParameter)
				{
					MapParameter mapParameterAnnot = (MapParameter)annot;
					paramName = mapParameterAnnot.name();
				}					
			}				
			if (paramName == null)
			{
				throw new Exception("MapParameter annotation is missing for param " + i);
			}
			log.debug(MessageFormat.format("Parameter type is {0}.  Parameter name is {1}", paramType.getName(), paramName));
			
			if (paramType.equals(String.class))
			{
				req.addString(paramName, (String)args[i]);
			}
			else if (paramType.equals(Boolean.class) || paramType.equals(Boolean.TYPE))
			{
				req.addBoolean(paramName, (Boolean)args[i]);
			}
			else if (paramType.equals(Long.class) || paramType.equals(Long.TYPE))
			{
				req.addInteger(paramName, (Long)args[i]);
			}				
			else
			{
				throw new Exception("Attempt to pass a parameter other than a boolean, long, or string");
			}
			
		}
		
		log.debug("Sending Service Request: " + req.toString());
		this.broker.sendRequest(req);
		
		return null;
	}

}
