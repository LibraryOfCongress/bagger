package gov.loc.repository.workflow.actionhandlers;

import static org.junit.Assert.*;

import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import gov.loc.repository.workflow.BaseHandlerTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.Message;
import javax.jms.MessageConsumer;
import javax.jms.Session;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.junit.After;
import org.junit.Test;

public class JmsQueueActionHandlerTest extends BaseHandlerTest {
	Connection connection;
	MessageConsumer consumer;
		
	@Override
	public void setup() throws Exception
	{
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
		
	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <state name='remote'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.JmsQueueActionHandler'>" +
	      "        <jobType>foo</jobType>" +
	      "        <queueName>" + QUEUE_1 + "</queueName>" +
	      "        <variableMap>" +
	      "          <entry><key>p1</key><value>v1</value></entry>" +
	      "          <entry><key>p2</key><value>v2</value></entry>" +
	      "        </variableMap>" +
	      "        <additionalParameterMap>" +
	      "          <entry><key>p3</key><value>c</value></entry>" +
	      "        </additionalParameterMap>" +	      
	      "      </action>" +
	      "    </event>" +
	      "    <transition name='continue' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
	      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    //Waiting at remote for signal
	    assertEquals("remote", processInstance.getRootToken().getNode().getName());

		Message message = consumer.receive(250);
		assertNotNull(message);
		assertEquals(Long.toString(processInstance.getRootToken().getId()), message.getJMSCorrelationID());
		assertEquals("foo", message.getStringProperty("jobType"));
		assertEquals("queue://" + configuration.getString("jms.replytoqueue"), message.getJMSReplyTo().toString());
		assertTrue(message instanceof MapMessage);
		MapMessage mapMessage = (MapMessage)message;
		assertEquals("a", mapMessage.getString("p1"));
		assertEquals("b", mapMessage.getString("p2"));
		assertEquals("c", mapMessage.getString("p3"));
	    
	}	
}
