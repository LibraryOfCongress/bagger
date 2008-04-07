package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.events.Event;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.workflow.BaseHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import static gov.loc.repository.workflow.WorkflowConstants.*;

public class AddPackageEventActionHandlerTest extends BaseHandlerTest{
	
	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		//A simple process definition is used to test the action handler.
		//Note no jbpmContext, so this process definition isn't being persisted.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <task-node name='a'>" +
	      "    <task name='a task'>" +
	      "      <assignment actor-id='" + PERSON_ID1 + "' />" +
	      "    <event type='task-end'>" +
	      "      <action name='add generic event action' class='AddPackageEventActionHandler'>" +
	      "        <eventClassName>PackageReceivedEvent</eventClassName>" +
	      "        <packageKey>${packageKey}</packageKey>" +
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      
	      "    </task>" +	      
	      "    <transition name='continue' to='b' />" +
	      "    <transition name='troubleshoot' to='c' />" +	      	      
	      "  </task-node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    Package packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    processInstance.getContextInstance().setVariable(VARIABLE_PACKAGEKEY, packge.getKey().toString());
	    
	    this.commitAndRestartTransaction();
	    
	    //Gets out of start state
	    processInstance.signal();

	    //Gets out of a task
	    TaskInstance taskInstance =(TaskInstance)processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken()).iterator().next();
	    assertEquals("a task", taskInstance.getTask().getName());
	    taskInstance.setActorId(PERSON_ID1);
	    taskInstance.start();
	    taskInstance.end("continue");
	    	    	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());

	    this.commitAndRestartTransaction();
	    
	    packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    assertNotNull(packge);
	    assertEquals(1, packge.getPackageEvents().size());
	    Event event = packge.getPackageEvents().iterator().next();
	    assertEquals(taskInstance.getStart().getTime(), event.getEventStart().getTime());
	    assertEquals(taskInstance.getEnd().getTime(), event.getEventEnd().getTime());
	    assertEquals(PERSON_ID1, event.getPerformingAgent().getId());
	    assertTrue(event.isSuccess());
	}

	
}
