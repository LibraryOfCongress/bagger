package gov.loc.repository.service;

import gov.loc.repository.utilities.ConfigurationFactory;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageConsumer;
import javax.jms.MessageProducer;
import javax.jms.Session;

import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;


public class ComponentContainerTest {

	protected ComponentContainer container;
	protected ApplicationContext context;
	protected Configuration configuration;
	protected ConnectionFactory connectionFactory;
	
	static final String REPLY_QUEUE = "reply_to_me";
	static final long WAIT = 10000;
	
	@Before
	public void setup() throws Exception
	{
		context = new ClassPathXmlApplicationContext("services-context-core.xml");
		container = (ComponentContainer)context.getBean("componentContainer");
		assertNotNull(container);
		
		configuration = ConfigurationFactory.getConfiguration(ServiceConstants.PROPERTIES_NAME);
		
		connectionFactory = (ConnectionFactory)context.getBean("connectionFactory");
		this.createMessages();
	}
		
	private void createMessages() throws Exception
	{
		Connection connection = connectionFactory.createConnection();
		connection.start();
		
		//Create the session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		String[] queueArray = configuration.getStringArray("jms.queues");
		for(String queue : queueArray)
		{
			Destination destination = session.createQueue(queue);
			MessageProducer producer = session.createProducer(destination);
			Destination replyToDestination = session.createQueue(REPLY_QUEUE);
			for(int i=0; i < 10; i++)
			{
				MapMessage mapMessage = session.createMapMessage();
				mapMessage.setJMSReplyTo(replyToDestination);
				mapMessage.setStringProperty("jobType", "test");
				mapMessage.setJMSCorrelationID(Integer.toString(i));
				mapMessage.setString("message", queue);
				mapMessage.setBoolean("istrue", true);
				mapMessage.setLong("key", 1L);
				producer.send(mapMessage);
				
			}
			producer.close();
		}
		
	}

	
	@Test
	public void testGetComponent() throws Exception
	{
		container.start();
		assertTrue(container.isStarted);
		
		//Check the reply messages
		Connection connection = connectionFactory.createConnection();
		connection.start();
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(REPLY_QUEUE);
		MessageConsumer consumer = session.createConsumer(destination);
		MapMessage message = (MapMessage)consumer.receive(WAIT);
		int count = 0;
		while(message != null)
		{
			count++;
			assertTrue(message.getBoolean("isSuccess"));
			message = (MapMessage)consumer.receive(WAIT);
			
		}
		assertEquals(20, count);
		
	}
	
}
