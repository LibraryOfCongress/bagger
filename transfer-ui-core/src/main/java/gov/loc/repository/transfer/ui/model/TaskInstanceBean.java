package gov.loc.repository.transfer.ui.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class TaskInstanceBean extends AbstractWorkflowBean implements VariableUpdatingBean {
	
	private TaskInstance taskInstance;
	private String transition;
	
	public TaskInstance getTaskInstance()
	{
		return this.taskInstance;
	}
	
	void setTaskInstance(TaskInstance taskInstance)
	{
		this.taskInstance = taskInstance;
		if (this.taskInstance.getTask().getTaskController() != null)
		{
			this.taskInstance.getTask().getTaskController().initializeVariables(this.taskInstance);
		}
				
	}
		
	public String getId()
	{
		return Long.toString(this.taskInstance.getId());
	}
	
	public String getName()
	{
		return "Task instance " + this.getId();
	}
	
	public boolean canUnassignUserBean()
	{
		if (this.taskInstance.getPooledActors().isEmpty())
		{
			return false;
		}
		return true;
	}
	
	public void setUserBean(UserBean userBean) throws Exception
	{
		if (! this.canUpdateUserBean())
		{
			throw new Exception("User for TaskInstance cannot be updated");
		}
		if (userBean == null)
		{
			if (! this.canUnassignUserBean())
			{
				throw new Exception("TaskInstance cannot be unassigned");
			}
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
		return this.factory.createUserBean(this.taskInstance.getActorId());
	}
	
	public boolean canUpdateUserBean()
	{
		if (! this.isEnded())
		{
			return true;
		}
		return false;
	}
	
	public boolean canUpdate()
	{
		if (! this.isEnded() && ! this.taskInstance.getProcessInstance().isSuspended() && ! this.taskInstance.getProcessInstance().hasEnded())
		{
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<GroupBean> getGroupBeanList()
	{
		//Making assumption that pooled actors only includes groups.  This is not necessarily the case, as could include actors.
		List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
		this.addPooledActors(this.taskInstance.getPooledActors(), groupBeanList);
		return groupBeanList;
	}
	
	private void addPooledActors(Set<PooledActor> pooledActors, List<GroupBean> groupBeanList)
	{
		if (pooledActors != null)
		{
			Iterator<PooledActor> iter = pooledActors.iterator();
			while (iter.hasNext())
			{				
				PooledActor pooledActor = iter.next();				
				GroupBean groupBean = this.factory.createGroupBean(pooledActor.getActorId());
				groupBeanList.add(groupBean);
			}
		}		
	}
	
	public TaskBean getTaskBean()
	{
		return this.factory.createTaskBean(this.taskInstance.getTask());
	}
	
	@SuppressWarnings("unchecked")
	public Map<Object,Object> getVariableMap()
	{
		return this.taskInstance.getVariablesLocally();
	}
	
	public void setVariable(String key, Object value)
	{
		log.debug(MessageFormat.format("Setting variable {0} with value {1} for {2}", key, value, this));
		this.taskInstance.setVariableLocally(key, value);
	}
	
	public void setTransition(String transition)
	{
		this.transition = transition;
	}

	public String getTransition()
	{
		return this.transition;
	}
	
	public boolean isEnded()
	{
		if (this.taskInstance.hasEnded() || this.getProcessInstanceBean().isEnded())
		{
			return true;
		}
		return false;
	}
	
	public ProcessInstanceBean getProcessInstanceBean()
	{
		return this.factory.createProcessInstanceBean(this.taskInstance.getProcessInstance());
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
	public Date getEndDate(){
		return this.taskInstance.getEnd();
	}
	
	public Date getCreateDate()
	{
		return this.taskInstance.getCreate();
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
}
