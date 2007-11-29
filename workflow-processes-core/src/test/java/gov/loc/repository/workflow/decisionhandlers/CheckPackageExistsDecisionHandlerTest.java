package gov.loc.repository.workflow.decisionhandlers;

import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import static org.junit.Assert.*;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.workflow.BaseHandlerTest;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.junit.Test;

public class CheckPackageExistsDecisionHandlerTest extends BaseHandlerTest {

	@Test
	public void testDecideDoesNotExist() throws Exception {
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <decision name='a'>" +
	   	  "    <handler class='gov.loc.repository.workflow.decisionhandlers.CheckPackageExistsDecisionHandler' />" +
	      "      <transition name='continue' to='b' />" +
	      "      <transition name='retry' to='c' />" +
	      "  </decision>" +   
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");

	    assertNull(this.dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter + "x"));
		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter + "x");
	    
	    this.commitAndRestartTransaction();
	    
	    //Gets out of start state
	    processInstance.signal();
	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());
	}

	@Test
	public void testDecideExists() throws Exception {
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <decision name='a'>" +
	   	  "    <handler class='gov.loc.repository.workflow.decisionhandlers.CheckPackageExistsDecisionHandler' />" +
	      "      <transition name='continue' to='b' />" +
	      "      <transition name='retry' to='c' />" +
	      "  </decision>" +   
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");

	    assertNotNull(this.dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter));

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);
	    
	    this.commitAndRestartTransaction();
	    
	    //Gets out of start state
	    processInstance.signal();
	    	    
	    assertEquals("c", processInstance.getRootToken().getNode().getName());
	}
	
	@Test
	public void testDecideExistWithSameProcessInstanceId() throws Exception {
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <decision name='a'>" +
	   	  "    <handler class='gov.loc.repository.workflow.decisionhandlers.CheckPackageExistsDecisionHandler' />" +
	      "      <transition name='continue' to='b' />" +
	      "      <transition name='retry' to='c' />" +
	      "  </decision>" +   
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");

		
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("repositoryId", REPOSITORY_ID);
	    processInstance.getContextInstance().setVariable("packageId", PACKAGE_ID1 + testCounter);

		Package packge = this.dao.findPackage(Package.class, REPOSITORY_ID, PACKAGE_ID1 + testCounter);
	    assertNotNull(packge);
		packge.setProcessInstanceId(processInstance.getId());
		this.dao.save(packge);
		this.commitAndRestartTransaction();
	    	    
	    //Gets out of start state
	    processInstance.signal();
	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());
	}
	
}
