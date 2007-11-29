package gov.loc.repository.workflow.continuations.impl;

import static org.junit.Assert.*;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;
import gov.loc.repository.workflow.processdefinitions.ProcessDefinitionHelper;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;

import java.util.Map;
import java.util.HashMap;

public class SimpleContinuationControllerTest extends AbstractProcessDefinitionTest
{

	String processDefinition =
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='remote' />" +
	      "  </start-state>" +
	      "  <state name='remote'>" +
	      "    <transition name='troubleshoot' to='end2' />" +
	      "    <transition name='continue' to='end1' />" +
	      "  </state>" +
	      "  <end-state name='end1' />" +
	      "  <end-state name='end2' />" +
	      "  <exception-handler>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      	      
	      "</process-definition>";
	Map<String,String> optionalVariableMap = new HashMap<String,String>();
	Map<String,String> requiredVariableMap = new HashMap<String,String>();
	long tokenInstanceId;
	Map<String,String> variableMap = new HashMap<String,String>();
	SimpleContinuationController controller;
	
	@Before
	public void setUp() throws Exception
	{
		ProcessDefinitionHelper helper = new ProcessDefinitionHelper();
		helper.setProcessDefinition(processDefinition);		
		String processDefinitionName = helper.deploy();
		this.optionalVariableMap.clear();
		this.requiredVariableMap.clear();
		this.variableMap.clear();
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			//Normally the set-up would be performed by a monitor or scheduler
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			//Makes sure everything is set-up OK
			assertEquals(processDefinitionName, processInstance.getProcessDefinition().getName());
			
			processInstance.getContextInstance().setVariable("v1", "a");
			assertEquals("a", processInstance.getContextInstance().getVariable("v1"));
			assertFalse(processInstance.getContextInstance().hasVariable("v2"));
			
			processInstance.signal();
			assertEquals("remote", processInstance.getRootToken().getNode().getName());
			this.tokenInstanceId = processInstance.getRootToken().getId();
		}
		finally
		{
			jbpmContext.close();
		}

		controller = new SimpleContinuationControllerImpl();			
		controller.setRequiredParameters(this.requiredVariableMap);
		controller.setOptionalParameters(this.optionalVariableMap);

	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInvokeDefault() throws Exception {
		
		this.requiredVariableMap.put("p1", "v1");
		this.requiredVariableMap.put("p2","v2");
		this.optionalVariableMap.put("p3","v3");
		this.variableMap.put("p1", "b");
		this.variableMap.put("p2", "c");
		this.variableMap.put("p3", "d");
		this.variableMap.put("p4", "e");
		controller.invoke(tokenInstanceId, true, this.variableMap);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("end1", token.getNode().getName());
			assertEquals("b", token.getProcessInstance().getContextInstance().getVariable("v1"));
			assertEquals("c", token.getProcessInstance().getContextInstance().getVariable("v2"));
			assertEquals("d", token.getProcessInstance().getContextInstance().getVariable("v3"));
			assertFalse(token.getProcessInstance().getContextInstance().hasVariable("v4"));
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

    
	@Test
	public void testInvokeFailureWithoutTransition() throws Exception {
		
		this.requiredVariableMap.put("p1", "v1");
		this.requiredVariableMap.put("p2","v2");
		this.optionalVariableMap.put("p3","v3");
		this.variableMap.put("p1", "b");
		this.variableMap.put("p2", "c");
		this.variableMap.put("p3", "d");
		this.variableMap.put("p4", "e");
		controller.setFailureTransition(null);
		controller.invoke(tokenInstanceId, false, this.variableMap);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
			assertTrue(token.getProcessInstance().isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

	@Test
	public void testInvokeError() throws Exception {
		
		this.requiredVariableMap.put("p1", "v1");
		this.requiredVariableMap.put("p2","v2");
		this.optionalVariableMap.put("p3","v3");
		this.variableMap.put("p1", "b");
		this.variableMap.put("p2", "c");
		this.variableMap.put("p3", "d");
		this.variableMap.put("p4", "e");
		controller.invoke(tokenInstanceId, "foo", "It's all fooed up");
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
			assertTrue(token.getProcessInstance().isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
    
	
	//Failure
	@Test
	public void testInvokeFailureWithTransition() throws Exception {
		
		this.requiredVariableMap.put("p1", "v1");
		this.requiredVariableMap.put("p2","v2");
		this.optionalVariableMap.put("p3","v3");
		this.variableMap.put("p1", "b");
		this.variableMap.put("p2", "c");
		this.variableMap.put("p3", "d");
		this.variableMap.put("p4", "e");
		controller.setFailureTransition("troubleshoot");
		controller.invoke(tokenInstanceId, false, this.variableMap);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("end2", token.getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
	
	//Missing required variable
	@Test(expected=Exception.class)
	public void testInvokeMissingVariable() throws Exception{
		
		this.requiredVariableMap.put("p1", "v1");
		this.requiredVariableMap.put("p2","v2");
		this.optionalVariableMap.put("p3","v3");
		//this.variableMap.put("p1", "b");
		this.variableMap.put("p2", "c");
		this.variableMap.put("p3", "d");
		this.variableMap.put("p4", "e");
		controller.invoke(tokenInstanceId, true, this.variableMap);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
	
	//TokenInstanceId not valid
	@Test(expected=Exception.class)
	public void testInvokeMissingTokenInstanceId() throws Exception {
		
		this.requiredVariableMap.put("p1", "v1");
		this.requiredVariableMap.put("p2","v2");
		this.optionalVariableMap.put("p3","v3");
		this.variableMap.put("p1", "b");
		this.variableMap.put("p2", "c");
		this.variableMap.put("p3", "d");
		this.variableMap.put("p4", "e");
		controller.invoke(123456, true, this.variableMap);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
	
}
