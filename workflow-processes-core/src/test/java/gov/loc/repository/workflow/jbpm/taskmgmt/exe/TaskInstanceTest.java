package gov.loc.repository.workflow.jbpm.taskmgmt.exe;

import static org.junit.Assert.*;
import gov.loc.repository.workflow.processdefinitions.AbstractProcessDefinitionTest;
import gov.loc.repository.workflow.processdefinitions.ProcessDefinitionHelper;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import org.junit.Test;

public class TaskInstanceTest extends AbstractProcessDefinitionTest {
	private String processDefinitionName;
	//Everything goes according to plan
	@Test
	public void testTransition() throws Exception
	{
		ProcessDefinitionHelper helper = new ProcessDefinitionHelper();
		helper.setProcessDefinition(
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <task-node name='a'>" +
	      "    <task name='a task'>" +
	      "      <assignment actor-id='ray' />" +
	      "      <event type='task-end'>" +
	      "        <action name='test' class='gov.loc.repository.workflow.jbpm.taskmgmt.exe.TaskActionTestActionHandler' />" +
	      "      </event>" +	      
	      "    </task>" +
	      "    <transition name='continue' to='b' />" +
	      "    <transition name='troubleshoot' to='c' />" +	      	      
	      "  </task-node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");

		processDefinitionName = helper.deploy();
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{			
			//The Fedex guy hands Ray the drive, so he starts a process
			//Have to make Ray the authenticated user			
			jbpmContext.setActorId("ray");
			assertEquals("ray", jbpmContext.getActorId());
			
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionName);
	    
		    //Gets out of start state
		    processInstance.signal();
	
		    //Gets out of a task
		    TaskInstance taskInstance =(TaskInstance)processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken()).iterator().next();
		    assertEquals("a task", taskInstance.getTask().getName());
		    taskInstance.getContextInstance().setVariable("foo", "bar");
		    //taskInstance.start();
		    //assertFalse(processInstance.getContextInstance().hasVariable("foo"));	    
		    taskInstance.end("continue");
		    assertEquals("continue", processInstance.getContextInstance().getTransientVariable("transition"));
		    assertTrue(processInstance.getContextInstance().hasVariable("foo"));
		}
		finally
		{
			jbpmContext.close();
		}
	}
	
}
