package gov.loc.repository.console.workflow.beans;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class TaskInstanceBean extends AbstractWorkflowBean {
	
	private TaskInstance taskInstance;
	
	public void setTaskInstance(TaskInstance taskInstance)
	{
		this.taskInstance = taskInstance;
		if (this.taskInstance.getTask().getTaskController() != null)
		{
			this.taskInstance.getTask().getTaskController().initializeVariables(this.taskInstance);
		}
				
	}
		
	public long getId()
	{
		return this.taskInstance.getId();
	}
	
	public void setUserBean(UserBean userBean)
	{
		if (userBean == null)
		{
			this.taskInstance.setActorId(null);
		}
		else
		{
			this.taskInstance.setActorId(userBean.getId());
		}
	}
	
	public UserBean getUserBean()
	{
		if (this.taskInstance.getActorId() == null)
		{
			return null;
		}
		UserBean userBean = new UserBean();
		userBean.setId(this.taskInstance.getActorId());
		userBean.setJbpmContext(jbpmContext);
		return userBean;
	}
	
	public List<GroupBean> getGroupBeanList()
	{
		//Making assumption that pooled actors only includes groups.  This is not necessarily the case, as could include actors.
		List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
		this.addPooledActors(this.taskInstance.getPooledActors(), groupBeanList);
		return groupBeanList;
	}
	
	private void addPooledActors(Set pooledActors, List<GroupBean> groupBeanList)
	{
		if (pooledActors != null)
		{
			Iterator iter = pooledActors.iterator();
			while (iter.hasNext())
			{				
				PooledActor pooledActor = (PooledActor)iter.next();
				GroupBean groupBean = new GroupBean();
				groupBean.setJbpmContext(jbpmContext);
				groupBean.setId(pooledActor.getActorId());
				groupBeanList.add(groupBean);
			}
		}		
	}
	
	public TaskBean getTaskBean()
	{
		TaskBean taskBean = new TaskBean();
		taskBean.setTask(this.taskInstance.getTask());
		taskBean.setJbpmContext(jbpmContext);
		return taskBean;
	}
	
	public Map getVariableMap()
	{
		return this.taskInstance.getVariablesLocally();
	}
	
	public void setVariable(String key, Object value)
	{
		this.taskInstance.setVariableLocally(key, value);
	}
	
	public void setTransition(String transition)
	{
		this.taskInstance.end(transition);
	}

	public boolean isEnded()
	{
		if (this.taskInstance.hasEnded() || this.getProcessInstanceBean().isEnded())
		{
			return true;
		}
		return false;
	}

	public void save()
	{
		if (this.taskInstance.getTask().getTaskController() != null)
		{
			this.taskInstance.getTask().getTaskController().submitParameters(this.taskInstance);
		}		
		this.jbpmContext.save(this.taskInstance);
	}
	
	public ProcessInstanceBean getProcessInstanceBean()
	{
		ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
		processInstanceBean.setJbpmContext(jbpmContext);
		processInstanceBean.setProcessInstance(this.taskInstance.getProcessInstance());
		return processInstanceBean;
	}
	/*
	public TokenBean getToken()
	{
		TokenBean tokenBean = new TokenBean();
		tokenBean.setJbpmContext(jbpmContext);
		tokenBean.setToken(this.taskInstance.getToken());
		return tokenBean;
	}
	*/
	public Date getEndDate()
	{
		return this.taskInstance.getEnd();
	}
}
