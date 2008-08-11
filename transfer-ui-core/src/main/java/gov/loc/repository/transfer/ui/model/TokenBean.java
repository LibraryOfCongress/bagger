package gov.loc.repository.transfer.ui.model;

import gov.loc.repository.serviceBroker.ServiceRequest;
import static gov.loc.repository.workflow.WorkflowConstants.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Query;
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
	
	public boolean isSuspended()
	{
		return this.token.isSuspended();
	}
	
	public java.util.Date hasEndDate()
	{
		return this.token.getEnd();
	}
	
	public WorkflowExceptionBean getWorkflowExceptionBean()
	{
		if (! this.isSuspended())
		{
			return null;
		}
		ExecutionContext executionContext = new ExecutionContext(this.token);
		return this.factory.createWorkflowExceptionBean((String)executionContext.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION), (String)executionContext.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_DETAIL), (String)executionContext.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_NODENAME), (String)executionContext.getContextInstance().getVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME));
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
	public List<TaskInstanceBean> getActiveTaskInstanceBeanList()
	{
		return this.taskInstanceListToTaskInstanceBeanList(this.jbpmContext.getTaskMgmtSession().findTaskInstancesByToken(this.token.getId()));
		
	}
	
	private List<TaskInstanceBean> taskInstanceListToTaskInstanceBeanList(List<TaskInstance> taskInstanceList)
	{
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator<TaskInstance> iter = taskInstanceList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = this.factory.createTaskInstanceBean(iter.next());
			taskInstanceBeanList.add(taskInstanceBean);
		}
		return taskInstanceBeanList;
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskInstanceBean> getTaskInstanceBeanList() throws Exception
	{
		String queryString = "select ti " +
			"from org.jbpm.taskmgmt.exe.TaskInstance ti " +
			"where ti.token.id = :tokenId";
		Query query = this.jbpmContext.getSession().createQuery(queryString);
	    query.setLong("tokenId", this.token.getId());
	    List<TaskInstance> taskInstanceList = query.list();
	    return this.taskInstanceListToTaskInstanceBeanList(taskInstanceList);	    
	}
	
	public ProcessInstanceBean getProcessInstanceBean()
	{
		return this.factory.createProcessInstanceBean(this.token.getProcessInstance());
	}
	
	public boolean isMovable()
	{		
		if (! this.isEnded() && this.isSuspended() && ! this.token.getProcessInstance().isSuspended())
		{
			return true;
		}
		return false;
	}
	
	public void setNodeBean(NodeBean nodeBean) throws Exception
	{
		if (! this.isMovable())
		{
			throw new Exception("Unable to move to node " + nodeBean.getName());
		}
		log.debug("Moving node to " + nodeBean.getName());
		this.token.setNode(nodeBean.getNode());
		this.token.resume();
		this.broker.resume(this.getId());
		ExecutionContext executionContext = new ExecutionContext(this.token);
		nodeBean.getNode().enter(executionContext);
		executionContext.getContextInstance().deleteVariable(VARIABLE_LAST_EXCEPTION);
		executionContext.getContextInstance().deleteVariable(VARIABLE_LAST_EXCEPTION_ACTIONNAME);
		executionContext.getContextInstance().deleteVariable(VARIABLE_LAST_EXCEPTION_NODENAME);
		executionContext.getContextInstance().deleteVariable(VARIABLE_LAST_EXCEPTION_DETAIL);
	}
	
	public List<ServiceRequest> getServiceRequestList()
	{
		return this.broker.findServiceRequests(this.getId());
	}
	
}
