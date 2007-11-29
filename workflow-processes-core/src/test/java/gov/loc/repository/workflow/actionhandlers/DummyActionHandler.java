package gov.loc.repository.workflow.actionhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.repository.workflow.actionhandlers.annotations.*;

@Transitions(transitions={"b","c"})
public class DummyActionHandler extends BaseActionHandler
{
	public DummyActionHandler() {
		listField.add("${baseactionhandler.test}");
		mapField.put("dummy", "${baseactionhandler.test}");
	}
	
	private static final long serialVersionUID = 1L; 
	public int i=0;
	public String propertyField = "${baseactionhandler.test}";
	
	@ContextVariable(name="contextListField", isRequired=false)
	public List<String> listContextField;
	
	@SuppressWarnings("unused")
	@ContextVariable(name="contextField")
	public String requiredContextField;
	
	@SuppressWarnings("unused")
	@ContextVariable(name="optionalContextField", isRequired=false)
	public String optionalContextField;
	
	@ContextVariable(configurationFieldName="configField")
	public String indirectContextField;
	
	public String configField;
	
	public List<String> listField = new ArrayList<String>();
	
	public Map<String,String> mapField = new HashMap<String, String>();
	
	@Override	
	protected void initialize() throws Exception {
		i += 1;		
	}
	
	@Override
	protected void execute() throws Exception {
		i += 2;
		if (this.executionContext != null)
		{
			this.executionContext.getContextInstance().setVariable("requiredContextField", this.requiredContextField);
			this.executionContext.getContextInstance().setVariable("listContextField", this.listContextField);
			this.executionContext.getContextInstance().setVariable("optionalContextField", this.optionalContextField);
			this.executionContext.getContextInstance().setVariable("indirectContextField", this.indirectContextField);
			this.executionContext.getContextInstance().setVariable("configField", this.configField);
			this.executionContext.getContextInstance().setVariable("listField", this.listField);
			this.executionContext.getContextInstance().setVariable("mapField", this.mapField);			
			this.executionContext.leaveNode("b");
		}
	}
	
	public String createString()
	{
		return "test";
	}
	
	public String getTestProperty()
	{
		return this.getConfiguration().getString("baseactionhandler.test");
	}
	
}
