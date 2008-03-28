package gov.loc.repository.transfer.ui.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Date;

import org.jbpm.graph.exe.Comment;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;

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

	public void suspended(boolean suspended)
	{
		if (suspended && ! this.isSuspended())
		{
			this.processInstance.suspend();
		}
		else if (! suspended && this.isSuspended())
		{
			this.processInstance.resume();
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
	/*
	public void end()
	{
		this.processInstance.end();
	}
	
	*/
}
