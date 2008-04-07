package gov.loc.repository.workflow.actionhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.repository.workflow.actionhandlers.annotations.*;

@Transitions(transitions={"b","c"})
public class DummyActionHandler extends BaseActionHandler
{
	public DummyActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
		
	}
	
	private static final long serialVersionUID = 1L; 
	public int i=0;

	@Required
	public String configField;
	
	public List<String> listConfigField;
	
	public Map<String, String> mapConfigField;
		
	@Override	
	protected void initialize() throws Exception {
		i += 1;		
	}
	
	@Override
	protected void execute() throws Exception {
		i += 2;
		this.setVariable("configField", this.configField);
		this.setVariable("listConfigField", this.listConfigField);
		this.setVariable("mapConfigField", this.mapConfigField);
		this.leave("b");
	}
	
	public String createString()
	{
		return "test";
	}
	
	public String getTestProperty() throws Exception
	{
		return this.getConfiguration().getString("baseactionhandler.test");
	}
	
}
