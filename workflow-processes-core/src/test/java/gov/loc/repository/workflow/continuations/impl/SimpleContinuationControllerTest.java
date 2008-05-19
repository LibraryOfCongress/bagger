package gov.loc.repository.workflow.continuations.impl;

import static org.junit.Assert.*;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import org.junit.Before;
import org.junit.Test;

import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;

public class SimpleContinuationControllerTest extends AbstractProcessDefinitionTest
{

	String processDefinitionString =
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
	      "</process-definition>";
	long tokenInstanceId;
	SimpleContinuationController controller;
	
	@Before
	public void setUp() throws Exception
	{
		String processDefinitionName;
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(processDefinitionString);
			jbpmContext.deployProcessDefinition(processDefinition);
			processDefinitionName = processDefinition.getName();
			
		}
		finally
		{
			jbpmContext.close();
		}
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
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

	}

	@Test
	public void testInvokeSuccess() throws Exception {
		
		controller.invoke(tokenInstanceId, true);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("end1", token.getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

    
	@Test
	public void testInvokeFailure() throws Exception {
		
		controller.invoke(tokenInstanceId, false);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
			assertTrue(token.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

	@Test
	public void testInvokeError() throws Exception {
		
		controller.invoke(tokenInstanceId, "foo", "It's all fooed up");
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
			assertTrue(token.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
   	
}
