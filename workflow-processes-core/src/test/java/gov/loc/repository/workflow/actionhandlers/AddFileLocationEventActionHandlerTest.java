package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.packagemodeler.events.filelocation.VerifyAgainstManifestEvent;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.AbstractCoreHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;

public class AddFileLocationEventActionHandlerTest extends AbstractCoreHandlerTest {

	static Repository repository;
	
	@Override
	public void createFixtures() throws Exception {
		repository = this.fixtureHelper.createRepository(REPOSITORY_ID);
		this.fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);
	}
		
	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		//Create the FileLocation
		Package packge = this.modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
	    FileLocation fileLocation = modelerFactory.createExternalFileLocation(packge, MediaType.EXTERNAL_HARDDRIVE, new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER), "/", false, false);
		this.template.save(packge);

		
		//A simple process definition is used to test the action handler.
		//Note no jbpmContext, so this process definition isn't being persisted.
		String processDefinitionString =
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <task-node name='a'>" +
	      "    <task name='a task'>" +
	      "      <assignment actor-id='" + PERSON_ID1 + "' />" +
	      "    <event type='task-end'>" +
	      "      <action name='add verify event action' class='AddFileLocationEventActionHandler'>" +
	      "        <eventClassName>VerifyAgainstManifestEvent</eventClassName>" +
	      "        <fileLocationKey>" + fileLocation.getKey() + "</fileLocationKey>" +
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      
	      "    </task>" +	      
	      "    <transition name='continue' to='b' />" +
	      "    <transition name='troubleshoot' to='c' />" +	      	      
	      "  </task-node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>";
		
		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		
		jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
		    //Gets out of start state
		    processInstance.signal();
	
		    //Gets out of a task
		    TaskInstance taskInstance =(TaskInstance)processInstance.getTaskMgmtInstance().getUnfinishedTasks(processInstance.getRootToken()).iterator().next();
		    assertEquals("a task", taskInstance.getTask().getName());
		    taskInstance.setActorId(PERSON_ID1);
		    taskInstance.start();
		    taskInstance.end("continue");
		    
		    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			this.template.refresh(packge);
			
		    assertEquals("b", processInstance.getRootToken().getNode().getName());	    
		    fileLocation = packge.getFileLocation(new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER));
		    assertNotNull(fileLocation);
		    assertEquals(1, fileLocation.getFileLocationEvents().size());
		    VerifyAgainstManifestEvent event = (VerifyAgainstManifestEvent)fileLocation.getFileLocationEvents().iterator().next();
		    assertEquals(taskInstance.getStart().getTime(), event.getEventStart().getTime());
		    assertEquals(taskInstance.getEnd().getTime(), event.getEventEnd().getTime());
		    assertEquals(PERSON_ID1, event.getPerformingAgent().getId());
		    assertTrue(event.isSuccess());
		    
		    txManager.commit(status);
		}
		finally
		{
			jbpmContext.close();
		}
	    
	    
	}

	
}
