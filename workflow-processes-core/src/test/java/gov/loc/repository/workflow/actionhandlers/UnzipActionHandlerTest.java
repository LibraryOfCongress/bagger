package gov.loc.repository.workflow.actionhandlers;

import org.junit.Test;
import static org.junit.Assert.*;

import org.junit.runner.RunWith;

import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.jmock.Expectations;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import gov.loc.repository.transfer.components.packaging.Unpackager;
import gov.loc.repository.workflow.utilities.ConfigurationHelper;

import java.io.File;

@RunWith(JMock.class)
public class UnzipActionHandlerTest {
	static Mockery context = new JUnit4Mockery();
	
	//Everything goes according to plan
	@Test
	public void executeDefault() throws Exception
	{
				
		//A simple process definition is used to test the action handler.
		//Note no jbpmContext, so this process definition isn't being persisted.
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test1'>" +
	      "  <start-state>" +
	      "    <transition to='unzip' />" +
	      "  </start-state>" +
	      "  <node name='unzip'>" +
	      "    <action name='unzip' class='gov.loc.repository.workflow.actionhandlers.UnzipActionHandler'>" +
	      "      <baseDestinationDirectory>c:\temp</baseDestinationDirectory>" +
	      //Here's where the mock component is injected.
	      //When createObject(Unpackager.class) is called, UnzipActionHandlerTest.createMockUnpackager (see below) will be invoked rather than createUnpackager().
	      "    </action>" +
	      "    <transition name='troubleshoot' to='c' />" +
	      "    <transition name='continue' to='b' />" +
	      "  </node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test1.unzip.Unpackager.factorymethod", "gov.loc.repository.workflow.actionhandlers.UnzipActionHandlerTest.createMockUnpackager");				 

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("sourceFile", "c:\\test\\here.zip");
	    processInstance.signal();
	    
	    assertEquals("b", processInstance.getRootToken().getNode().getName());

	    assertTrue(processInstance.getContextInstance().hasVariable("destinationDirectory"));
	    assertTrue(((String)processInstance.getContextInstance().getVariable("destinationDirectory")).startsWith("c:"));
	    
	}

	public static Unpackager createMockUnpackager() throws Exception
	{
		//Setup mock
		final Unpackager unpackager = context.mock(Unpackager.class);
		context.checking(new Expectations() {{
			one(unpackager).unpackage(with(any(File.class)), with(any(File.class)));
		}});
		return unpackager;
	}
	
	//Unpackager throws Exception
	@Test
	public void executeUnpackagerException() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test2'>" +
	      "  <start-state>" +
	      "    <transition to='unzip' />" +
	      "  </start-state>" +
	      "  <node name='unzip'>" +
	      "    <action name='unzip' class='gov.loc.repository.workflow.actionhandlers.UnzipActionHandler'>" +
	      "      <baseDestinationDirectory>c:\temp</baseDestinationDirectory>" +
	      "    </action>" +
	      "    <transition name='continue' to='b' />" +
	      "  </node>" +
	      "  <end-state name='b' />" +
	      "  <exception-handler>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      	      
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test2.unzip.Unpackager.factorymethod", "gov.loc.repository.workflow.actionhandlers.UnzipActionHandlerTest.createThrowingMockUnpackager");				 
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("sourceFile", "c:\\test\\here.zip");
	    processInstance.signal();
	    
	    assertTrue(processInstance.getRootToken().isSuspended());
	    
	}
			
	public static Unpackager createThrowingMockUnpackager() throws Exception
	{
		//Setup mock
		final Unpackager unpackager = context.mock(Unpackager.class);
		context.checking(new Expectations() {{
			one(unpackager).unpackage(with(any(File.class)), with(any(File.class)));
			will(throwException(new Exception("Ooops.  The unpackager didn't work.")));
		}});
		return unpackager;
	}
	
	//Continue transition missing
	@Test(expected=Exception.class)
	public void executeContinueTransitionMissing() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test3'>" +
	      "  <start-state>" +
	      "    <transition to='unzip' />" +
	      "  </start-state>" +
	      "  <node name='unzip'>" +
	      "    <action name='unzip' class='gov.loc.repository.workflow.actionhandlers.UnzipActionHandler'>" +
	      "      <baseDestinationDirectory>c:\temp</baseDestinationDirectory>" +
	      "    </action>" +
	      "    <transition name='troubleshoot' to='c' />" +
	      "  </node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test3.unzip.Unpackager.factorymethod", "gov.loc.repository.workflow.actionhandlers.UnzipActionHandlerTest.createThrowingMockUnpackager");				 
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("sourceFile", "c:\\test\\here.zip");
	    processInstance.signal();
	    	    
	}

	
	//baseDestinationDirectory missing
	@Test(expected=Exception.class)
	public void executeBaseDestinationDirectoryMissing() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test4'>" +
	      "  <start-state>" +
	      "    <transition to='unzip' />" +
	      "  </start-state>" +
	      "  <node name='unzip'>" +
	      "    <action name='unzip' class='gov.loc.repository.workflow.actionhandlers.UnzipActionHandler'>" +
	      "      <factoryMethodMap>" +
	      "        <entry><key>Unpackager</key><value>gov.loc.repository.workflow.actionhandlers.UnzipActionHandlerTest.createMockUnpackager</value></entry>" +
	      "      </factoryMethodMap>" +
	      "    </action>" +
	      "    <transition name='troubleshoot' to='c' />" +
	      "    <transition name='continue' to='b' />" +
	      "  </node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test4.unzip.Unpackager.factorymethod", "gov.loc.repository.workflow.actionhandlers.UnzipActionHandlerTest.createMockUnpackager");				 
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("sourceFile", "c:\\test\\here.zip");
	    processInstance.signal();	    	    
	}

	
	//sourceFile missing
	@Test(expected=Exception.class)
	public void executeSourceFileMissing() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition name='test5'>" +
	      "  <start-state>" +
	      "    <transition to='unzip' />" +
	      "  </start-state>" +
	      "  <node name='unzip'>" +
	      "    <action name='unzip' class='gov.loc.repository.workflow.actionhandlers.UnzipActionHandler'>" +
	      "      <baseDestinationDirectory>c:\temp</baseDestinationDirectory>" +
	      "    </action>" +
	      "    <transition name='troubleshoot' to='c' />" +
	      "    <transition name='continue' to='b' />" +
	      "  </node>" +
	      "  <end-state name='b' />" +
	      "  <end-state name='c' />" +
	      "</process-definition>");
		ConfigurationHelper.getConfiguration().addProperty("test5.unzip.Unpackager.factorymethod", "gov.loc.repository.workflow.actionhandlers.UnzipActionHandlerTest.createMockUnpackager");				 
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.signal();
	    	    
	}
	
	@Test
	public void createUnpackager() throws Exception
	{
		UnzipActionHandler handler = new UnzipActionHandler();
		assertTrue(handler.createObject(Unpackager.class) instanceof Unpackager);
	}
	
	
}
