package gov.loc.repository.workflow.actionhandlers;

import java.text.MessageFormat;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ExceptionActionHandler extends BaseActionHandler {

	private static final long serialVersionUID = 1L;
	private static final Log log = LogFactory.getLog(ExceptionActionHandler.class);
	@Override
	protected void execute() throws Exception {		
		log.debug(MessageFormat.format("Process instance {0}, token {1} threw an exception and has been suspended.  Current node is {2}.  Exception is {3}.", this.executionContext.getProcessInstance().getId(), this.executionContext.getToken().getId(),this.executionContext.getNode().getId(), this.executionContext.getException()), this.executionContext.getException());
		this.executionContext.getProcessInstance().suspend();
	}

}
