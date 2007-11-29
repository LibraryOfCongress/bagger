package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Expectations;
import static org.hamcrest.Matchers.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import gov.loc.repository.transfer.components.remote.GenericHttpClient;

import java.util.Map;

@RunWith(JMock.class)
public class SimpleHttpAsyncActionHandlerTest {
	static Mockery context = new JUnit4Mockery();
	static String tokenInstanceId;
	
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
	      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
	      "        <baseUrl>http://localhost/test.html</baseUrl>" +
	      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
	      "        <variableList>" +
	      "          <element>v1</element>" +
	      "          <element>v2</element>" +
	      "        </variableList>" +
	      "        <additionalParameterMap>" +
	      "          <entry><key>v3</key><value>c</value></entry>" +
	      "          <entry><key>v4</key><value>d</value></entry>" +
	      "        </additionalParameterMap>" +	      
	      "        <factoryMethodMap>" +
	      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClient</value></entry>" +
	      "        </factoryMethodMap>" +
	      "      </action>" +
	      "    </event>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </state>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    assertEquals("remote", processInstance.getRootToken().getNode().getName());
	    	    
	}

	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockClient() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("callback", "http://localhost/response.html"),hasEntry("v1", "a"), hasEntry("v2", "b"),hasEntry("v3", "c"),hasEntry("v4", "d"),hasEntry("tokeninstanceid", tokenInstanceId))));
			will(returnValue(true));
		}});
		return client;
	}
	
	//No additional parameters or variables
	@Test
	public void executeNoParametersOrVariables() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='remote' />" +
			      "  </start-state>" +
			      "  <state name='remote'>" +
			      "    <event type='node-enter'>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
			      "        <baseUrl>http://localhost/test.html</baseUrl>" +
			      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
			      "        <factoryMethodMap>" +
			      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClientWithNoParameters</value></entry>" +
			      "        </factoryMethodMap>" +
			      "      </action>" +
			      "    </event>" +
			      "    <transition name='troubleshoot' to='end2' />" +
			      "    <transition name='continue' to='end1' />" +
			      "  </state>" +
			      "  <end-state name='end1' />" +
			      "  <end-state name='end2' />" +
			      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();
	    
	    assertEquals("remote", processInstance.getRootToken().getNode().getName());
	    	    
	}

	@SuppressWarnings("unchecked")
	public static GenericHttpClient createMockClientWithNoParameters() throws Exception
	{
		//Setup mock
		final GenericHttpClient client = context.mock(GenericHttpClient.class);
		context.checking(new Expectations() {{
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("callback", "http://localhost/response.html"),hasEntry("tokeninstanceid", tokenInstanceId))));
			will(returnValue(true));
		}});
		return client;
	}

	//Client return false
	@Test
	public void executeFalse() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='remote' />" +
			      "  </start-state>" +
			      "  <state name='remote'>" +
			      "    <event type='node-enter'>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
			      "        <baseUrl>http://localhost/test.html</baseUrl>" +
			      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
			      "        <variableList>" +
			      "          <element>v1</element>" +
			      "          <element>v2</element>" +
			      "        </variableList>" +
			      "        <additionalParameterMap>" +
			      "          <entry><key>v3</key><value>c</value></entry>" +
			      "          <entry><key>v4</key><value>d</value></entry>" +
			      "        </additionalParameterMap>" +	      
			      "        <factoryMethodMap>" +
			      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClientFalse</value></entry>" +
			      "        </factoryMethodMap>" +
			      "      </action>" +
			      "    </event>" +
			      "    <transition name='continue' to='end1' />" +
			      "  </state>" +
			      "  <end-state name='end1' />" +
			      "  <exception-handler>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
			      "      </action>" +
			      "  </exception-handler>" +	      			      
			      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
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
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("callback", "http://localhost/response.html"),hasEntry("v1", "a"), hasEntry("v2", "b"),hasEntry("v3", "c"),hasEntry("v4", "d"),hasEntry("tokeninstanceid", tokenInstanceId))));
			will(returnValue(false));
		}});
		return client;
	}
		
	//Client throws exception
	@Test
	public void executeException() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='remote' />" +
			      "  </start-state>" +
			      "  <state name='remote'>" +
			      "    <event type='node-enter'>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
			      "        <baseUrl>http://localhost/test.html</baseUrl>" +
			      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
			      "        <variableList>" +
			      "          <element>v1</element>" +
			      "          <element>v2</element>" +
			      "        </variableList>" +
			      "        <additionalParameterMap>" +
			      "          <entry><key>v3</key><value>c</value></entry>" +
			      "          <entry><key>v4</key><value>d</value></entry>" +
			      "        </additionalParameterMap>" +	      
			      "        <factoryMethodMap>" +
			      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClientException</value></entry>" +
			      "        </factoryMethodMap>" +
			      "      </action>" +
			      "    </event>" +
			      "    <transition name='continue' to='end1' />" +
			      "  </state>" +
			      "  <end-state name='end1' />" +
			      "  <exception-handler>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
			      "      </action>" +
			      "  </exception-handler>" +	      			      
			      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
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
			one(client).execute(with(equalTo("http://localhost/test.html")), with(allOf(hasEntry("callback", "http://localhost/response.html"),hasEntry("v1", "a"), hasEntry("v2", "b"),hasEntry("v3", "c"),hasEntry("v4", "d"),hasEntry("tokeninstanceid", tokenInstanceId))));
			will(throwException(new Exception("Ooops.  The client didn't work.")));
		}});
		return client;
	}

	//No baseUrl
	@Test(expected=Exception.class)
	public void executeNoBaseUrl() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(				
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <state name='remote'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
//	      "        <baseUrl>http://localhost/test.html</baseUrl>" +
	      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
	      "        <variableList>" +
	      "          <element>v1</element>" +
	      "          <element>v2</element>" +
	      "        </variableList>" +
	      "        <additionalParameterMap>" +
	      "          <entry><key>v3</key><value>c</value></entry>" +
	      "          <entry><key>v4</key><value>d</value></entry>" +
	      "        </additionalParameterMap>" +	      
	      "        <factoryMethodMap>" +
	      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClientNotCalled</value></entry>" +
	      "        </factoryMethodMap>" +
	      "      </action>" +
	      "    </event>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </state>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();

	    assertEquals("end2", processInstance.getRootToken().getNode().getName());
	    
	}

	//No callbackBaseUrl
	@Test(expected=Exception.class)
	public void executeNoCallbackBaseUrl() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(				
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <state name='remote'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
	      "        <baseUrl>http://localhost/test.html</baseUrl>" +
//	      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
	      "        <variableList>" +
	      "          <element>v1</element>" +
	      "          <element>v2</element>" +
	      "        </variableList>" +
	      "        <additionalParameterMap>" +
	      "          <entry><key>v3</key><value>c</value></entry>" +
	      "          <entry><key>v4</key><value>d</value></entry>" +
	      "        </additionalParameterMap>" +	      
	      "        <factoryMethodMap>" +
	      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClientNotCalled</value></entry>" +
	      "        </factoryMethodMap>" +
	      "      </action>" +
	      "    </event>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </state>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
	    processInstance.getContextInstance().setVariable("v1", "a");
	    processInstance.getContextInstance().setVariable("v2", "b");
	    processInstance.signal();

	    assertEquals("end2", processInstance.getRootToken().getNode().getName());
	    
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
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='remote' />" +
			      "  </start-state>" +
			      "  <state name='remote'>" +
			      "    <event type='node-enter'>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandler'>" +
			      "        <baseUrl>http://localhost/test.html</baseUrl>" +
			      "        <callbackBaseUrl>http://localhost/response.html</callbackBaseUrl>" +	      
			      "        <variableList>" +
			      "          <element>v1</element>" +
			      "          <element>v2</element>" +
			      "        </variableList>" +
			      "        <additionalParameterMap>" +
			      "          <entry><key>v3</key><value>c</value></entry>" +
			      "          <entry><key>v4</key><value>d</value></entry>" +
			      "        </additionalParameterMap>" +	      
			      "        <factoryMethodMap>" +
			      "          <entry><key>GenericHttpClient</key><value>gov.loc.repository.workflow.actionhandlers.SimpleHttpAsyncActionHandlerTest.createMockClientNotCalled</value></entry>" +
			      "        </factoryMethodMap>" +
			      "      </action>" +
			      "    </event>" +
			      "    <transition name='troubleshoot' to='end2' />" +
			      "    <transition name='continue' to='end1' />" +
			      "  </state>" +
			      "  <end-state name='end1' />" +
			      "  <end-state name='end2' />" +
			      "</process-definition>");
			    
			    ProcessInstance processInstance = new ProcessInstance(processDefinition);
			    tokenInstanceId = Long.toString(processInstance.getRootToken().getId());
			    //processInstance.getContextInstance().setVariable("v1", "a");
			    processInstance.getContextInstance().setVariable("v2", "b");
			    processInstance.signal();

			    assertEquals("end2", processInstance.getRootToken().getNode().getName());
			    	    	    	    
	}
	
	@Test
	public void createGenericHttpClient() throws Exception
	{
		SimpleHttpAsyncActionHandler handler = new SimpleHttpAsyncActionHandler();
		assertTrue(handler.createObject(GenericHttpClient.class) instanceof GenericHttpClient);
	}
	
}
