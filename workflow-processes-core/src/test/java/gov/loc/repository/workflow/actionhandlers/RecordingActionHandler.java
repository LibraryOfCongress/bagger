package gov.loc.repository.workflow.actionhandlers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.repository.workflow.actionhandlers.annotations.*;

public class RecordingActionHandler extends BaseActionHandler
{
	public RecordingActionHandler(String actionHandlerConfiguration) {
		super(actionHandlerConfiguration);
		
	}
	
	private static final long serialVersionUID = 1L; 

	public String transition = null;
	
	@Required
	public String id;
		
	@SuppressWarnings("unchecked")
	@Override
	protected void execute() throws Exception {
		List<String> idList = (List<String>)this.executionContext.getContextInstance().getVariable("idList");
		if (idList == null)
		{
			idList = new ArrayList<String>();
		}
		idList.add(this.id);
			
		this.executionContext.getContextInstance().createVariable("idList", idList);
		
		if (this.transition != null)
		{
			this.leave(this.transition);
		}
	}
	
	
}
