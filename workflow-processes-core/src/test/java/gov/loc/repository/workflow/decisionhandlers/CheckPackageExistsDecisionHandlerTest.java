package gov.loc.repository.workflow.decisionhandlers;

import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import static org.junit.Assert.*;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.workflow.AbstractCoreHandlerTest;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.junit.Test;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;

public class CheckPackageExistsDecisionHandlerTest extends AbstractCoreHandlerTest {

	static Repository repository;
	Package packge;
	
	@Override
	public void createFixtures() throws Exception {
		repository = this.fixtureHelper.createRepository(REPOSITORY_ID);
	}	
	
	@Test
	public void testDecideDoesNotExist() throws Exception {
		String processDefinitionString =
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <decision name='a'>" +
	   	  "    <handler class='gov.loc.repository.workflow.decisionhandlers.CheckPackageExistsDecisionHandler' config-type='constructor'>" +
	   	  "      <repositoryId>" + REPOSITORY_ID + "</repositoryId>" +
	   	  "      <packageId>" + PACKAGE_ID1 + testCounter + "</packageId>" +
	   	  "    </handler>" +	   	  
	      "      <transition name='continue' to='b' />" +
	      "      <transition name='retry' to='c' />" +
	      "  </decision>" +   
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>";

		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
	    assertNull(this.dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter));
		txManager.commit(status);
	    
		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
		    
		    //Gets out of start state
		    processInstance.signal();
		    
		    assertEquals("b", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}
	}

	@Test
	public void testDecideExists() throws Exception {

		Package packge = this.modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		this.template.save(packge);
		
		String processDefinitionString = 
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <decision name='a'>" +
	   	  "    <handler class='gov.loc.repository.workflow.decisionhandlers.CheckPackageExistsDecisionHandler' config-type='constructor'>" +
	   	  "      <repositoryId>" + REPOSITORY_ID + "</repositoryId>" +
	   	  "      <packageId>" + PACKAGE_ID1 + testCounter + "</packageId>" +
	   	  "    </handler>" +	   	  	   	  
	      "      <transition name='continue' to='b' />" +
	      "      <transition name='retry' to='c' />" +
	      "  </decision>" +   
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>";

		TransactionStatus status = txManager.getTransaction(new DefaultTransactionDefinition());
	    assertNotNull(this.dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter));
	    txManager.commit(status);
	    
	    Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			    
			//Gets out of start state
			processInstance.signal();
	    	    
			assertEquals("c", processInstance.getRootToken().getNode().getName());
		}
		finally
		{
			jbpmContext.close();
		}
	}
	
	@Test
	public void testDecideExistWithSameProcessInstanceId() throws Exception {
		String processDefinitionString =
	      "<process-definition name='test'>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <decision name='a'>" +
	   	  "    <handler class='gov.loc.repository.workflow.decisionhandlers.CheckPackageExistsDecisionHandler' config-type='constructor'>" +
	   	  "      <repositoryId>" + REPOSITORY_ID + "</repositoryId>" +
	   	  "      <packageId>" + PACKAGE_ID1 + testCounter + "</packageId>" +
	   	  "    </handler>" +	   	  	   	  
	      "      <transition name='continue' to='b' />" +
	      "      <transition name='retry' to='c' />" +
	      "  </decision>" +   
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>";

		Long processInstanceId = this.deployAndCreateProcessInstance(processDefinitionString);
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
			    		
		    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
		    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);
	
		    Package packge = this.modelerFactory.createPackage(Package.class, repository, PACKAGE_ID1 + testCounter);
		    packge.setProcessInstanceId(processInstance.getId());
		    this.template.save(packge);
		    	    	    
		    //Gets out of start state
		    processInstance.signal();
		    
		    assertEquals("b", processInstance.getRootToken().getNode().getName());
	    
		}
		finally
		{
			jbpmContext.close();
		}
	}
	
}
