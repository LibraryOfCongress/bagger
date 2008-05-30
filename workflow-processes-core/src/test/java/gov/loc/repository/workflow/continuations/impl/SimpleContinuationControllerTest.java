package gov.loc.repository.workflow.continuations.impl;

import static org.junit.Assert.*;

import javax.annotation.Resource;

import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.hibernate.HibernateHelper;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import gov.loc.repository.workflow.continuations.SimpleContinuationController;
import gov.loc.repository.workflow.jbpm.spring.LocalSessionFactoryBean;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/workflow-core-context.xml","classpath:conf/workflow-continuations-context.xml"})
public class SimpleContinuationControllerTest
{
		
	Long tokenInstanceId;
	Long processInstanceId;
	
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
