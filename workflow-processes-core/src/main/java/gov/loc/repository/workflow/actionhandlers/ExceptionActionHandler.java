package gov.loc.repository.workflow.actionhandlers;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ExceptionActionHandler.class);
	
	public ExceptionActionHandler(String actionHandlerConfig) {
		super(actionHandlerConfig);		
	}
	
	@Override
	protected void execute() throws Exception {		
		String processInstanceId = "unknown";
		if (this.executionContext.getProcessInstance() != null)
		{
			processInstanceId = Long.toString(this.executionContext.getProcessInstance().getId());
		}
		String tokenId = "unknown";
		if (this.executionContext.getToken() != null)
		{
			tokenId = Long.toString(this.executionContext.getToken().getId());
		}
		String nodeId = "unknown";
		if (this.executionContext.getNode() != null)
		{
			nodeId = Long.toString(this.executionContext.getNode().getId());
		}
		
		log.error(MessageFormat.format("Process instance {0}, token {1} threw an exception and has been suspended.  Current node is {2}.  Exception is {3}.", processInstanceId, tokenId, nodeId, this.executionContext.getException()));
		try
		{
			if (this.executionContext.getProcessInstance() != null)
			{
				this.executionContext.getProcessInstance().suspend();
			}
		}
		catch(Exception ex)
		{
			log.error("Error suspending process instance", ex);
		}

		
	}

}
