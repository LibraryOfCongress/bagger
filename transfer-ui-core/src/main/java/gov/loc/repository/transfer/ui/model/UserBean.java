package gov.loc.repository.transfer.ui.model;

import java.security.Permission;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.jbpm.identity.Group;
import org.jbpm.identity.hibernate.IdentitySession;
import org.jbpm.taskmgmt.exe.TaskInstance;

public class UserBean extends AbstractWorkflowBean {
	private String id;
	
	public String getId()
	{
		return this.id;
	}
	
	public void setId(String id)
	{
		this.id = id;
	}
		
	@SuppressWarnings("unchecked")
	public List<ProcessDefinitionBean> getProcessDefinitionBeanList()
	{
		IdentitySession identitySession = new IdentitySession(this.jbpmContext.getSession());
		List<String> groupNameList = identitySession.getGroupNamesByUserAndGroupType(this.id, "organisation");
		Iterator<String> iter = groupNameList.iterator();

		Set<String> processDefinitionIdSet = new HashSet<String>();
		Pattern pattern = Pattern.compile("processdefinition\\.(.+)\\.initiate");
		while (iter.hasNext())
		{
			String groupName = iter.next();
			Group group = identitySession.getGroupByName(groupName);
			Iterator<Permission> permissionsIter = group.getPermissions().iterator();
			while(permissionsIter.hasNext())
			{
				Permission permission = permissionsIter.next();
				Matcher matcher = pattern.matcher(permission.getName());
				if (matcher.matches())
				{
					String processDefinitionId = matcher.group(1);
					processDefinitionIdSet.add(processDefinitionId);
				}
				
			}
		}
		List<ProcessDefinitionBean> processDefinitionBeanList = new ArrayList<ProcessDefinitionBean>();
		for(String processDefinitionId : processDefinitionIdSet)
		{
			ProcessDefinitionBean processDefinitionBean = new ProcessDefinitionBean();
			processDefinitionBean.setJbpmContext(jbpmContext);			    
			processDefinitionBean.setProcessDefinition(
			    jbpmContext.getGraphSession().findLatestProcessDefinition(
			        processDefinitionId
			    )
			);
			processDefinitionBeanList.add(processDefinitionBean);
		}
		return processDefinitionBeanList;
		
	}
	
	@SuppressWarnings("unchecked")
	public List<GroupBean> getGroupBeanList()
	{
		IdentitySession identitySession = new IdentitySession(this.jbpmContext.getSession());
		List<String> groupNameList = identitySession.getGroupNamesByUserAndGroupType(this.id, "organisation");
		
		List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
		Iterator<String> iter = groupNameList.iterator();
		while (iter.hasNext())
		{
			GroupBean groupBean = new GroupBean();
			groupBean.setJbpmContext(this.jbpmContext);
			groupBean.setId(iter.next());
			groupBeanList.add(groupBean);
		}
		return groupBeanList;
		
	}

	@SuppressWarnings("unchecked")
	public List<TaskInstanceBean> getUserTaskInstanceBeanList()
	{
		List<TaskInstance> taskList = jbpmContext.getTaskList(this.id);
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator<TaskInstance> iter = taskList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = new TaskInstanceBean();
			taskInstanceBean.setTaskInstance(iter.next());
			if (! taskInstanceBean.isEnded())
			{
				taskInstanceBeanList.add(taskInstanceBean);
			}
		}
		return taskInstanceBeanList;
	}
	
	@SuppressWarnings("unchecked")
	public List<TaskInstanceBean> getGroupTaskInstanceBeanList()
	{

		IdentitySession identitySession = new IdentitySession(this.jbpmContext.getSession());
		
		List<TaskInstance> taskList =  jbpmContext.getGroupTaskList(
		    identitySession.getGroupNamesByUserAndGroupType(this.id, "organisation")
		);
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator<TaskInstance> iter = taskList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = new TaskInstanceBean();
			taskInstanceBean.setTaskInstance(iter.next());
			if (! taskInstanceBean.isEnded())
			{
				taskInstanceBeanList.add(taskInstanceBean);
			}
		}
		return taskInstanceBeanList;
	}
		
}
