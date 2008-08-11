package gov.loc.repository.workflow.actionhandlers;

public class ActionHandlerException extends Exception {

	private static final long serialVersionUID = 1L;

	Long processInstanceId = null;
	Long tokenId = null;
	String nodeName = null;
	String actionName = null;
	
	public ActionHandlerException() {
		super();
	}
	
	public ActionHandlerException(String message)
	{
		super(message);
	}
	
	public ActionHandlerException(String message, Throwable cause)
	{
		super(message, cause);
	}
	
	public ActionHandlerException(Throwable cause)
	{
		super(cause);
	}
	
	public ActionHandlerException(Long processInstanceId, Long tokenId, String nodeName, String actionName, Throwable cause)
	{
		super(cause);
		this.processInstanceId = processInstanceId;
		this.tokenId = tokenId;
		this.nodeName = nodeName;
		this.actionName = actionName;
	}

	public Long getProcessInstanceId() {
		return processInstanceId;
	}

	public Long getTokenId() {
		return tokenId;
	}

	public String getNodeName() {
		return nodeName;
	}

	public String getActionName() {
		return actionName;
	}

}
