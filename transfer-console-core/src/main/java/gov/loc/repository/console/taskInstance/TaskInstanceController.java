package gov.loc.repository.console.taskInstance;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.JbpmContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.servlet.ModelAndView;

import gov.loc.repository.console.AbstractRestController;
import gov.loc.repository.console.workflow.beans.TaskInstanceBean;
import gov.loc.repository.console.workflow.beans.TaskInstanceHelper;
import gov.loc.repository.console.workflow.beans.UserBean;
import gov.loc.repository.console.workflow.beans.UserHelper;

public class TaskInstanceController extends AbstractRestController {
	
	public static final String PARAMETER_TASKINSTANCEID = "taskInstanceId";
	public static final String NULL = "null";
	public static final String VARIABLE_PREFIX = "variable.";
	public static final String PARAMETER_TRANSITION = "transition";

	private TaskInstanceUpdateCommand defaultCommand;
	private Map<String,TaskInstanceUpdateCommand> commandMap = new HashMap<String, TaskInstanceUpdateCommand>();
	
	/*
	public void setTaskInstanceUpdateCommandMap(Map<String,TaskInstanceUpdateCommand> commandMap)
	{
		this.commandMap = commandMap;
	}
	*/
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initApplicationContext() throws BeansException {
		ApplicationContext applicationContext = this.getApplicationContext();
		Map<String,TaskInstanceUpdateCommandMap<String, TaskInstanceUpdateCommand>> beanMap = applicationContext.getBeansOfType(TaskInstanceUpdateCommandMap.class);
		for(TaskInstanceUpdateCommandMap<String, TaskInstanceUpdateCommand> map : beanMap.values())
		{
			this.commandMap.putAll(map);
		}
		super.initApplicationContext();
	}
	
	public void setDefaultTaskInstanceUpdateCommand(TaskInstanceUpdateCommand defaultCommand)
	{
		this.defaultCommand = defaultCommand;
	}
	
	private TaskInstanceUpdateCommand getTaskInstanceUpdateFormCommand(TaskInstanceBean taskInstanceBean) throws Exception
	{
		TaskInstanceUpdateCommand command = null;
		for(String pattern : this.commandMap.keySet())
		{
			String[] patternArray = pattern.split("\\.");
			if (patternArray.length != 2)
			{
				throw new Exception("Invalid pattern: " + pattern);
			}
			if (PatternMatchUtils.simpleMatch(patternArray[0], taskInstanceBean.getProcessInstanceBean().getProcessDefinitionBean().getId()) && PatternMatchUtils.simpleMatch(patternArray[1], taskInstanceBean.getTaskBean().getName()))
			{
				command = this.commandMap.get(pattern);
				break;
			}
		}
		if (command == null)
		{
			if (this.defaultCommand == null)
			{
				throw new Exception("Default task instance update form command not configured.");
			}
			command = this.defaultCommand;
		}
		return command;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleGet(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String, String> urlParameterMap) throws Exception {
		Map model = new HashMap();
		//If there is no taskinstanceid in urlParameterMap then list
		if (! urlParameterMap.containsKey(PARAMETER_TASKINSTANCEID))
		{
			//If there is a user specified then limit
			if (request.getParameterMap().containsKey(PARAMETER_USER))
			{
				UserBean userBean = new UserBean();
				userBean.setId(request.getParameter(PARAMETER_USER));
				userBean.setJbpmContext(jbpmContext);
				model.put("groupTaskInstanceBeanList", userBean.getGroupTaskInstanceBeanList());
				model.put("userTaskInstanceBeanList", userBean.getUserTaskInstanceBeanList());
				return new ModelAndView(".taskinstance.list", "model", model);
			}
			//In jbpm can't (easily) get a list of all task instances, so
			response.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		//Otherwise handle taskinstanceid
		String taskInstanceId = urlParameterMap.get(PARAMETER_TASKINSTANCEID);
		if (! TaskInstanceHelper.exists(taskInstanceId, jbpmContext))
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		TaskInstanceBean taskInstanceBean = TaskInstanceHelper.getTaskInstanceBean(taskInstanceId, jbpmContext);
		TaskInstanceUpdateCommand command = this.getTaskInstanceUpdateFormCommand(taskInstanceBean);
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);

		model.put("taskInstanceBean", taskInstanceBean);
		model.put("canUpdateUser", command.canUpdateUser());
		if (command.canUpdateUser())
		{
			//This could be in the command, but why bother
			model.put("userBeanList", UserHelper.getUserBeanList(jbpmContext));
		}
		
		String taskFormView = null;
		String instructionView = null;
		if (command.canUpdateTaskInstance())
		{			
			ModelAndView formModelAndView = command.prepareForm();
			model.putAll(formModelAndView.getModel());
			taskFormView = formModelAndView.getViewName();

			ModelAndView instructionModelAndView = command.prepareInstruction();
			if (instructionModelAndView != null)
			{
				model.putAll(instructionModelAndView.getModel());
				instructionView = instructionModelAndView.getViewName();
			}
						
		}		
		model.put("taskFormView", taskFormView);
		model.put("instructionView", instructionView);
				
		return new ModelAndView(".taskinstance.item", "model", model);
		
	}
		
	@Override
	protected ModelAndView handlePut(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String, String> urlParameterMap) throws Exception {
		if (! urlParameterMap.containsKey(PARAMETER_TASKINSTANCEID))
		{			
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}

		String taskInstanceId = urlParameterMap.get(PARAMETER_TASKINSTANCEID);
		if (! TaskInstanceHelper.exists(taskInstanceId, jbpmContext))
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		TaskInstanceBean taskInstanceBean = TaskInstanceHelper.getTaskInstanceBean(taskInstanceId, jbpmContext);

		TaskInstanceUpdateCommand command = this.getTaskInstanceUpdateFormCommand(taskInstanceBean);
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);
		command.setResponse(response);
		command.preprocessPut();
		command.bindPut();
				
		//Check to make sure an error wasn't reported by binding
		if (response.isCommitted())
		{
			return null;
		}
		
		String error = command.validatePut();
		if (error != null)
		{
			response.sendError(HttpServletResponse.SC_CONFLICT, error);
			return null;
		}
		
		taskInstanceBean.save();
			
		return this.handleGet(request, response, jbpmContext, urlParameterMap);
	}
		
}
