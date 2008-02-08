package gov.loc.repository.service.impl;

import java.util.Set;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.service.Memento;
import gov.loc.repository.service.Messenger;
import gov.loc.repository.service.RequestMessage;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.service.TaskResult;

public class ActiveMQJmsMessenger implements Messenger {

	private static final Log log = LogFactory.getLog(ActiveMQJmsMessenger.class);
		
	private Connection connection;
	private MessageConsumer[] consumerArray;
	private Session session;
	
	public RequestMessage getNextRequestMessage() throws Exception {
		for(MessageConsumer consumer : consumerArray)
		{
			Message message = consumer.receiveNoWait();
			if (message != null)
			{
				log.debug("Message in queue.");
				JmsRequestMessage requestMessage = new JmsRequestMessage();
				requestMessage.setMessage(message);
				return requestMessage;
				
			}
		}
		log.debug("No messages in queue.");
		return null;

	}

	public void sendResponseMessage(Memento memento,
			TaskResult taskResult) throws Exception {
		if (! (memento instanceof JmsRequestMessageMemento))
		{
			throw new Exception("Only can send responses for JmsRequestMessages");
		}
		JmsRequestMessageMemento requestMessageMemento = (JmsRequestMessageMemento)memento;
		Destination destination = session.createQueue(requestMessageMemento.getJMSReplyTo());
		MessageProducer producer = session.createProducer(destination);
		MapMessage replyMessage = session.createMapMessage();
		replyMessage.setStringProperty("jobType", requestMessageMemento.getJobType());
		replyMessage.setJMSCorrelationID(requestMessageMemento.getJMSCorrelationId());

		if (taskResult.error != null)
		{
			replyMessage.setString("error", taskResult.error);
			replyMessage.setString("errorDetail", taskResult.error);			
		}
		else
		{
			replyMessage.setBoolean("isSuccess", taskResult.isSuccess);
			for(String name : taskResult.variableMap.keySet())
			{
				replyMessage.setString(name, taskResult.variableMap.get(name));
			}			
		}
				
		producer.send(replyMessage);
		producer.close();
	}
	
	public void start(Set<String> jobTypeList) throws Exception
	{
		this.stop();
		
		Configuration configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
		
		//Setup a listener
		//Create the connection
		String connectionString = configuration.getString("jms.connection");
		log.debug("Connecting to " + connectionString);
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(connectionString);
		connection = connectionFactory.createConnection();
		connection.start();
		
		//Create the sessions
		String[] queueArray = configuration.getStringArray("jms.queues");
		consumerArray = new MessageConsumer[queueArray.length];
		int i=0;
		for(String queue : queueArray)
		{
			session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
			log.debug("Queue is " + queue);
			Destination destination = session.createQueue(queue);		
			//Create the consumer			
			consumerArray[i] = session.createConsumer(destination, this.generateMessageSelector(jobTypeList));
			i++;
		}
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
		log.debug("Message Selector is " + messageSelector);
		return messageSelector;
	}
	
	public void stop()
	{
	
		try
		{
			for(MessageConsumer consumer : consumerArray)
			{
				consumer.close();
			}
		}
		catch(Throwable ignore)
		{			
		}

		try
		{
			connection.close();
		}
		catch(Throwable ignore)
		{			
		}
	}
	
	@Override
	protected void finalize() throws Throwable {
		stop();
	}

	public Memento createMemento(RequestMessage message) throws Exception {
		if (! (message instanceof JmsRequestMessage))
		{
			throw new Exception("RequestMessage is not a JmsRequestMessage");
		}
		return new JmsRequestMessageMemento((JmsRequestMessage)message);
	}

}
