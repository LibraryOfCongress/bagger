package gov.loc.repository.workflow.actionhandlers;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

public class BaseActionHandlerTest {

	DummyActionHandler actionHandler= new DummyActionHandler();
		
	@Test
	public void testCreateObject() throws Exception {
		String aString = actionHandler.createObject(String.class);
		assertEquals("test", aString);
	}

	@Test(expected=Exception.class)
	public void testBadCreateObject() throws Exception {
		actionHandler.createObject(Integer.class);
	}
	
	@Test
	public void testCreateTestObject() throws Exception {
		actionHandler.factoryMethodMap.put("String", "gov.loc.repository.workflow.actionhandlers.BaseActionHandlerTest.createAString");
		String aString = actionHandler.createObject(String.class);
		assertEquals("astring", aString);
	}
	
	@Test
	public void testExecute() throws Exception {
		actionHandler.execute((ExecutionContext)null);
		assertEquals(3, actionHandler.i);
	}
	
	@Test
	public void testProperty() throws Exception {
		actionHandler.execute((ExecutionContext)null);
		assertEquals("foo", actionHandler.getTestProperty());
		assertEquals("foo", actionHandler.propertyField);
		assertEquals("foo", actionHandler.listField.get(0));
		assertEquals("foo", actionHandler.mapField.get("dummy"));
	}

	@SuppressWarnings("unchecked")
	@Test
	public void testAnnotations() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='gov.loc.repository.workflow.actionhandlers.DummyActionHandler'>" +
	      "      <configField>contextField</configField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "  <exception-handler>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      		      
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("contextField", "bar");
	    List<String> list = new ArrayList<String>();
	    list.add("foo");
	    list.add("bar");
	    processInstance.getContextInstance().setVariable("contextListField", list);
	    assertNotNull(processInstance.getContextInstance().getVariable("contextListField"));
	    processInstance.signal();
	    assertFalse(processInstance.isSuspended());
	    assertEquals("end", processInstance.getRootToken().getNode().getName());
		assertEquals("contextField", processInstance.getContextInstance().getVariable("configField"));
	    assertEquals("bar", processInstance.getContextInstance().getVariable("requiredContextField"));
		assertNull(processInstance.getContextInstance().getVariable("optionalContextField"));
		assertEquals("bar", processInstance.getContextInstance().getVariable("indirectContextField"));
	    assertTrue(processInstance.getContextInstance().getVariable("listContextField") instanceof List);
	    assertEquals(2, ((List<String>)processInstance.getContextInstance().getVariable("listContextField")).size());
	}

	@Test
	public void testMissingTransition() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='a' />" +
			      "  </start-state>" +
			      "  <node name='a'>" +
			      "    <action class='gov.loc.repository.workflow.actionhandlers.DummyActionHandler'>" +
			      "      <configField>contextField</configField>" +
			      "    </action>" +
			      "    <transition name='b' to='end' />" +
			      "  </node>" +		      
			      "  <end-state name='end' />" +
			      "  <exception-handler>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
			      "      </action>" +
			      "  </exception-handler>" +	      		      
			      "</process-definition>");

		    ProcessInstance processInstance = new ProcessInstance(processDefinition);
		    processInstance.getContextInstance().setVariable("contextField", "bar");
		    processInstance.signal();
		    assertTrue(processInstance.isSuspended());
		    
	}

	@Test
	public void testMissingContextVariable() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='a' />" +
			      "  </start-state>" +
			      "  <node name='a'>" +
			      "    <action class='gov.loc.repository.workflow.actionhandlers.DummyActionHandler'>" +
			      "      <configField>contextField</configField>" +
			      "    </action>" +
			      "    <transition name='b' to='end' />" +
			      "    <transition name='c' to='end' />" +
			      "  </node>" +		      
			      "  <end-state name='end' />" +
			      "  <exception-handler>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
			      "      </action>" +
			      "  </exception-handler>" +	      		      
			      "</process-definition>");

		    ProcessInstance processInstance = new ProcessInstance(processDefinition);
		    //processInstance.getContextInstance().setVariable("contextField", "bar");
		    processInstance.signal();
		    assertTrue(processInstance.isSuspended());
			    
	}

	@Test
	public void testMissingIndirectContextVariable() throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
			      "<process-definition>" +
			      "  <start-state>" +
			      "    <transition to='a' />" +
			      "  </start-state>" +
			      "  <node name='a'>" +
			      "    <action class='gov.loc.repository.workflow.actionhandlers.DummyActionHandler'>" +
			      "      <configField>xcontextField</configField>" +
			      "    </action>" +
			      "    <transition name='b' to='end' />" +
			      "    <transition name='c' to='end' />" +
			      "  </node>" +		      
			      "  <end-state name='end' />" +
			      "  <exception-handler>" +
			      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
			      "      </action>" +
			      "  </exception-handler>" +	      		      
			      "</process-definition>");

		    ProcessInstance processInstance = new ProcessInstance(processDefinition);
		    processInstance.getContextInstance().setVariable("contextField", "bar");
		    processInstance.signal();
		    assertTrue(processInstance.isSuspended());
	}

	@Test
	public void testMissingConfiguration() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='gov.loc.repository.workflow.actionhandlers.DummyActionHandler'>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "  <exception-handler>" +
	      "      <action class='gov.loc.repository.workflow.actionhandlers.ExceptionActionHandler'>" +
	      "      </action>" +
	      "  </exception-handler>" +	      		      
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("contextField", "bar");
	    processInstance.signal();
	    assertTrue(processInstance.isSuspended());
	    
	}
		
	public static String createAString()
	{
		return "astring";
	}
	
	
}
