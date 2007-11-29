package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.BaseHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;

public class AddExternalFileLocationActionHandlerTest extends BaseHandlerTest {

	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		//A simple process definition is used to test the action handler.
		//Note no jbpmContext, so this process definition isn't being persisted.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='b' />" +
	      "    <event type='node-leave'>" +
	      "      <action name='add external file location' class='gov.loc.repository.workflow.actionhandlers.AddExternalFileLocationActionHandler'>" +
	      "        <mediaType>" + MediaType.EXTERNAL_HARDDRIVE.toString() + "</mediaType>" +	      	      
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      	      
	      "  </start-state>" +
	      "  <end-state name='b' />" +
	      "</process-definition>");
		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);
	    processInstance.getContextInstance().setVariable("externalIdentifierValue", SERIAL_NUMBER_1);
	    processInstance.getContextInstance().setVariable("externalIdentifierType", IdentifierType.SERIAL_NUMBER.toString());
	    
	    this.commitAndRestartTransaction();
	    
	    //Gets out of start state
	    processInstance.signal();	    
	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());	    
	    Package packge = dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    assertNotNull(packge);
	    ExternalFileLocation fileLocation = packge.getFileLocation(new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER));
	    assertNotNull(fileLocation);
	    assertEquals(MediaType.EXTERNAL_HARDDRIVE, fileLocation.getMediaType());
	    assertEquals("/", fileLocation.getBasePath());
	    assertFalse(fileLocation.isManaged());
	    assertFalse(fileLocation.isLCPackageStructure());
	}

	
}
