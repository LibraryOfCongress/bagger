package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.packagemodeler.packge.ExternalFileLocation;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.workflow.AbstractCoreHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;

public class AddExternalFileLocationActionHandlerTest extends AbstractCoreHandlerTest {

	static Repository repository;
	
	@Override
	public void createFixtures() throws Exception {
		repository = this.fixtureHelper.createRepository(REPOSITORY_ID);
	}
		
	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{

		Package packge = this.modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		this.template.save(packge);
				
		String processDefinitionString = 
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='b' />" +
	      "    <event type='node-leave'>" +
	      "      <action name='add external file location' class='AddExternalFileLocationActionHandler'>" +
	      "        <externalIdentifierValue>" + SERIAL_NUMBER_1 + "</externalIdentifierValue>" +
	      "        <externalIdentifierType>" + IdentifierType.SERIAL_NUMBER.toString() + "</externalIdentifierType>" +	   
	      "        <mediaType>" + MediaType.EXTERNAL_HARDDRIVE.toString() + "</mediaType>" +
	      "        <packageKey>" + packge.getKey() + "</packageKey>" +
	      "        <keyVariable>externalFileLocationKey</keyVariable>" +
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      	      
	      "  </start-state>" +
	      "  <end-state name='b' />" +
	      "</process-definition>";
				
		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		
		jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
		    ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			//Gets out of start state
		    processInstance.signal();	    

			
			TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			this.template.refresh(packge);

			assertEquals("b", processInstance.getRootToken().getNode().getName());	    	    
		    ExternalFileLocation fileLocation = packge.getFileLocation(new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER));
		    assertNotNull(fileLocation);
		    assertEquals(MediaType.EXTERNAL_HARDDRIVE, fileLocation.getMediaType());
		    assertEquals("/", fileLocation.getBasePath());
		    assertFalse(fileLocation.isManaged());
		    assertFalse(fileLocation.isLCPackageStructure());
		    assertEquals(fileLocation.getKey().toString(), (String)processInstance.getContextInstance().getVariable("externalFileLocationKey"));
		    
		    txManager.commit(status);
		}
		finally
		{
			jbpmContext.close();
		}	    
		    
	}

	
}
