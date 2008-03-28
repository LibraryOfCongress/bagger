package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class TokenBean extends AbstractWorkflowBean {
	
	private Token token;
	
	public Token getToken()
	{
		return this.token;
	}
	
	void setToken(Token token)
	{
		this.token = token;
	}
		
	public String getId()
	{
		return Long.toString(this.token.getId());
	}
	
	public String getName() {
		return "Token " + this.getId();
	}
	
	public NodeBean getNodeBean()
	{
		return this.factory.createNodeBean(this.token.getNode());
	}
	
	public boolean isChild()
	{
		return this.token.hasParent();
	}
	
	public boolean isEnded()
	{
		return this.token.hasEnded();
	}
	
	public TokenBean getParentTokenBean()
	{
		if (! this.isChild())
		{
			return null;
		}
		return this.factory.createTokenBean(this.token.getParent());
		
	}
		
	@SuppressWarnings("unchecked")
	public List<String> getLogEntryList()
	{
		List<String> logEntryList = new ArrayList<String>();
		List<ProcessLog> processLogList = this.jbpmContext.getLoggingSession().findLogsByToken(this.token.getId());
		log.debug("ProcessLogList has " + processLogList.size());
		Iterator<ProcessLog> iter = processLogList.iterator();
		while (iter.hasNext())
		{
			ProcessLog processLog = iter.next();
			logEntryList.add(processLog.toString());
		}
		log.debug("LogEntryList has " + logEntryList.size());
		return logEntryList;
	}
		
	@SuppressWarnings("unchecked")
	public List<TaskInstanceBean> getTaskInstanceBeanList()
	{
		List<TaskInstance> taskInstanceList = this.jbpmContext.getTaskMgmtSession().findTaskInstancesByToken(this.token.getId());
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator<TaskInstance> iter = taskInstanceList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = this.factory.createTaskInstanceBean(iter.next());
			taskInstanceBeanList.add(taskInstanceBean);
		}
		return taskInstanceBeanList;
	}
	
	public ProcessInstanceBean getProcessInstanceBean()
	{
		return this.factory.createProcessInstanceBean(this.token.getProcessInstance());
	}
	
	public boolean isMovable()
	{		
		if (this.isEnded() || ! this.getProcessInstanceBean().isSuspended())
		{
			return false;
		}
		return true;
	}
	
	public void setNodeBean(NodeBean nodeBean) throws Exception
	{
		if (! this.isMovable())
		{
			throw new Exception("Unable to move to node " + nodeBean.getName());
		}
		log.debug("Moving node to " + nodeBean.getName());
		this.token.setNode(nodeBean.getNode());
		this.token.getProcessInstance().resume();
		nodeBean.getNode().enter(new ExecutionContext(this.token));
	}
	
}
