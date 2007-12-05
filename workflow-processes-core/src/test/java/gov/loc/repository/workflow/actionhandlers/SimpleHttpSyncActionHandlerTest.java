package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Expectations;
import static org.hamcrest.Matchers.*;
import static gov.loc.repository.workflow.hamcrest.collection.IsEmptyMap.isEmpty;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import gov.loc.repository.transfer.components.remote.GenericHttpClient;
import gov.loc.repository.workflow.utilities.ConfigurationHelper;

import java.util.Map;

@RunWith(JMock.class)
public class SimpleHttpSyncActionHandlerTest {
	static Mockery context = new JUnit4Mockery();
	
	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <node name='remote'>" +
	      "    <action name='httpsync' class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler'>" +
	      "      <baseUrl>http://localhost/test.html</baseUrl>" +
	      "      <variableList>" +
	      "        <element>v1</element>" +
	      "        <element>v2</element>" +
	      "      </variableList>" +
	      "      <additionalParameterMap>" +
	      "        <entry><key>v3</key><value>c</value></entry>" +
	      "        <entry><key>v4</key><value>d</value></entry>" +
	      "      </additionalParameterMap>" +	      
	      "    </action>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </node>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");
	    
		ConfigurationHelper.getConfiguration().addProperty("test1.httpsync.GenericHttpClient.factorymethod", "gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandlerTest.createMockClient");		
		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    assertEquals("end1", processInstance.getRootToken().getNode().getName());
	    	    
	}

	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockClient() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("v1", "a"), hasEntry("v2", "b"),hasEntry("v3", "c"),hasEntry("v4", "d"))));
			will(returnValue(true));
		}});
		return client;
	}
	
	//No additional parameters or variables
	@Test
	public void executeNoParametersOrVariables() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test2'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <node name='remote'>" +
	      "    <action name='httpsync' class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler'>" +
	      "      <baseUrl>http://localhost/test.html</baseUrl>" +
	      "    </action>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </node>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test2.httpsync.GenericHttpClient.factorymethod", "gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandlerTest.createMockClientWithNoParameters");		
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    assertEquals("end1", processInstance.getRootToken().getNode().getName());
	    	    
	}

	public static GenericHttpClient createMockClientWithNoParameters() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/test.html")), with(isEmpty(String.class, String.class)));
			will(returnValue(true));
		}});
		return client;
	}

	//Client return false
	@Test
	public void executeFalse() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test3'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <node name='remote'>" +
	      "    <action name='httpsync' class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler'>" +
	      "      <baseUrl>http://localhost/test.html</baseUrl>" +
	      "      <variableList>" +
	      "        <element>v1</element>" +
	      "        <element>v2</element>" +
	      "      </variableList>" +
	      "      <additionalParameterMap>" +
	      "        <entry><key>v3</key><value>c</value></entry>" +
	      "        <entry><key>v4</key><value>d</value></entry>" +
	      "      </additionalParameterMap>" +	      
	      "    </action>" +
	      "    <transition name='continue' to='end1' />" +
	      "  </node>" +
	      "  <end-state name='end1' />" +
	      "  <exception-handler>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      	      
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test3.httpsync.GenericHttpClient.factorymethod", "gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandlerTest.createMockClientFalse");		

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    assertTrue(processInstance.getRootToken().isSuspended());
	    	    
	}

	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockClientFalse() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("v1", "a"), hasEntry("v2", "b"),hasEntry("v3", "c"),hasEntry("v4", "d"))));
			will(returnValue(false));
		}});
		return client;
	}
		
	//Client throws exception
	@Test
	public void executeException() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test4'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <node name='remote'>" +
	      "    <action name='httpsync' class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler'>" +
	      "      <baseUrl>http://localhost/test.html</baseUrl>" +
	      "      <variableList>" +
	      "        <element>v1</element>" +
	      "        <element>v2</element>" +
	      "      </variableList>" +
	      "      <additionalParameterMap>" +
	      "        <entry><key>v3</key><value>c</value></entry>" +
	      "        <entry><key>v4</key><value>d</value></entry>" +
	      "      </additionalParameterMap>" +	      
	      "    </action>" +
	      "    <transition name='continue' to='end1' />" +
	      "  </node>" +
	      "  <end-state name='end1' />" +
	      "  <exception-handler>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      	      
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test4.httpsync.GenericHttpClient.factorymethod", "gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandlerTest.createMockClientException");		
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    assertTrue(processInstance.getRootToken().isSuspended());
	    	    
	}

	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockClientException() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("v1", "a"), hasEntry("v2", "b"),hasEntry("v3", "c"),hasEntry("v4", "d"))));
			will(throwException(new Exception("Ooops.  The client didn't work.")));
		}});
		return client;
	}
	
	//No baseUrl
	@Test(expected=Exception.class)
	public void executeNoBaseUrl() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test5'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <node name='remote'>" +
	      "    <action name='httpsync' class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler'>" +
//	      "      <baseUrl>http://localhost/test.html</baseUrl>" +
	      "      <variableList>" +
	      "        <element>v1</element>" +
	      "        <element>v2</element>" +
	      "      </variableList>" +
	      "      <additionalParameterMap>" +
	      "        <entry><key>v3</key><value>c</value></entry>" +
	      "        <entry><key>v4</key><value>d</value></entry>" +
	      "      </additionalParameterMap>" +	      
	      "    </action>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </node>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test5.httpsync.GenericHttpClient.factorymethod", "gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandlerTest.createMockClientNotCalled");		
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    	    	    
	}
	
	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockClientNotCalled() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			never(client).execute(with(any(String.class)), with(any(Map.class)));
		}});
		return client;
	}
	
	
	//Missing variable
	@Test(expected=Exception.class)
	public void executeMissingVariable() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test6'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <node name='remote'>" +
	      "    <action name='httpsync' class='gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandler'>" +
	      "      <baseUrl>http://localhost/test.html</baseUrl>" +
	      "      <variableList>" +
	      "        <element>v1</element>" +
	      "        <element>v2</element>" +
	      "      </variableList>" +
	      "      <additionalParameterMap>" +
	      "        <entry><key>v3</key><value>c</value></entry>" +
	      "        <entry><key>v4</key><value>d</value></entry>" +
	      "      </additionalParameterMap>" +	      
	      "    </action>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </node>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");

		ConfigurationHelper.getConfiguration().addProperty("test6.httpsync.GenericHttpClient.factorymethod", "gov.loc.repository.workflow.actionhandlers.SimpleHttpSyncActionHandlerTest.createMockClientNotCalled");		
		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
//	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    	    	    
	}
	
	@Test
	public void createGenericHttpClient() throws Exception
	{
		SimpleHttpSyncActionHandler handler = new SimpleHttpSyncActionHandler();
		assertTrue(handler.createObject(GenericHttpClient.class) instanceof GenericHttpClient);
	}
}
