package gov.loc.repository.workflow.actionhandlers;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

import org.junit.Test;
import static org.junit.Assert.*;


public class VariableAdapterActionHandlerTest {

	@Test
	public void execute() throws Exception
	{
				
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(
	      "<process-definition>" +
	      "  <start-state>" +
	      "    <transition to='n1'>" +
	      "      <action name='adapt variable' class='gov.loc.repository.workflow.actionhandlers.VariableAdapterActionHandler'>" +
          "        <fromVariableName>v1</fromVariableName>" +
          "        <toVariableName>v2</toVariableName>" +
          "      </action>" +
          "    </transition>" +
	      "  </start-state>" +
	      "  <state name='n1'></state>" +
	      "</process-definition>");
	    
	    ProcessInstance processInstance = new ProcessInstance(processDefinition);
	    processInstance.getContextInstance().setVariable("v1", "a");
	    assertFalse(processInstance.getContextInstance().hasVariable("v2"));
	    processInstance.signal();

	    assertEquals("a", (String)processInstance.getContextInstance().getVariable("v2"));
	}
	
	
}
