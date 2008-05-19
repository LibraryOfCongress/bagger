package gov.loc.repository.workflow.processdefinitions;

import static org.junit.Assert.*;

import java.util.List;

import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;

import org.apache.commons.configuration.Configuration;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Before;
import org.junit.Test;

public class ResumeTest extends AbstractProcessDefinitionTest	
{
	static String tokenInstanceId;
	private long processInstanceId;
	private JbpmContext jbpmContext;
	Configuration configuration;
	
	private static final String IDENTITIES =
		"<identity>" +
		"  <user name='u' />" +
		"</identity>";
	
	@Override
	public void createFixtures() throws Exception {		
		//Load identities to be used by the process definition
		loadIdentities(IDENTITIES);
		
	}		
	
	@Before
	public void setup() throws Exception
	{
		configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
		
	@SuppressWarnings("unchecked")
	@Test
	public void testMoveToNode() throws Exception
	{
		//Shows that moving a suspended token works OK.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a' />" +
	      "  <node name='b'>" +
	      "    <action class='RecordingActionHandler'>" +
	      "      <id>mainhandler</id>" +
	      "      <transition>continue2</transition>" +
	      "    </action>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "    <event type='node-leave'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeleavehandler</id>" +
	      "      </action>" +	      
	      "    </event>" +
	      "    <transition name='continue2' to='end' />" +
	      "  </node>" +
	      "  <end-state name='end' />" +
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

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
			assertEquals("a", processInstance.getRootToken().getNode().getName());
			
		}
		finally
		{
			jbpmContext.close();
		}

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(this.processInstanceId);
			
			//Let's suspend, move, and resume
			processInstance.suspend();
			assertTrue(processInstance.isSuspended());
			
			Node resumeNode = processInstance.getProcessDefinition().getNode("b");
			processInstance.getRootToken().setNode(resumeNode);
			assertEquals("b", processInstance.getRootToken().getNode().getName());
			processInstance.resume();
			resumeNode.enter(new ExecutionContext(processInstance.getRootToken()));
			
			assertTrue(processInstance.hasEnded());
			List<String> idList = (List<String>)processInstance.getContextInstance().getVariable("idList");
			assertTrue(idList.contains("mainhandler"));
			assertTrue(idList.contains("nodeenterhandler"));
			assertTrue(idList.contains("nodeleavehandler"));
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMoveToTaskNode() throws Exception
	{
		//Shows that a task is re-done when a token is moved to it and resumed.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +	      
	      "  <task-node name='a'>" +
	      "    <task name='taska'> +" +
	      "      <assignment expression='user(u)' />" +
	      "      <event type='task-end'>" +
	      "        <action class='RecordingActionHandler'>" +
	      "          <id>taskendhandler</id>" +
	      "        </action>" +	      
	      "      </event>" +	      
	      "    </task>" +
	      "    <event type='node-enter'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeenterhandler</id>" +
	      "      </action>" +	      
	      "    </event>" +	      
	      "    <event type='node-leave'>" +
	      "      <action class='RecordingActionHandler'>" +
	      "        <id>nodeleavehandler</id>" +
	      "      </action>" +	      
	      "    </event>" +
	      "    <transition name='continue' to='b' />" +
	      "  </task-node>" +
	      "  <state name='b'>" +
	      "    <transition name='continue' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
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

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
			assertEquals("a", processInstance.getRootToken().getNode().getName());
			
			//User u should now have task on task list
			List<TaskInstance> taskInstanceList = jbpmContext.getTaskList("u");
			assertEquals(1, taskInstanceList.size());
			
			//Complete the task
			TaskInstance taskInstance = taskInstanceList.get(0);
			taskInstance.end("continue");
			
			assertEquals("b", processInstance.getRootToken().getNode().getName());
			
			List<String> idList = (List<String>)processInstance.getContextInstance().getVariable("idList");
			assertTrue(idList.contains("nodeenterhandler"));
			assertTrue(idList.contains("nodeleavehandler"));
			assertTrue(idList.contains("taskendhandler"));

			processInstance.getContextInstance().deleteVariable("idList");
			assertNull(processInstance.getContextInstance().getVariable("idList"));
			
		}
		finally
		{
			jbpmContext.close();
		}

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(this.processInstanceId);
			
			List<TaskInstance> taskInstanceList = jbpmContext.getTaskList("u");
			assertTrue(taskInstanceList.isEmpty());
			
			//Let's suspend, move, and resume
			processInstance.suspend();
			assertTrue(processInstance.isSuspended());
			
			Node resumeNode = processInstance.getProcessDefinition().getNode("a");
			processInstance.getRootToken().setNode(resumeNode);
			assertEquals("a", processInstance.getRootToken().getNode().getName());
			processInstance.resume();
			resumeNode.enter(new ExecutionContext(processInstance.getRootToken()));
			
			taskInstanceList = jbpmContext.getTaskList("u");
			assertEquals(1, taskInstanceList.size());
			
			//Complete the task
			TaskInstance taskInstance = taskInstanceList.get(0);
			taskInstance.end("continue");
			
			assertEquals("b", processInstance.getRootToken().getNode().getName());

			List<String> idList = (List<String>)processInstance.getContextInstance().getVariable("idList");
			assertTrue(idList.contains("nodeenterhandler"));
			assertTrue(idList.contains("nodeleavehandler"));
			assertTrue(idList.contains("taskendhandler"));
		}
		finally
		{
			jbpmContext.close();
		}
		
	}
	
	@SuppressWarnings("unchecked")
	//@Test
	public void testRestartEnded() throws Exception
	{
		//Shows that trying to restart a process instance is a bad idea
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
		  "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "    <transition name='continue' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
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

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
			this.processInstanceId = processInstance.getId();
			processInstance.signal();
	    
			assertEquals("a", processInstance.getRootToken().getNode().getName());
			
			processInstance.signal("continue");
			
			assertTrue(processInstance.hasEnded());
			
		}
		finally
		{
			jbpmContext.close();
		}

		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(this.processInstanceId);
			
			//Let's suspend, move, and resume
			processInstance.suspend();
			assertTrue(processInstance.isSuspended());
			
			Node resumeNode = processInstance.getProcessDefinition().getNode("a");
			processInstance.getRootToken().setNode(resumeNode);
			assertEquals("a", processInstance.getRootToken().getNode().getName());
			processInstance.setEnd(null);
			processInstance.resume();
			resumeNode.enter(new ExecutionContext(processInstance.getRootToken()));
						
			assertFalse(processInstance.hasEnded());
		}
		finally
		{
			jbpmContext.close();
		}
			
		jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(this.processInstanceId);
			processInstance.signal("continue");
			
			//This fails!
			//Now the processInstance won't end!
			assertTrue(processInstance.hasEnded());
			
		}
		finally
		{
			jbpmContext.close();
		}
		
	}

}
