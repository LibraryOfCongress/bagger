package gov.loc.repository.workflow.processdefinitions;

import static org.junit.Assert.*;

import java.util.List;

import gov.loc.repository.workflow.AbstractCoreHandlerTest;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/workflow-core-context.xml"})
public class ResumeTest extends AbstractCoreHandlerTest	
{
	static String tokenInstanceId;

	@Override
	public void setup() throws Exception {
		String identitiesString =
			"<identity>" +
			"  <user name='u' />" +
			"</identity>";
		this.loadIdentities(identitiesString);
	}
	
	/*
	@Before
	public void loadIdentities() throws Exception
	{
		jbpmConfiguration= JbpmConfiguration.getInstance();
		String identities =
			"<identity>" +
			"  <user name='u' />" +
			"</identity>";
		
		InputStream stream = new ByteArrayInputStream(identities.getBytes());
		Entity[] entities = IdentityXmlParser.parseEntitiesResource(stream);

		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
	    try {
	      Session session = jbpmContext.getSession();
	      for (int i=0; i<entities.length; i++) {
	        session.save(entities[i]);
	      }
	    } finally {
	      jbpmContext.close();
	    }		
		
	}	
	@Before
	public void setup() throws Exception
	{
		configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
	*/
	
	@SuppressWarnings("unchecked")
	@Test
	public void testMoveToNode() throws Exception
	{
		//Shows that moving a suspended token works OK.
		String processDefinitionString = 
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
	      "</process-definition>";

		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
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
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			
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
		String processDefinitionString =
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
	      "</process-definition>";

		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);

		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
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
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			
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
		String processDefinitionString = 
		  "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition name='continue1' to='a' />" +
	      "  </start-state>" +
	      "  <state name='a'>" +
	      "    <transition name='continue' to='end' />" +
	      "  </state>" +
	      "  <end-state name='end' />" +
	      "</process-definition>";

		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
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
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			
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
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
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
