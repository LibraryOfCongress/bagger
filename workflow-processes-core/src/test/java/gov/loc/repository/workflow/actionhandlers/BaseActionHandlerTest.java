package gov.loc.repository.workflow.actionhandlers;

import static org.junit.Assert.*;

import gov.loc.repository.transfer.components.test.TestComponent;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.workflow.WorkflowConstants;

import java.io.FileFilter;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.Test;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;

public class BaseActionHandlerTest {

	DummyActionHandler actionHandler= new DummyActionHandler(null);
	Configuration configuration;
	
	@Before
	public void setup() throws Exception
	{
		configuration = ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
	
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
	public void testCreateFactoryObject() throws Exception {
		configuration.addProperty("none.String.factorymethod", this.getClass().getName() + ".createAString");
		String aString = actionHandler.createObject(String.class);
		assertEquals("astring", aString);
	}
	
	public static String createAString()
	{
		return "astring";
	}

	@Test(expected=Exception.class)
	public void testCreateBadProxyObject() throws Exception {
		//FileFilter is just a random interface, but does not implement Component
		configuration.addProperty("none.FileFilter.queue", "testqueue");
		FileFilter fileFilter = actionHandler.createObject(FileFilter.class);
		assertTrue(Proxy.getInvocationHandler(fileFilter) instanceof ServiceInvocationHandler);
		fileFilter.accept(null);
	}

	@Test
	public void testCreateProxyObject() throws Exception {
		configuration.addProperty("none.TestComponent.queue", "testqueue");
		TestComponent testComponent = actionHandler.createObject(TestComponent.class);
		assertTrue(Proxy.getInvocationHandler(testComponent) instanceof ServiceInvocationHandler);
		//testComponent.test("foo", true);
	}
	
	
	@Test
	public void testExecute() throws Exception {
		actionHandler.execute((ExecutionContext)null);
		assertEquals(3, actionHandler.i);
	}

	//Test configuration field
	@Test
	public void testConfigurationField() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "      <configField>foo</configField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.signal();
	    
	    assertEquals("end", processInstance.getRootToken().getNode().getName());
	    assertEquals("foo", processInstance.getContextInstance().getVariable("configField"));
	}

	//Test missing required field
	@Test(expected=Exception.class)
	public void testMissingRequiredField() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.signal();
	    
	}
	
	
	//Test configuration field with context variable placeholder
	@Test
	public void testConfigurationFieldWithContextVariablePlaceholder() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "      <configField>${contextvariable}</configField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("contextvariable", "foo");
	    processInstance.signal();
	    
	    assertEquals("end", processInstance.getRootToken().getNode().getName());
	    assertEquals("foo", processInstance.getContextInstance().getVariable("configField"));
	}

	//Test configuration field with configuration placeholder
	@Test
	public void testConfigurationFieldWithConfigurationPlaceholder() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "      <configField>$#{config}</configField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

		this.configuration.addProperty("config", "foo");
		ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.signal();
	    
	    assertEquals("end", processInstance.getRootToken().getNode().getName());
	    assertEquals("foo", processInstance.getContextInstance().getVariable("configField"));
	}
	
	//Test not allowed transition
	@Test(expected=Exception.class)
	public void testDeclaringBadTransition() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "      <configField>foo</configField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='d' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.signal();	    
	}
	
	//Test configuration list field with context variable placeholder
	@SuppressWarnings("unchecked")
	@Test
	public void testListConfigurationField() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "      <configField>foo</configField>" +
	      "      <listConfigField>${listContextVariable}</listConfigField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    List<String> list = new ArrayList<String>();
	    list.add("foo");
	    list.add("${contextVariable}");
	    processInstance.getContextInstance().setVariable("listContextVariable", list);
	    processInstance.getContextInstance().setVariable("contextVariable", "bar");
	    processInstance.signal();
	    	    
	    assertEquals("end", processInstance.getRootToken().getNode().getName());
	    assertNotNull(processInstance.getContextInstance().getVariable("listConfigField"));
	    list = (List<String>)processInstance.getContextInstance().getVariable("listConfigField");
	    assertEquals(2, list.size());
	    assertTrue(list.contains("foo"));
	    assertTrue(list.contains("bar"));
	}
	
	//Test configuration map field with context variable placeholder
	@SuppressWarnings("unchecked")
	@Test
	public void testMapConfigurationField() throws Exception
	{
	ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='a' />" +
	      "  </start-state>" +
	      "  <node name='a'>" +
	      "    <action class='DummyActionHandler'>" +
	      "      <configField>foo</configField>" +
	      "      <mapConfigField>${mapContextVariable}</mapConfigField>" +
	      "    </action>" +
	      "    <transition name='b' to='end' />" +
	      "    <transition name='c' to='end' />" +
	      "  </node>" +		      
	      "  <end-state name='end' />" +
	      "</process-definition>");

	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    Map<String,String> map = new HashMap<String, String>();
	    map.put("v1", "foo");
	    map.put("v2", "${contextVariable}");
	    processInstance.getContextInstance().setVariable("mapContextVariable", map);
	    processInstance.getContextInstance().setVariable("contextVariable", "bar");
	    processInstance.signal();
	    
	    
	    assertEquals("end", processInstance.getRootToken().getNode().getName());
	    assertNotNull(processInstance.getContextInstance().getVariable("mapConfigField"));
	    map = (Map<String,String>)processInstance.getContextInstance().getVariable("mapConfigField");
	    assertEquals(2, map.size());
	    assertTrue("foo".equals(map.get("v1")));
	    assertTrue("bar".equals(map.get("v2")));
	}

}
