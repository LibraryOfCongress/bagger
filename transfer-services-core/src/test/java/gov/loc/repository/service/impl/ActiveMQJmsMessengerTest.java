package gov.loc.repository.service.impl;

import static org.junit.Assert.*;
import gov.loc.repository.service.Messenger;
import gov.loc.repository.service.RequestMessage;
import gov.loc.repository.service.ServiceConstants;
import gov.loc.repository.service.TaskResult;
import gov.loc.repository.utilities.ConfigurationFactory;

import java.util.HashSet;
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
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ActiveMQJmsMessengerTest {
		
	Connection connection;
	MessageProducer producer1;
	MessageProducer producer2;
	MessageConsumer consumer;
	Session session;
	Messenger messenger;
	Destination replyToDestination;
		
	@Before
	public void setUp() throws Exception {
		Configuration configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
		
		//Create the connection
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(configuration.getString("jms.connection"));
		connection = connectionFactory.createConnection();
		connection.start();
		
		//Create the session
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);		
		
		//Create the producer
		String[] queueArray = configuration.getStringArray("jms.queues"); 
		
		Destination destination1 = session.createQueue(queueArray[0]);
		producer1 = session.createProducer(destination1);

		Destination destination2 = session.createQueue(queueArray[1]);
		producer2 = session.createProducer(destination2);
				
		//Create the consumer
		replyToDestination = session.createQueue("completedjobqueue");
		consumer = session.createConsumer(replyToDestination);
		
		//Create the messenger
		messenger = new ActiveMQJmsMessenger();
		Set<String> jobTypeList = new HashSet<String>();
		jobTypeList.add("foo");
		messenger.start(jobTypeList);
		
	}

	@After
	public void teardown() throws Exception
	{
		messenger.stop();
		try
		{
			connection.close();
		}
		catch(Throwable ignore)
		{				
		}		
	}
	
	@Test
	public void testGetNextRequestMessage() throws Exception {
		assertNull(messenger.getNextRequestMessage());
		//Put a message on the queue
		producer1.send(this.generateMessage());
		
		Thread.sleep(250);
		//Get it off the queue
		RequestMessage requestMessage = messenger.getNextRequestMessage();
		assertNotNull(requestMessage);
		assertEquals("foo", requestMessage.getJobType());
		assertEquals(2, requestMessage.getVariableMap().size());

		assertNull(messenger.getNextRequestMessage());
		
		producer2.send(this.generateMessage());
		
		Thread.sleep(250);
		//Get it off the queue
		requestMessage = messenger.getNextRequestMessage();
		assertNotNull(requestMessage);
		assertEquals("foo", requestMessage.getJobType());
		assertEquals(2, requestMessage.getVariableMap().size());
		
		assertNull(messenger.getNextRequestMessage());
		
	}

	private MapMessage generateMessage() throws Exception
	{
		MapMessage message = session.createMapMessage();
		message.setStringProperty("jobType", "foo");
		message.setJMSCorrelationID("1");
		message.setJMSReplyTo(replyToDestination);
		message.setString("foo", "foo");
		message.setString("bar", "bar");
		
		return message;
	}
	
	@Test
	public void testSendResponseMessage() throws Exception {
		TaskResult taskResult = new TaskResult();
		taskResult.isSuccess = true;
		taskResult.variableMap.put("foo", "bar");
		
		JmsRequestMessage requestMessage = new JmsRequestMessage();
		requestMessage.setMessage(generateMessage());
		
		assertNull(consumer.receiveNoWait());
		
		messenger.sendResponseMessage(messenger.createMemento(requestMessage), taskResult);
		
		Message responseMessage = consumer.receive(500);
		assertNotNull(responseMessage);
		MapMessage responseMapMessage = (MapMessage)responseMessage;
		assertEquals("1", responseMapMessage.getJMSCorrelationID());
		assertTrue(responseMapMessage.getBoolean("isSuccess"));
		assertEquals("bar", responseMapMessage.getString("foo"));
		
	}

	@Test
	public void testNotSelectedMessage() throws Exception
	{
		assertNull(messenger.getNextRequestMessage());
		//Put a message on the queue
		MapMessage message = this.generateMessage();
		message.setStringProperty("jobType", "xfoo");
		producer1.send(message);
		
		Thread.sleep(250);
		//Get it off the queue
		assertNull(messenger.getNextRequestMessage());
		
	}
}
