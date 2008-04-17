package gov.loc.repository.workflow.actionhandlers;
import gov.loc.repository.transfer.components.Component;
import gov.loc.repository.transfer.components.annotations.JobType;
import gov.loc.repository.transfer.components.annotations.MapParameter;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;

import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.text.MessageFormat;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class JmsInvocationHandler implements InvocationHandler {

	private static final String CONNECTION_STRING_KEY = "jms.connection";
	private static final String REPLY_TO_QUEUENAME = "jms.replytoqueue";
	private static final String JOBTYPE_PROPERTY="jobType";

	private static final Log log = LogFactory.getLog(JmsInvocationHandler.class);	
		
	private String queueName;
	private Long tokenId;
	
	public JmsInvocationHandler(String queueName, Long tokenId) {
		this.queueName = queueName;
		this.tokenId = tokenId;
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
		
		Connection connection = null;
		Configuration configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);

		try
		{
			//Create the connection
			String connectionString = configuration.getString(CONNECTION_STRING_KEY);
			if (connectionString == null)
			{
				throw new Exception(CONNECTION_STRING_KEY + " is missing from configuration");
			}
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionString);
			connection = connectionFactory.createConnection();
			connection.start();
			
			//Create the session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			String replyToQueueName = configuration.getString(REPLY_TO_QUEUENAME);
			if (replyToQueueName == null)
			{
				throw new Exception(REPLY_TO_QUEUENAME + " is missing from configuration");
			}
			Destination replyToDestination = session.createQueue(replyToQueueName);
			
			//Create the producer
			MessageProducer producer = session.createProducer(destination);
			
			MapMessage message = session.createMapMessage();
			message.setJMSReplyTo(replyToDestination);
			message.setStringProperty(JOBTYPE_PROPERTY, jobTypeAnnot.name());
			message.setJMSCorrelationID(Long.toString(this.tokenId));
			
			for(int i=0; i < args.length; i++)
			{
				Class paramType = method.getParameterTypes()[i];
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
					message.setString(paramName, (String)args[i]);
				}
				else if (paramType.equals(Boolean.class) || paramType.equals(Boolean.TYPE))
				{
					message.setBoolean(paramName, (Boolean)args[i]);
				}
				else if (paramType.equals(Long.class) || paramType.equals(Long.TYPE))
				{
					message.setLong(paramName, (Long)args[i]);
				}				
				else
				{
					throw new Exception("Attempt to pass a parameter other than a boolean, long, or string");
				}
				
			}
			
			log.debug("Sending message: " + message.toString());
			
			producer.send(message);
			producer.close();
		}
		finally
		{
			try
			{
				connection.close();
			}
			catch(Throwable ignore)
			{	
			}
		}
		return null;
	}

}
