package gov.loc.repository.transfer.ui.model;

import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

import org.jbpm.JbpmContext;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.Comment;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.graph.exe.Token;
import org.jbpm.taskmgmt.def.Task;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.context.MessageSource;

public class WorkflowBeanFactory {
	protected JbpmContext jbpmContext;
	private MessageSource messageSource;
	private ServiceRequestDAO serviceRequestDAO;
	
	public void setJbpmContext(JbpmContext jbpmContext) {
		this.jbpmContext = jbpmContext;
	}
	
	public void setMessageSource(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}
	
	public void setServiceRequestDAO(ServiceRequestDAO dao)
	{
		this.serviceRequestDAO = dao;
	}
	
	public CommentBean createCommentBean(Comment comment)
	{
		CommentBean bean = this.createWorkflowBean(CommentBean.class);
		bean.setComment(comment);
		return bean;
	}

	public GroupBean createGroupBean(String id)
	{
		GroupBean bean = this.createWorkflowBean(GroupBean.class);
		bean.setId(id);
		return bean;
	}

	public ProcessInstanceBean createProcessInstanceBean(ProcessInstance processInstance)
	{
		ProcessInstanceBean bean = this.createWorkflowBean(ProcessInstanceBean.class);
		bean.setProcessInstance(processInstance);
		return bean;
	}

	public ProcessInstanceBean createNewProcessInstanceBean(ProcessDefinitionBean processDefinitionBean)
	{
		ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinitionBean.getId());
		processInstance.getRootToken().signal();
		
		ProcessInstanceBean bean = this.createWorkflowBean(ProcessInstanceBean.class);
		bean.setProcessInstance(processInstance);
		return bean;		
	}
		
	public ProcessDefinitionBean createProcessDefinitionBean(ProcessDefinition processDefinition)
	{
		ProcessDefinitionBean bean = this.createWorkflowBean(ProcessDefinitionBean.class);
		bean.setProcessDefinition(processDefinition);
		return bean;
	}
	
	public TaskBean createTaskBean(Task task)
	{
		TaskBean bean = this.createWorkflowBean(TaskBean.class);
		bean.setTask(task);
		return bean;
	}

	public TaskInstanceBean createTaskInstanceBean(TaskInstance taskInstance)
	{
		TaskInstanceBean bean = this.createWorkflowBean(TaskInstanceBean.class);
		bean.setTaskInstance(taskInstance);
		return bean;
	}
	
	public NodeBean createNodeBean(Node node)
	{
		NodeBean bean = this.createWorkflowBean(NodeBean.class);
		bean.setNode(node);
		return bean;
	}
	
	public TokenBean createTokenBean(Token token)
	{
		TokenBean bean = this.createWorkflowBean(TokenBean.class);
		bean.setToken(token);
		return bean;
	}
	
	public UserBean createUserBean(String id)
	{
		UserBean bean = this.createWorkflowBean(UserBean.class);
		bean.setId(id);
		return bean;
	}
	
	public VariableBean createVariableBean(VariableAccess variable, TaskBean taskBean)
	{
		VariableBean bean = this.createWorkflowBean(VariableBean.class);
		bean.setVariable(variable);
		bean.setTaskBean(taskBean);
		return bean;
	}
	
	public TransitionBean createTransitionBean(Transition transition)
	{
		TransitionBean bean = this.createWorkflowBean(TransitionBean.class);
		bean.setTransition(transition);
		return bean;
	}
	
	public <T extends AbstractWorkflowBean> T createWorkflowBean(Class<T> beanType) {
		try
		{
			AbstractWorkflowBean bean = (AbstractWorkflowBean)beanType.newInstance();
			bean.setJbpmContext(jbpmContext);
			bean.setMessageSource(messageSource);
			bean.setWorkflowBeanFactory(this);
			bean.setServiceRequestDAO(serviceRequestDAO);
			return beanType.cast(bean);
		}
		catch(Exception ex)
		{
			throw new RuntimeException(ex);
		}
	}
}
