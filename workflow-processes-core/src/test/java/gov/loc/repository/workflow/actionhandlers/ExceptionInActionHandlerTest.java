package gov.loc.repository.workflow.actionhandlers;

import static org.junit.Assert.*;

import java.util.Collection;

import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;
import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;
import static gov.loc.repository.workflow.WorkflowConstants.*;

import org.apache.commons.configuration.Configuration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Before;
import org.junit.Test;

public class ExceptionInActionHandlerTest extends AbstractProcessDefinitionTest
{
	static String tokenInstanceId;
	private JbpmContext jbpmContext;
	Configuration configuration;
	
	@Before
	public void setup() throws Exception
	{
		configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
	
	@Test
	public void executeExceptionInMainActionHandler() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action name='a-action' class='ExceptionThrowingActionHandler' />" +
	      "    <transition name='continue' to='b' />" +
	      "  </node>" +
	      "  <state name='b'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "  </state>" +
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

		configuration.addProperty("test.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			try
			{
				processInstance.signal();
			}
			catch(Exception ex)
			{
				//Never got to node b
				assertNull(processInstance.getContextInstance().getVariable("idList"));
				
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION));
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_DETAIL));
				assertEquals("a-action", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME));
				assertEquals("a", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_NODENAME));
				assertFalse(processInstance.isSuspended());
				assertTrue(processInstance.getRootToken().isSuspended());
			}
				
		}
		finally
		{
			jbpmContext.close();
		}
	}
			
	@Test
	public void executeExceptionInNodeEnterActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "	   <event type='node-enter'>" +
	      "      <action name='a-nodeenteraction' class='ExceptionThrowingActionHandler' />" +
	      "    </event>" +
	      "    <transition name='continue' to='b' />" +
	      "  </state>" +
	      "  <state name='b'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "  </state>" +
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

		configuration.addProperty("test.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			try
			{
				processInstance.signal();
			}
			catch(Exception ex)
			{
				//Never got to node b
				assertNull(processInstance.getContextInstance().getVariable("idList"));
				
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION));
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_DETAIL));
				assertEquals("a-nodeenteraction", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME));
				assertEquals("a", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_NODENAME));
				assertFalse(processInstance.isSuspended());
				assertTrue(processInstance.getRootToken().isSuspended());
			}
				
		}
		finally
		{
			jbpmContext.close();
		}	    	    
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void executeExceptionInNodeLeaveActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "	   <event type='node-leave'>" +
	      "      <action name='a-nodeleaveaction' class='ExceptionThrowingActionHandler' />" +
	      "    </event>" +
	      "    <transition name='continue' to='b' />" +
	      "  </state>" +
	      "  <state name='b'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "  </state>" +
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

		configuration.addProperty("test.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			try
			{
				processInstance.signal();
			}
			catch(Exception ex)
			{
				//Never got to node b
				assertNull(processInstance.getContextInstance().getVariable("idList"));
				
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION));
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_DETAIL));
				assertEquals("a-nodeleaveaction", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME));
				assertEquals("a", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_NODENAME));
				assertFalse(processInstance.isSuspended());
				assertTrue(processInstance.getRootToken().isSuspended());
			}
				
		}
		finally
		{
			jbpmContext.close();
		}	    	    
	}

	@Test
	@SuppressWarnings("unchecked")
	public void executeExceptionInTaskEndActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <task-node name='a'>" +
	      "    <task name='a task'>" +
	      "      <event type='task-end'>" +
	      "        <action name='a-taskendaction' class='ExceptionThrowingActionHandler' />" +
		  "  	 </event>" +	      	      	      	      	               		 
	      "    </task>" +
	      "    <transition name='continue' to='b' />" +
	      "  </task-node>" +
	      "  <state name='b'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "  </state>" +
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

		configuration.addProperty("test.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			try
			{
				processInstance.signal();
				
			    assertEquals("a", processInstance.getRootToken().getNode().getName());	    
			    //Leave state a
			    Collection taskInstanceCollection = processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken());
			    assertEquals(1, taskInstanceCollection.size());
			    TaskInstance taskInstance = (TaskInstance)taskInstanceCollection.iterator().next();
			    assertEquals("a task", taskInstance.getName());
			    taskInstance.end("continue");
				
			}
			catch(Exception ex)
			{
				//Never got to node b
				assertNull(processInstance.getContextInstance().getVariable("idList"));
				
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION));
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_DETAIL));
				assertEquals("a-taskendaction", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME));
				assertEquals("a", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_NODENAME));
				assertFalse(processInstance.isSuspended());
				assertTrue(processInstance.getRootToken().isSuspended());
			}
				
		}
		finally
		{
			jbpmContext.close();
		}	    	    
	}
	
	@Test
	@SuppressWarnings("unchecked")
	public void executeExceptionInTransitionActionHandler() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "    <transition name='continue' to='b'>" +
	      "      <action name='continue-transitionaction' class='ExceptionThrowingActionHandler' />" +
	      "    </transition>" +
	      "  </state>" +
	      "  <state name='b'>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "  </state>" +
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

		configuration.addProperty("test.troubleshoot.ActionHandler.factorymethod", "gov.loc.repository.workflow.actionhandlers.ExceptionActionHandlerTest.createMockActionHandler");
		
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			try
			{
				processInstance.signal();
				processInstance.signal("continue");
			}
			catch(Exception ex)
			{
				//Never got to node b
				assertNull(processInstance.getContextInstance().getVariable("idList"));
				
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION));
				assertNotNull(processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_DETAIL));
				assertEquals("continue-transitionaction", processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME));
				assertEquals(null, processInstance.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_NODENAME));
				assertFalse(processInstance.isSuspended());
				assertTrue(processInstance.getRootToken().isSuspended());
			}
				
		}
		finally
		{
			jbpmContext.close();
		}	    	    
	}
	
}
