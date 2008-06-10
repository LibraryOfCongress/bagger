package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.constants.Roles;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.StorageSystemFileLocation;
import gov.loc.repository.workflow.AbstractCoreHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import static gov.loc.repository.constants.Agents.*;

public class AddStorageSystemFileLocationActionHandlerTest extends AbstractCoreHandlerTest {

	static Repository repository;
	
	@Override
	public void createFixtures() throws Exception {
		repository = this.fixtureHelper.createRepository(REPOSITORY_ID);
		this.fixtureHelper.createSystem(RDC, new Role[] { fixtureHelper.createRole(Roles.STORAGE_SYSTEM)});
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
	      "      <action name='add staging file location' class='AddStorageSystemFileLocationActionHandler'>" +
	      "        <basePath>" + BASEPATH_1 + "</basePath>" +
	      "        <storageSystemId>" + RDC + "</storageSystemId>" +
	      "        <packageKey>" + packge.getKey() + "</packageKey>" +
	      "        <keyVariable>stagingFileLocationKey</keyVariable>" +
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
	
		    assertEquals("b", processInstance.getRootToken().getNode().getName());
		    
		    TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			this.template.refresh(packge);
	
		    StorageSystemFileLocation fileLocation = packge.getFileLocation(RDC, BASEPATH_1);
		    assertNotNull(fileLocation);
		    assertTrue(fileLocation.isManaged());
		    assertTrue(fileLocation.isBag());
		    assertEquals(fileLocation.getKey().toString(), (String)processInstance.getContextInstance().getVariable("stagingFileLocationKey"));
		    
		    txManager.commit(status);
		}
		finally
		{
			jbpmContext.close();
		}
	}

	
}
