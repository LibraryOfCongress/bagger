package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.events.filelocation.VerifyEvent;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.BaseHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;

public class AddExternalFileLocationEventActionHandlerTest extends BaseHandlerTest {

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
	      "      <action name='add verify event action' class='gov.loc.repository.workflow.actionhandlers.AddExternalFileLocationEventActionHandler'>" +
	      "        <eventClassName>gov.loc.repository.packagemodeler.events.filelocation.VerifyEvent</eventClassName>" +	      
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      
	      "    </task>" +	      
	      "    <transition name='continue' to='b' />" +
	      "    <transition name='troubleshoot' to='c' />" +	      	      
	      "  </task-node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");

		//Create the FileLocation
	    Package packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    this.factory.createExternalFileLocation(packge, MediaType.EXTERNAL_HARDDRIVE, new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER), "/", false, false);
	    this.dao.save(packge);
	    this.commitAndRestartTransaction();
		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);
	    processInstance.getContextInstance().setVariable("externalIdentifierValue", SERIAL_NUMBER_1);
	    processInstance.getContextInstance().setVariable("externalIdentifierType", IdentifierType.SERIAL_NUMBER.toString());
	    
	    //Gets out of start state
	    processInstance.signal();

	    //Gets out of a task
	    TaskInstance taskInstance =(TaskInstance)processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken()).iterator().next();
	    assertEquals("a task", taskInstance.getTask().getName());
	    taskInstance.setActorId(PERSON_ID1);
	    taskInstance.start();
	    taskInstance.end("continue");

	    this.commitAndRestartTransaction();
	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());	    
	    packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    assertNotNull(packge);
	    FileLocation fileLocation = packge.getFileLocation(new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER));
	    assertNotNull(fileLocation);
	    assertEquals(1, fileLocation.getFileLocationEvents().size());
	    VerifyEvent event = (VerifyEvent)fileLocation.getFileLocationEvents().iterator().next();
	    assertEquals(taskInstance.getStart().getTime(), event.getEventStart().getTime());
	    assertEquals(taskInstance.getEnd().getTime(), event.getEventEnd().getTime());
	    assertEquals(PERSON_ID1, event.getPerformingAgent().getId());
	    assertTrue(event.isSuccess());
	}

	
}
