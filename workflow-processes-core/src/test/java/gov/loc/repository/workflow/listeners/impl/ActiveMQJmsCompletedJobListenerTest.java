package gov.loc.repository.workflow.listeners.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.workflow.listeners.JmsCompletedJobListener;
import gov.loc.repository.workflow.listeners.impl.ActiveMQJmsCompletedJobListener;
import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;

import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.Destination;
import javax.jms.MapMessage;
import javax.jms.MessageProducer;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.junit.After;
import org.junit.Test;

public class ActiveMQJmsCompletedJobListenerTest extends AbstractProcessDefinitionTest{

	Connection connection;
	MessageProducer producer;
	Session session;
	JmsCompletedJobListener listener;
	Long tokenId;
	Long processInstanceId;
	private static String processDefinitionName;
	private static Long WAIT = 500L;
	
	@Override
	public void createFixtures() throws Exception {
		//Setup the test
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition name='test'>" +
			      "  <start-state>" +
			      "    <transition to='remote' />" +
			      "  </start-state>" +
			      "  <state name='remote'>" +
			      "    <transition name='continue' to='end' />" +
			      "  </state>" +
			      "  <exception-handler>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
			      "      </action>" +
			      "  </exception-handler>" +	      	      			      
			      "  <end-state name='end' />" +
			      "</process-definition>");

		processDefinitionName = processDefinition.getName();
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);
		}
		finally
		{
			jbpmContext.close();
		}	    
	}
	
	@Override
	public void setup() throws Exception
	{
		//Setup a listener
		//Create the connection
		ConnectionFactory connectionFactory = new ActiveMQConnectionFactory(this.getConfiguration().getString("jms.connection"));
		connection = connectionFactory.createConnection();
		connection.start();
		
		//Create the session
		session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
		Destination destination = session.createQueue(this.getConfiguration().getString("jms.replytoqueue"));
		
		//Create the producer
		producer = session.createProducer(destination);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{						
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			processInstanceId = processInstance.getId();
			tokenId = processInstance.getRootToken().getId();
		}
		finally
		{
			jbpmContext.close();
		}	    
				
		listener = new ActiveMQJmsCompletedJobListener();
		listener.start();
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			    
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			processInstance.signal();

			//Waiting at remote for signal
		    assertEquals("remote", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}
	
	@After
	public void tearDown() throws Exception
	{
		listener.stop();
		try
		{
			connection.close();
		}
		catch(Throwable ignore)
		{				
		}
		
	}
	
	@Test
	public void testDefault() throws Exception {
		//These aren't really necessary, since they're the default
		listener.setDefaultSuccessTransition("continue");
		listener.setDefaultFailureTransition(null);
		
		//Set required variables
		Map<String,Map<String,String>> requiredVariables = new HashMap<String,Map<String,String>>();
		Map<String,String> requiredVariableMap = new HashMap<String,String>();
		requiredVariableMap.put("p1", "v1");
		requiredVariables.put("foo", requiredVariableMap);
		listener.setRequiredParameters(requiredVariables);

		//Set required variables
		Map<String,Map<String,String>> optionalVariables = new HashMap<String,Map<String,String>>();
		Map<String,String> optionalVariableMap = new HashMap<String,String>();
		optionalVariableMap.put("p2", "v2");
		optionalVariables.put("foo", optionalVariableMap);
		listener.setOptionalParameters(optionalVariables);
		
		MapMessage message = session.createMapMessage();
		message.setJMSCorrelationID(Long.toString(tokenId));
		message.setStringProperty("jobType", "foo");
		message.setBoolean("isSuccess", true);
		message.setString("p1", "a");
		message.setString("p2", "b");
		message.setString("p3", "c");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertFalse(processInstance.isSuspended());
			assertEquals("end", processInstance.getRootToken().getNode().getName());
			assertEquals("a", processInstance.getContextInstance().getVariable("v1"));
			assertEquals("b", processInstance.getContextInstance().getVariable("v2"));
			assertFalse(processInstance.getContextInstance().hasVariable("v3"));
		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}

	@Test
	public void testReturnsError() throws Exception
	{
		//This isn't really necessary, since it's the default
		listener.setDefaultFailureTransition(null);
		
		MapMessage message = session.createMapMessage();
		message.setJMSCorrelationID(Long.toString(tokenId));
		message.setStringProperty("jobType", "foo");
		message.setString("error", "Darn");
		message.setString("errorDetail", "Darn, something went really wrong");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertTrue(processInstance.isSuspended());
			assertEquals("remote", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}
	
	@Test
	public void testNotAMapMessage() throws Exception
	{
		TextMessage message = session.createTextMessage();
		message.setJMSCorrelationID(Long.toString(tokenId));
		message.setStringProperty("jobType", "foo");
		message.setText("This shouldn't work");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertTrue(processInstance.isSuspended());
			assertEquals("remote", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}
	
	@Test
	public void testNoJobType() throws Exception
	{
		MapMessage message = session.createMapMessage();
		message.setJMSCorrelationID(Long.toString(tokenId));
		message.setBoolean("isSuccess", true);
		message.setString("p1", "a");
		message.setString("p2", "b");
		message.setString("p3", "c");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertTrue(processInstance.isSuspended());
			assertEquals("remote", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}
	
	@Test
	public void testNoCorrelationId() throws Exception
	{
		MapMessage message = session.createMapMessage();
		message.setStringProperty("jobType", "foo");
		message.setBoolean("isSuccess", true);
		message.setString("p1", "a");
		message.setString("p2", "b");
		message.setString("p3", "c");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertFalse(processInstance.isSuspended());
			assertEquals("remote", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}
	
	@Test
	public void testNoIsSuccess() throws Exception
	{
		MapMessage message = session.createMapMessage();
		message.setJMSCorrelationID(Long.toString(tokenId));
		message.setStringProperty("jobType", "foo");
		message.setString("p1", "a");
		message.setString("p2", "b");
		message.setString("p3", "c");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertTrue(processInstance.isSuspended());
			assertEquals("remote", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}	    		
	}

	@Test
	public void testMissingRequiredVariable() throws Exception
	{
		//Set required variables
		Map<String,Map<String,String>> requiredVariables = new HashMap<String,Map<String,String>>();
		Map<String,String> requiredVariableMap = new HashMap<String,String>();
		requiredVariableMap.put("p1", "v1");
		requiredVariables.put("foo", requiredVariableMap);
		listener.setRequiredParameters(requiredVariables);

		//Set required variables
		Map<String,Map<String,String>> optionalVariables = new HashMap<String,Map<String,String>>();
		Map<String,String> optionalVariableMap = new HashMap<String,String>();
		optionalVariableMap.put("p2", "v2");
		optionalVariables.put("foo", optionalVariableMap);
		listener.setOptionalParameters(optionalVariables);
		
		MapMessage message = session.createMapMessage();
		message.setJMSCorrelationID(Long.toString(tokenId));
		message.setStringProperty("jobType", "foo");
		message.setBoolean("isSuccess", true);
		message.setString("p2", "b");
		message.setString("p3", "c");
		
		producer.send(message);
		
		Thread.sleep(WAIT);
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{			
			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			assertTrue(processInstance.isSuspended());
			assertEquals("remote", processInstance.getRootToken().getNode().getName());

		}
		finally
		{
			jbpmContext.close();
		}	    
		
	}
}
