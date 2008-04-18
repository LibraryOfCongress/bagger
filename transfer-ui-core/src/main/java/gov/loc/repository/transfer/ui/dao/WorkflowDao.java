package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.model.ProcessDefinitionBean;
import gov.loc.repository.transfer.ui.model.ProcessInstanceBean;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;
import gov.loc.repository.transfer.ui.model.TokenBean;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.identity.User;
import org.jbpm.identity.hibernate.IdentitySession;

public class WorkflowDao {

	private JbpmContext jbpmContext;
	private WorkflowBeanFactory factory;
	
	public void setJbpmContext(JbpmContext jbpmContext)
	{
		this.jbpmContext = jbpmContext;
	}

	public void setWorkflowBeanFactory(WorkflowBeanFactory factory)
	{
		this.factory = factory;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessDefinitionBean> getProcessDefinitionBeanList()
	{
		List<ProcessDefinitionBean> processDefinitionList = new ArrayList<ProcessDefinitionBean>();
		Iterator<ProcessDefinition> iter = jbpmContext.getGraphSession().findLatestProcessDefinitions().iterator();
		while(iter.hasNext())
		{
			
			ProcessDefinitionBean processDefinitionBean = this.factory.createProcessDefinitionBean(iter.next());
			processDefinitionList.add(processDefinitionBean);
		}
		return processDefinitionList;
		
	}
	
	public ProcessDefinitionBean getProcessDefinitionBean(String id) throws Exception
	{
		ProcessDefinition definition = jbpmContext.getGraphSession().findLatestProcessDefinition(id);
		if (definition == null)
		{
			throw new Exception(MessageFormat.format("Process definition {0} not found", id));
		}
		return this.factory.createProcessDefinitionBean(definition);
	}

	@SuppressWarnings("unchecked")
	public List<ProcessInstanceBean> getProcessInstanceBeanList()
	{
		return this.getProcessInstanceBeanList(true, true);
	}

	@SuppressWarnings("unchecked")
	public List<ProcessInstanceBean> getActiveProcessInstanceBeanList()
	{
		return this.getProcessInstanceBeanList(true, false);
	}
		
	public List<ProcessInstanceBean> getSuspendedProcessInstanceBeanList()
	{
		return this.getProcessInstanceBeanList(false, true);
	}
	
	@SuppressWarnings("unchecked")
	private List<ProcessInstanceBean> getProcessInstanceBeanList(boolean includeActive, boolean includeSuspended)
	{
		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List<ProcessDefinition> processDefinitionList = jbpmContext.getGraphSession().findAllProcessDefinitions();
		Iterator<ProcessDefinition> definitionIter = processDefinitionList.iterator();
		while(definitionIter.hasNext())
		{
			ProcessDefinition definition = definitionIter.next();
			List<ProcessInstance> processInstanceList = jbpmContext.getGraphSession().findProcessInstances(definition.getId());
			Iterator<ProcessInstance> instanceIter = processInstanceList.iterator();
			while (instanceIter.hasNext())
			{
				ProcessInstanceBean processInstanceBean = this.factory.createProcessInstanceBean(instanceIter.next());
				if (processInstanceBean.isEnded())
				{
					continue;
				}
				if (includeActive && ! processInstanceBean.isSuspended())
				{
					processInstanceBeanList.add(processInstanceBean);
				}
				else if (includeSuspended && processInstanceBean.isSuspended())
				{
					processInstanceBeanList.add(processInstanceBean);
				}
				
			}
		}
		return processInstanceBeanList;
		
	}
	
	public ProcessInstanceBean getProcessInstanceBean(String id)
	{
		ProcessInstance processInstance = jbpmContext.getProcessInstance(Long.parseLong(id));
		if (processInstance == null)
		{
			return null;
		}
		return this.factory.createProcessInstanceBean(processInstance);
	}
	
	public TaskInstanceBean getTaskInstanceBean(String id)
	{
		try
		{
			return this.factory.createTaskInstanceBean(jbpmContext.getTaskInstance(Long.parseLong(id)));
		}
		catch(Exception ex)
		{			
		}
		return null;
	}
	
	public TokenBean getTokenBean(String id)
	{
		Token token = jbpmContext.getToken(Long.parseLong(id));
		if (token == null) {
			return null;
		}
		return this.factory.createTokenBean(token);
	}

	@SuppressWarnings("unchecked")
	public List<UserBean> getUserBeanList()
	{
		IdentitySession identitySession = new IdentitySession(jbpmContext.getSession());
		return toUserBeanList(identitySession.getUsers().iterator(), jbpmContext);
	}
	
	public boolean userBeanExists(String id)
	{
		IdentitySession identitySession = new IdentitySession(jbpmContext.getSession());
		if (identitySession.getUserByName(id) != null) {
			return true;
		}
		return false;
	}
		
	private List<UserBean> toUserBeanList(Iterator<User> iter, JbpmContext jbpmContext)
	{
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		while(iter.hasNext())
		{
			UserBean userBean = this.factory.createUserBean(iter.next().getName());
			userBeanList.add(userBean);
		}

		return userBeanList;
	}
	
	public void save(TokenBean tokenBean)
	{
		this.jbpmContext.save(tokenBean.getToken());
	}
	
	public void save(ProcessInstanceBean processInstanceBean)
	{
		this.jbpmContext.save(processInstanceBean.getProcessInstance());	
	}
	
	public void save(TaskInstanceBean taskInstanceBean)
	{
		if(taskInstanceBean.isEnded())
		{
			return;
		}
		
		if (taskInstanceBean.getTaskInstance().getTask().getTaskController() != null)
		{		
			taskInstanceBean.getTaskInstance().getTask().getTaskController().submitParameters(taskInstanceBean.getTaskInstance());		
		}
		
		if (taskInstanceBean.getTransition() != null)
		{
			taskInstanceBean.getTaskInstance().end(taskInstanceBean.getTransition());
		}
		
		this.jbpmContext.save(taskInstanceBean.getTaskInstance());
	}
	

	
}
