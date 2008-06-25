package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

import gov.loc.repository.fixity.FixityAlgorithm;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Fixity;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.packagemodeler.packge.ExternalFileLocation.MediaType;
import gov.loc.repository.packagemodeler.packge.ExternalIdentifier.IdentifierType;
import gov.loc.repository.packagemodeler.packge.impl.PackageImpl;
import gov.loc.repository.workflow.AbstractCoreHandlerTest;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;


public class AddCanonicalFilesActionHandlerTest extends AbstractCoreHandlerTest {

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
		FileLocation fileLocation = modelerFactory.createExternalFileLocation(packge, MediaType.EXTERNAL_HARDDRIVE, new ExternalIdentifier(SERIAL_NUMBER_1, IdentifierType.SERIAL_NUMBER), "/", false, false);
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_1));
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_2), new Fixity(FIXITY_1, FixityAlgorithm.MD5));
		modelerFactory.createFileInstance(fileLocation, new FileName(FILENAME_3), new Fixity(FIXITY_2, FixityAlgorithm.MD5));
		this.template.save(packge);
				
		String processDefinitionString = 
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='b' />" +
	      "    <event type='node-leave'>" +
	      "      <action name='add canonical files' class='AddCanonicalFilesActionHandler'>" +
	      "        <packageKey>" + packge.getKey() + "</packageKey>" +
	      "        <fileLocationKey>" + fileLocation.getKey() + "</fileLocationKey>" +
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      	      
	      "  </start-state>" +
	      "  <state name='b'>" +
	      "    <transition name='continue' to='c' />" +
	      "    <event type='node-leave'>" +
	      "      <action name='add canonical files' class='AddCanonicalFilesActionHandler'>" +
	      "        <packageKey>" + packge.getKey() + "</packageKey>" +
	      "        <fileLocationKey>" + fileLocation.getKey() + "</fileLocationKey>" +
	      "      </action>" +
	      "    </event>" +	      	      	      	      	      	      
	      "  </state>" +	      
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
			
			TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			this.template.refresh(fileLocation);
			
			packge = (Package)this.template.load(PackageImpl.class, packge.getKey());

			assertEquals(3, fileLocation.getFileInstances().size());
			assertEquals("b", processInstance.getRootToken().getNode().getName());	    	    
			assertEquals(2, packge.getCanonicalFiles().size());
		    
		    txManager.commit(status);
		}
		finally
		{
			jbpmContext.close();
		}	    

		jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			//Make sure that can be repeated			
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);

			//Gets out of b
		    processInstance.signal("continue");	    
			
		    
			TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
			this.template.refresh(fileLocation);
			
			packge = (Package)this.template.load(PackageImpl.class, packge.getKey());

			assertEquals(3, fileLocation.getFileInstances().size());
			assertEquals("c", processInstance.getRootToken().getNode().getName());	    	    
			assertEquals(2, packge.getCanonicalFiles().size());
		    
		    txManager.commit(status);
		}
		finally
		{
			jbpmContext.close();
		}	    
		
		
	}

	
}
