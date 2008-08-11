package gov.loc.repository.transfer.ui.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.jbpm.graph.exe.Comment;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.graph.node.TaskNode;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;

public class ProcessInstanceBean extends AbstractWorkflowBean implements VariableUpdatingBean {
	
	private ProcessInstance processInstance;
	
	public ProcessInstance getProcessInstance()
	{
		return this.processInstance;
	}
	
	void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public String getPackageName() {		
		return (String)this.processInstance.getContextInstance().getVariable("packageId");
	}
	
	public ProcessDefinitionBean getProcessDefinitionBean() {
		return this.factory.createProcessDefinitionBean(this.processInstance.getProcessDefinition());
	}
	
	public String getName() {
		return "Process instance " + this.getId();
	}
	
	public String getId() {
		return Long.toString(this.processInstance.getId());
	}
	
	public void addComment(String message)
	{
		this.processInstance.getRootToken().addComment(message);
	}
	
	@SuppressWarnings("unchecked")
	public List<CommentBean> getCommentBeanList()
	{
		List<Comment> commentList = this.processInstance.getRootToken().getComments();
		List<CommentBean> commentBeanList = new ArrayList<CommentBean>();
		Iterator<Comment> iter = commentList.iterator();
		while (iter.hasNext())
		{
			CommentBean commentBean = this.factory.createCommentBean(iter.next());
			commentBeanList.add(commentBean);
		}
		return commentBeanList;

	}
	
	@SuppressWarnings("unchecked")
	public Map getVariableMap()
	{
		return this.processInstance.getContextInstance().getVariables();
	}
		
	public void setVariable(String name, Object value)
	{
		log.debug(MessageFormat.format("Setting variable {0} to {1}", name, value));
		this.processInstance.getContextInstance().setVariable(name, value);
	}

	@SuppressWarnings("unchecked")
	public List<TokenBean> getTokenBeanList()
	{
		List<TokenBean> tokenBeanList = new ArrayList<TokenBean>();
		List<Token> tokenList = this.processInstance.findAllTokens();
		log.debug("TokenList has " + tokenList.size());
		Iterator<Token> iter = tokenList.iterator();
		while(iter.hasNext())
		{			
			TokenBean tokenBean = this.factory.createTokenBean(iter.next());
			tokenBeanList.add(tokenBean);
		}
		log.debug("TokenBeanList has " + tokenBeanList.size());
		return tokenBeanList;
	}
		
	public boolean isSuspended()
	{
		return this.processInstance.isSuspended();
	}

	private List<Token> getTokenList()
	{
		List<Token> tokenList = new ArrayList<Token>(); 
		this.processInstance.getRootToken().collectChildrenRecursively(tokenList);
		tokenList.add(this.processInstance.getRootToken());
		return tokenList;
	}
	
	public void suspended(boolean suspended)
	{
		if (suspended && ! this.isSuspended())
		{
			this.processInstance.suspend();
			List<Token> tokenList = this.getTokenList(); 
			for(Token token : tokenList)
			{
				this.broker.suspend(Long.toString(token.getId()));				
			}
			
		}
		else if (! suspended && this.isSuspended())
		{
			this.processInstance.resume();
			List<Token> tokenList = this.getTokenList(); 
			for(Token token : tokenList)
			{
				this.broker.resume(Long.toString(token.getId()));				
			}
			
		}
	}
		
	public boolean isEnded() {
		return this.processInstance.hasEnded();
	}
	
	public Date getEndDate(){
		return this.processInstance.getEnd();
	}
	
	public Date getStartDate()
	{
		return this.processInstance.getStart();
	}
	
	@SuppressWarnings("unchecked")
	public void cancel()
	{
		TaskNode cancelNode = (TaskNode)processInstance.getProcessDefinition().getNode("cancel");
		processInstance.getRootToken().setNode(cancelNode);
		cancelNode.removeTaskInstanceSynchronization(processInstance.getRootToken());
		cancelNode.enter(new ExecutionContext(processInstance.getRootToken()));
		TaskMgmtInstance taskMgmtInstance = (TaskMgmtInstance) processInstance.getInstance(TaskMgmtInstance.class);
		Collection<TaskInstance> taskInstances = taskMgmtInstance.getUnfinishedTasks(processInstance.getRootToken());
		TaskInstance cancelTaskInstance = null;
		Iterator<TaskInstance> iter = taskInstances.iterator();
		while(iter.hasNext())
		{
			cancelTaskInstance = iter.next();
			if ("cancel task".equals(cancelTaskInstance.getName()))
			{
				break;
			}
		}
		if (cancelTaskInstance == null)
		{
			throw new RuntimeException("Cancel task instance not found");
		}
		cancelTaskInstance.setActorId(this.jbpmContext.getActorId());
		
	}
}
