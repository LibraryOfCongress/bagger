package gov.loc.repository.workflow.actionhandlers;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.workflow.actionhandlers.annotations.ConfigurationField;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;

public class JmsQueueActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	public static final String JOBTYPE_PROPERTY="jobType";
	private static final Log log = LogFactory.getLog(JmsQueueActionHandler.class);	
	
	@ConfigurationField
	public String jobType;
	
	@ConfigurationField
	public String queueName;
	
	@ConfigurationField
	public Map<String,String> variableMap;
	
	@ConfigurationField(isRequired=false)
	public Map<String,String> additionalParameterMap = new HashMap<String,String>();
	
	public String connectionString = "${jms.connection}";
	
	public String replyToQueueName = "${jms.replytoqueue}";
	
	protected Map<String,String> parameterMap = new HashMap<String, String>();
	
	@Override
	protected void initialize() throws Exception {
		for(String parameterName : variableMap.keySet())
		{
			String value = (String)this.helper.getRequiredVariable(variableMap.get(parameterName));
			parameterMap.put(parameterName, value);
		}
	}	
	
	
	@Override
	protected void execute() throws Exception {
		Connection connection = null;
		try
		{
			//Create the connection
			ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionString);
			connection = connectionFactory.createConnection();
			connection.start();
			
			//Create the session
			Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			Destination destination = session.createQueue(queueName);
			Destination replyToDestination = session.createQueue(replyToQueueName);
			
			//Create the producer
			MessageProducer producer = session.createProducer(destination);
			
			MapMessage message = session.createMapMessage();
			message.setJMSReplyTo(replyToDestination);
			message.setStringProperty(JOBTYPE_PROPERTY, jobType);
			message.setJMSCorrelationID(Long.toString(this.executionContext.getToken().getId()));
			
			for(String parameterName : parameterMap.keySet())
			{
				if ("true".equalsIgnoreCase(parameterMap.get(parameterName)))
				{
					log.debug(MessageFormat.format("Adding boolean parameter {0} with value {1} to message", parameterName, parameterMap.get(parameterName)));
					message.setBoolean(parameterName, true);
				}
				else if ("false".equalsIgnoreCase(parameterMap.get(parameterName)))
				{
					log.debug(MessageFormat.format("Adding boolean parameter {0} with value {1} to message", parameterName, parameterMap.get(parameterName)));
					message.setBoolean(parameterName, false);
				}
				else
				{
					log.debug(MessageFormat.format("Adding string parameter {0} with value {1} to message", parameterName, parameterMap.get(parameterName)));
					message.setString(parameterName, parameterMap.get(parameterName));
				}
			}
			
			for(String parameterName : additionalParameterMap.keySet())
			{
				if ("true".equalsIgnoreCase(additionalParameterMap.get(parameterName)))
				{
					log.debug(MessageFormat.format("Adding boolean parameter {0} with value {1} to message", parameterName, parameterMap.get(parameterName)));
					message.setBoolean(parameterName, true);
				}
				else if ("false".equalsIgnoreCase(additionalParameterMap.get(parameterName)))
				{
					log.debug(MessageFormat.format("Adding boolean parameter {0} with value {1} to message", parameterName, parameterMap.get(parameterName)));
					message.setBoolean(parameterName, false);
				}
				else
				{
					log.debug(MessageFormat.format("Adding string parameter {0} with value {1} to message", parameterName, parameterMap.get(parameterName)));
					message.setString(parameterName, additionalParameterMap.get(parameterName));
				}
			}
			log.debug("Sending message");
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
	}

}
