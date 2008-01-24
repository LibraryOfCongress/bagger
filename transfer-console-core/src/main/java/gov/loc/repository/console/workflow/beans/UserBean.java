package gov.loc.repository.console.workflow.beans;

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
		
	public List<ProcessDefinitionBean> getProcessDefinitionBeanList()
	{
		IdentitySession identitySession = new IdentitySession(this.jbpmContext.getSession());
		List groupNameList = identitySession.getGroupNamesByUserAndGroupType(this.id, "organisation");
		Iterator iter = groupNameList.iterator();

		Set<String> processDefinitionIdSet = new HashSet<String>();
		Pattern pattern = Pattern.compile("processdefinition\\.(.+)\\.initiate");
		while (iter.hasNext())
		{
			String groupName = (String)iter.next();
			Group group = identitySession.getGroupByName(groupName);
			Iterator permissionsIter = group.getPermissions().iterator();
			while(permissionsIter.hasNext())
			{
				Permission permission = (Permission)permissionsIter.next();
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
			processDefinitionBean.setProcessDefinition(jbpmContext.getGraphSession().findLatestProcessDefinition(processDefinitionId));
			processDefinitionBeanList.add(processDefinitionBean);
		}
		return processDefinitionBeanList;
		
	}
	
	public List<GroupBean> getGroupBeanList()
	{
		IdentitySession identitySession = new IdentitySession(this.jbpmContext.getSession());
		List groupNameList = identitySession.getGroupNamesByUserAndGroupType(this.id, "organisation");
		
		List<GroupBean> groupBeanList = new ArrayList<GroupBean>();
		Iterator iter = groupNameList.iterator();
		while (iter.hasNext())
		{
			GroupBean groupBean = new GroupBean();
			groupBean.setJbpmContext(this.jbpmContext);
			groupBean.setId((String)iter.next());
			groupBeanList.add(groupBean);
		}
		return groupBeanList;
		
	}

	public List<TaskInstanceBean> getUserTaskInstanceBeanList()
	{
		List taskList = jbpmContext.getTaskList(this.id);
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator iter = taskList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = new TaskInstanceBean();
			taskInstanceBean.setTaskInstance((TaskInstance)iter.next());
			if (! taskInstanceBean.isEnded())
			{
				taskInstanceBeanList.add(taskInstanceBean);
			}
		}
		return taskInstanceBeanList;
	}
	
	public List<TaskInstanceBean> getGroupTaskInstanceBeanList()
	{

		IdentitySession identitySession = new IdentitySession(this.jbpmContext.getSession());
		
		List taskList = jbpmContext.getGroupTaskList(identitySession.getGroupNamesByUserAndGroupType(this.id, "organisation"));
		List<TaskInstanceBean> taskInstanceBeanList = new ArrayList<TaskInstanceBean>();
		Iterator iter = taskList.iterator();
		while (iter.hasNext())
		{
			TaskInstanceBean taskInstanceBean = new TaskInstanceBean();
			taskInstanceBean.setTaskInstance((TaskInstance)iter.next());
			if (! taskInstanceBean.isEnded())
			{
				taskInstanceBeanList.add(taskInstanceBean);
			}
		}
		return taskInstanceBeanList;
	}
		
}
