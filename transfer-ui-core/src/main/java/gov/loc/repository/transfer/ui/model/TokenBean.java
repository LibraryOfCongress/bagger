package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.graph.exe.Token;
import org.jbpm.logging.log.ProcessLog;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class TokenBean extends AbstractWorkflowBean {
	private static final Log log = LogFactory.getLog(TokenBean.class);
	
	private Token token;
	
	public void setToken(Token token)
	{
		this.token = token;
	}
		
	public long getId()
	{
		return this.token.getId();
	}
	
	public NodeBean getNodeBean()
	{
		NodeBean nodeBean = new NodeBean();
		nodeBean.setJbpmContext(jbpmContext);
		nodeBean.setNode(this.token.getNode());
		return nodeBean;
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
		TokenBean parentTokenBean = new TokenBean();
		parentTokenBean.setJbpmContext(jbpmContext);
		parentTokenBean.setToken(this.token.getParent());
		return parentTokenBean;
		
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
		List<TaskInstance> taskInstanceList = this.jbpmContext.getTaskMgmtSession().findTaskInstancesByToken(this.getId());
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator<TaskInstance> iter = taskInstanceList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = new TaskInstanceBean();
			taskInstanceBean.setJbpmContext(this.jbpmContext);
			taskInstanceBean.setTaskInstance(iter.next());			
			taskInstanceBeanList.add(taskInstanceBean);
		}
		return taskInstanceBeanList;
	}
	
	public ProcessInstanceBean getProcessInstanceBean()
	{
		ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
		processInstanceBean.setJbpmContext(this.jbpmContext);
		processInstanceBean.setProcessInstance(this.token.getProcessInstance());
		return processInstanceBean;
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
	
	public void save()
	{
		this.jbpmContext.save(this.token);
	}
}
