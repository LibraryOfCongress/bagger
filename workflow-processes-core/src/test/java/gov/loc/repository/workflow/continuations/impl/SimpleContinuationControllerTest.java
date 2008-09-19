package gov.loc.repository.workflow.continuations.impl;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.hibernate.HibernateHelper;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static gov.loc.repository.workflow.WorkflowConstants.*;
import gov.loc.repository.workflow.continuations.ResponseParameterMapper;
import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.jbpm.spring.LocalSessionFactoryBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/workflow-core-context.xml","classpath:conf/workflow-continuations-context.xml"})
public class SimpleContinuationControllerTest
{
	Mockery context = new Mockery();
	
	Long tokenInstanceId;
	Long processInstanceId;
	Map<String, Object> contextVariableMap = new HashMap<String, Object>();
	
	@Resource(name="continuationController")
	SimpleContinuationController controller;
	
	@Autowired
	JbpmConfiguration jbpmConfiguration;
	
	@Resource(name="&jbpmSessionFactory")
	private LocalSessionFactoryBean sessionFactoryBean;
		
	@Before
	public void setup() throws Exception
	{
		sessionFactoryBean.dropDatabaseSchema();		
		sessionFactoryBean.createDatabaseSchema();

		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			HibernateHelper.clearHibernateCache(jbpmContext.getSessionFactory());
		}
		finally
		{
			jbpmContext.close();
		}
		
		String processDefinitionString =
		      "<process-definition name='controller_test'>" +
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
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(processDefinitionString);		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinition.getName());			

			processInstanceId = processInstance.getId();
						
		}
		finally
		{
			jbpmContext.close();
		}	    
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			//Normally the set-up would be performed by a monitor or scheduler
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			//Makes sure everything is set-up OK
			
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
		
		final ResponseParameterMapper mapper = context.mock(ResponseParameterMapper.class);
		context.checking(new Expectations(){{
			one(mapper).map("foo");
			will(returnValue("foo2"));
		}});
		
		((SimpleContinuationControllerImpl)controller).addResponseParameterMapper("controller_test", mapper);
		
		contextVariableMap.put("foo", "bar");

	}
	
	@Test
	public void testInvokeSuccess() throws Exception {
		
		controller.invoke(tokenInstanceId, contextVariableMap, true);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("end1", token.getNode().getName());
			ExecutionContext executionContext = new ExecutionContext(token);
			assertEquals("bar", (String)executionContext.getVariable("foo2"));
			context.assertIsSatisfied();
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

    
	@Test
	public void testInvokeFailure() throws Exception {
		
		controller.invoke(tokenInstanceId, contextVariableMap, false);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("end2", token.getNode().getName());
			assertFalse(token.isSuspended());
			ExecutionContext executionContext = new ExecutionContext(token);
			assertEquals("bar", (String)executionContext.getVariable("foo2"));
			context.assertIsSatisfied();
			
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

	@Test
	public void testInvokeFailureWithoutTroubleshoot() throws Exception {
		
		((SimpleContinuationControllerImpl)controller).setTroubleshootTransition("missing");
		controller.invoke(tokenInstanceId, contextVariableMap, false);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("remote", token.getNode().getName());
			assertTrue(token.isSuspended());
			ExecutionContext executionContext = new ExecutionContext(token);
			assertEquals("bar", (String)executionContext.getVariable("foo2"));
			context.assertIsSatisfied();
			
		}
		finally
		{
			jbpmContext.close();
		}
		((SimpleContinuationControllerImpl)controller).setTroubleshootTransition(TRANSITION_TROUBLESHOOT);
	}
	
	
	@Test
	public void testInvokeError() throws Exception {
		
		controller.invoke(tokenInstanceId, contextVariableMap, "foo", "It's all fooed up");
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			Token token = jbpmContext.getToken(this.tokenInstanceId);
			assertEquals("end2", token.getNode().getName());
			assertFalse(token.isSuspended());
			ExecutionContext executionContext = new ExecutionContext(token);
			assertEquals("bar", (String)executionContext.getVariable("foo2"));
			context.assertIsSatisfied();
			
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
   	
}
