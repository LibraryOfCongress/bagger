package gov.loc.repository.workflow.actionhandlers;

import static org.junit.Assert.*;

import java.util.Collection;

import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;
import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;

import org.apache.commons.configuration.Configuration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ActionHandler;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(JMock.class)
public class ExceptionActionHandlerTest extends AbstractProcessDefinitionTest
{
	static Mockery context;
	static String tokenInstanceId;
	private long processInstanceId;
	private JbpmContext jbpmContext;
	Configuration configuration;
	
	@Before
	public void setup() throws Exception
	{
		configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
		context = new JUnit4Mockery();
	}
	
	@Test
	public void executeExceptionInSynActionHandler() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='ExceptionThrowingActionHandler' />" +
	      "    <transition name='continue2' to='end' />" +
	      "  </node>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");

		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test1.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
	    this.moveAndResume();	    
	}

	@Test
	public void executeExceptionInAsynActionHandler() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test2'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <node name='a' aync='true'>" +
	      "    <action class='ExceptionThrowingActionHandler' />" +
	      "    <transition name='continue2' to='end' />" +
	      "  </node>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test2.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
		}
		finally
		{
			jbpmContext.close();
		}

		//Now lets start the JobExecutor
		jbpmConfiguration.startJobExecutor();
		Thread.sleep(1000);
		jbpmConfiguration.getJobExecutor().stopAndJoin();
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);				
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
	    this.moveAndResume();	    
	}
		
	@Test
	public void executeExceptionInSynActionHandlerAfterTransition() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test3'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='ExceptionThrowingAfterTransitionActionHandler' />" +
	      "    <transition name='continue2' to='end' />" +
	      "  </node>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test3.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);	    
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
	    
		this.moveAndResume();
	}

	@Test
	public void executeExceptionInAsynActionHandlerAfterTransition() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test4'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <node name='a' async='true'>" +
	      "    <action class='ExceptionThrowingAfterTransitionActionHandler' />" +
	      "    <transition name='continue2' to='end' />" +
	      "  </node>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test4.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);	    
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
		}
		finally
		{
			jbpmContext.close();
		}

		//Now lets start the JobExecutor
		jbpmConfiguration.startJobExecutor();
		Thread.sleep(1000);
		jbpmConfiguration.getJobExecutor().stopAndJoin();
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);				
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
		
		this.moveAndResume();
	}
	
	
	@Test
	public void executeExceptionInSyncNodeEnterActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test5'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "	 <event type='node-enter'>" +
	      "    <action class='ExceptionThrowingActionHandler' />" +
	      "  </event>" +
	      "    <transition name='continue2' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "      <factoryMethodMap>" +
	      "        <entry><key>ActionHandler</key><value>gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler</value></entry>" +
	      "      </factoryMethodMap>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test5.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
	    	    
		this.moveAndResume();
	}

	@Test
	public void executeExceptionInAsyncNodeEnterActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test6'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a' async='true'>" +
	      "	 <event type='node-enter'>" +
	      "    <action class='ExceptionThrowingActionHandler' />" +
	      "  </event>" +
	      "    <transition name='continue2' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test6.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
		}
		finally
		{
			jbpmContext.close();
		}

		//Now lets start the JobExecutor
		jbpmConfiguration.startJobExecutor();
		Thread.sleep(1000);
		jbpmConfiguration.getJobExecutor().stopAndJoin();
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);				
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
				
		this.moveAndResume();
	}
		
	@Test
	public void executeExceptionInSyncNodeLeaveActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test7'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "	 <event type='node-leave'>" +
	      "    <action class='ExceptionThrowingActionHandler' />" +
	      "  </event>" +
	      "    <transition name='continue2' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}

		configuration.addProperty("test7.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);	    
			processInstanceId = processInstance.getId();
			processInstance.signal();
	    
		    assertEquals("a", processInstance.getRootToken().getNode().getName());	    
		    //Leave state a
		    processInstance.signal();
		    
		    assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
				
		this.moveAndResume();
		
	}	

	@Test
	public void executeExceptionInAsyncNodeLeaveActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test8'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <node name='a' async='true'>" +
	      "	 <event type='node-leave'>" +
	      "    <action class='ExceptionThrowingActionHandler' />" +
	      "  </event>" +
	      "    <transition name='continue2' to='end' />" +
	      "  </node>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}
	    
		configuration.addProperty("test8.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);	    
			processInstanceId = processInstance.getId();
			processInstance.signal();
	    		    
		}
		finally
		{
			jbpmContext.close();
		}

				
		//Now lets start the JobExecutor
		jbpmConfiguration.startJobExecutor();
		Thread.sleep(1000);
		jbpmConfiguration.getJobExecutor().stopAndJoin();
				
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);				
			assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}
				
		this.moveAndResume();
		
	}	
	
	
	@Test
	public void executeExceptionInTaskEndActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test9'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <task-node name='a'>" +
	      "    <task name='a task'>" +
	      "      <event type='task-end'>" +
	      "        <action class='ExceptionThrowingActionHandler' />" +
		  "  	 </event>" +	      	      	      	      	               		 
	      "    </task>" +
	      "    <transition name='continue2' to='end'></transition>" +
	      "  </task-node>" +
	      "  <end-state name='end' />" +
	      "  <node name='troubleshoot'>" +
	      "    <action name='troubleshoot' class='MockingActionHandler'>" +
	      "    </action>" +
	      "    <transition name='continue' to='end' />" +
	      "  </node>" +	      
	      "  <exception-handler>" +
	      "      <action class='ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      
	      "</process-definition>");
		String processDefinitionName = processDefinition.getName();
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);						
		}
		finally
		{
			jbpmContext.close();
		}
	    
		configuration.addProperty("test9.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
				
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
		    assertEquals("a", processInstance.getRootToken().getNode().getName());	    
		    //Leave state a
		    Collection taskInstanceCollection = processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken());
		    assertEquals(1, taskInstanceCollection.size());
		    TaskInstance taskInstance = (TaskInstance)taskInstanceCollection.iterator().next();
		    assertEquals("a task", taskInstance.getName());
		    taskInstance.end("continue2");
		    
		    assertTrue(processInstance.isSuspended());
		}
		finally
		{
			jbpmContext.close();
		}	    	    	    
		
		this.moveAndResume();
	}
	
	private void moveAndResume()
	{
	    jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			//Verify that can move and then resume it
			Node troubleshootNode = processInstance.getProcessDefinition().getNode("troubleshoot");
			processInstance.getRootToken().setNode(troubleshootNode);
			assertEquals("troubleshoot", processInstance.getRootToken().getNode().getName());
			processInstance.resume();
			troubleshootNode.enter(new ExecutionContext(processInstance.getRootToken()));
			if (! processInstance.hasEnded())
			{
				assertEquals("end", processInstance.getRootToken().getNode().getName());
			}
			
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
	
	public static ActionHandler createMockActionHandler() throws Exception
	{
		//Setup mock
		final ActionHandler handler = context.mock(ActionHandler.class);
		context.checking(new Expectations() {{
			one(handler).execute(with(any(ExecutionContext.class)));			
		}});
		return handler;
	}
	
}
