package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;

import gov.loc.repository.packagemodeler.packge.FileLocation;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import static gov.loc.repository.constants.Agents.*;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.workflow.BaseHandlerTest;

public class ManualFileCopyActionHandlerTest extends BaseHandlerTest{
	
	//TODO Fix
	@Override
	public void setup() throws Exception {
		//this.dao.createOrFindFileInstance(REPOSITORY_ID, PACKAGE_ID1 + testCounter, RDC, BASEPATH_1, FILENAME_1, FIXITY_1, MD5);
	}
	
	@Test
	public void foo()
	{
		
	}
	
	/*
	//Everything goes according to plan	
	@Test
	public void executeDefault() throws Exception
	{

	    FileLocation fileLocation1 = dao.findFileLocation(REPOSITORY_ID, PACKAGE_ID1 + testCounter, RDC, BASEPATH_1);
	    assertNotNull(fileLocation1);
	    assertEquals(1, fileLocation1.getFileInstanceList().size());
		
		//A simple process definition is used to test the action handler.
		//Note no jbpmContext, so this process definition isn't being persisted.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <task-node name='a'>" +
	      "    <task name='copy task'>" +
	      "      <assignment actor-id='" + PERSON_ID1 + "' />" +
	      "    <event type='task-end'>" +
	      "      <action name='manual copy action' class='gov.loc.repository.workflow.actionhandlers.ManualFileCopyActionHandler'>" +
	      "        <sourcePackageLocationVariable>rdcPackageLocation</sourcePackageLocationVariable>" +
	      "        <sourceStorageServiceVariable>rdcStorageService</sourceStorageServiceVariable>" +	      	      
	      "        <destinationPackageLocationVariable>rs25PackageLocation</destinationPackageLocationVariable>" +
	      "        <destinationStorageServiceVariable>rs25StorageService</destinationStorageServiceVariable>" +	      	      
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
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);
	    processInstance.getContextInstance().setVariable("rdcPackageLocation", BASEPATH_1);
	    processInstance.getContextInstance().setVariable("rdcStorageService", RDC);
	    processInstance.getContextInstance().setVariable("rs25PackageLocation", BASEPATH_2);
	    processInstance.getContextInstance().setVariable("rs25StorageService", RS25);
	    
	    //Gets out of start state
	    processInstance.signal();

	    //Gets out of a task
	    TaskInstance taskInstance =(TaskInstance)processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken()).iterator().next();
	    assertEquals("copy task", taskInstance.getTask().getName());
	    taskInstance.setActorId(PERSON_ID1);
	    taskInstance.start();
	    taskInstance.end("continue");
	    	    	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());

	    this.commitAndRestartTransaction();
	    
	    Package packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    assertNotNull(packge);
	    assertEquals(1, packge.getEventSet().size());
	    FileLocation fileLocation2 = dao.findFileLocation(REPOSITORY_ID, PACKAGE_ID1 + testCounter, RS25, BASEPATH_2);
	    assertNotNull(fileLocation2);
	    assertEquals(1, fileLocation2.getFileInstanceList().size());
	}
	*/
	
}
