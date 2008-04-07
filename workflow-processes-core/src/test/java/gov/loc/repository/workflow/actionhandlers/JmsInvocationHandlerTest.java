package gov.loc.repository.workflow.actionhandlers;

import static gov.loc.repository.workflow.constants.FixtureConstants.QUEUE_1;
import static org.junit.Assert.*;
import gov.loc.repository.transfer.components.test.TestComponent;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;

import java.lang.reflect.Proxy;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.commons.configuration.Configuration;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class JmsInvocationHandlerTest {
	DummyActionHandler actionHandler= new DummyActionHandler(null);

	Connection connection;
	MessageConsumer consumer;
	Configuration configuration;
	
		
	@Before
	public void setup() throws Exception
	{
		configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
		//Setup a listener
		//Create the connection
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(configuration.getString("jms.connection"));
		connection = connectionFactory.createConnection();
		connection.start();
		
		//Create the session
		Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(QUEUE_1);
		
		//Create the consumer
		consumer = session.createConsumer(destination);
		//Make sure no messages
		assertNull(consumer.receiveNoWait());
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		try
		{
			connection.close();
		}
		catch(Throwable ignore)
		{				
		}
		
	}
	
	@Test
	public void testInvoke() throws Exception {
		configuration.clearProperty("none.TestComponent.queue");
		configuration.addProperty("none.TestComponent.queue", QUEUE_1);
		TestComponent testComponent = actionHandler.createObject(TestComponent.class);
		assertTrue(Proxy.getInvocationHandler(testComponent) instanceof JmsInvocationHandler);
		testComponent.test("foo", true, 1L);
		
		Message message = consumer.receive(500);
		assertNotNull(message);
		assertEquals(Long.toString(0), message.getJMSCorrelationID());
		assertEquals("test", message.getStringProperty("jobType"));
		assertEquals("queue://" + configuration.getString("jms.replytoqueue"), message.getJMSReplyTo().toString());
		assertTrue(message instanceof MapMessage);
		MapMessage mapMessage = (MapMessage)message;
		assertEquals("foo", mapMessage.getString("message"));
		assertTrue(mapMessage.getBoolean("istrue"));
		assertEquals(1L, mapMessage.getLong("key"));
		
	}

}
