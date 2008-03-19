package gov.loc.repository.transfer.ui.controllers;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.jbpm.JbpmContext;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.commands.DefaultTaskInstanceUpdateCommand;
import gov.loc.repository.transfer.ui.commands.TaskInstanceUpdateCommand;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;
import gov.loc.repository.transfer.ui.model.TaskInstanceHelper;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.UserHelper;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;

@Controller
public class TaskInstanceController extends AbstractRestController {
	
	protected static final Log log = LogFactory.getLog(TaskInstanceController.class);
	
	public static final String NULL = "null";	

	private TaskInstanceUpdateCommand defaultCommand 
	    = new DefaultTaskInstanceUpdateCommand();
	private Map<String,TaskInstanceUpdateCommand> commandMap 
	    = new HashMap<String, TaskInstanceUpdateCommand>();
		
	@Override
	public String getUrlParameterDescription() {
		return "taskinstance/{taskInstanceId}\\.{format}";
	}
	
	@Override
	@RequestMapping("/taskinstance/*.*")
	public ModelAndView handleRequest(
	        HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		return this.handleRequestInternal(request, response);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initApplicationContext() throws BeansException {
		ApplicationContext applicationContext = 
		    this.getApplicationContext();
		Map< String, TaskInstanceUpdateCommandMap< String,  TaskInstanceUpdateCommand>> beanMap =
		        applicationContext.getBeansOfType(TaskInstanceUpdateCommandMap.class);
		for(TaskInstanceUpdateCommandMap<String, TaskInstanceUpdateCommand> map : beanMap.values()){
			this.commandMap.putAll(map);
		}
		super.initApplicationContext();
	}
	
	public void setDefaultTaskInstanceUpdateCommand(
	        TaskInstanceUpdateCommand defaultCommand ) {
		this.defaultCommand = defaultCommand;
	}
	
	private TaskInstanceUpdateCommand getTaskInstanceUpdateFormCommand(
	        TaskInstanceBean taskInstanceBean) throws Exception {
		TaskInstanceUpdateCommand command = null;
		for(String pattern : this.commandMap.keySet()) {
			String[] patternArray = pattern.split("\\.");
			if (patternArray.length != 2) {
				throw new Exception("Invalid pattern: " + pattern);
			}
			if ( PatternMatchUtils.simpleMatch(
			        patternArray[0],
			        taskInstanceBean.getProcessInstanceBean().getProcessDefinitionBean().getId() ) 
			    && PatternMatchUtils.simpleMatch(
			        patternArray[1], 
			        taskInstanceBean.getTaskBean().getName() )
			){
				command = this.commandMap.get(pattern);
				break;
			}
		}
		if (command == null) {
			if (this.defaultCommand == null){
				throw new Exception("Default task instance update form command not configured.");
			}
			command = this.defaultCommand;
		}
		return command;
	}
	
	@Override
	protected void handleIndex(
	        HttpServletRequest request, 
	        ModelAndView mav,
			JbpmContext jbpmContext, 
			PermissionsHelper permissionsHelper, 
			Map<String, String> urlParameterMap) throws Exception 
	{
		mav.addObject("contextPath", request.getContextPath());
		if (request.getUserPrincipal() == null) {
			mav.setViewName("redirect:/login/login.html");
			return;
		}
		mav.setViewName("tasks");
		
		UserBean userBean = new UserBean();
		userBean.setId(request.getUserPrincipal().getName());
		userBean.setJbpmContext(jbpmContext);
		
		mav.addObject("groupTaskInstanceBeanList", userBean.getGroupTaskInstanceBeanList());
		mav.addObject("userTaskInstanceBeanList", userBean.getUserTaskInstanceBeanList());
		mav.addObject("currentUser", request.getUserPrincipal().getName());
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap) throws Exception {
		//If there is no taskinstanceid in urlParameterMap then 404
		if (! urlParameterMap.containsKey(UIConstants.PARAMETER_TASKINSTANCEID)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		//Otherwise handle taskinstanceid
		String taskInstanceId = urlParameterMap.get(UIConstants.PARAMETER_TASKINSTANCEID);
		if (! TaskInstanceHelper.exists(taskInstanceId, jbpmContext)){
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		TaskInstanceBean taskInstanceBean = 
		    TaskInstanceHelper.getTaskInstanceBean(
		        taskInstanceId, 
		        jbpmContext
		    );
		TaskInstanceUpdateCommand command = 
		    this.getTaskInstanceUpdateFormCommand(taskInstanceBean);
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);
		command.setModelAndView(mav);
		command.setPermissionsHelper(permissionsHelper);

		//This will give us access to the task instance details
		mav.addObject("taskInstanceBean", taskInstanceBean);
		
		mav.addObject("userBeanList", UserHelper.getUserBeanList(jbpmContext));
						
		if (permissionsHelper.canUpdateTaskInstance(taskInstanceBean) && taskInstanceBean.canUpdate()) {			
			command.prepareForm();
			command.prepareInstruction();						
		}		
						
		mav.setViewName("taskinstance");
		
	}
		
	@Override
	protected void handlePut(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext,
	         PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap ) throws Exception {
		if (! urlParameterMap.containsKey(UIConstants.PARAMETER_TASKINSTANCEID)) {			
			mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}

		String taskInstanceId = urlParameterMap.get(UIConstants.PARAMETER_TASKINSTANCEID);
		if (! TaskInstanceHelper.exists(taskInstanceId, jbpmContext)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		TaskInstanceBean taskInstanceBean = 
		    TaskInstanceHelper.getTaskInstanceBean(
		        taskInstanceId, 
		        jbpmContext
		    );

		TaskInstanceUpdateCommand command = 
		    this.getTaskInstanceUpdateFormCommand(taskInstanceBean);
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);
		command.setModelAndView(mav);
		command.setJbpmContext(jbpmContext);
		command.setPermissionsHelper(permissionsHelper);
		command.preprocessPut();
		command.bindPut();
				
		//Check to make sure an error wasn't reported by binding
		if (mav.getStatusCode() != null) { return; }
		
		//This will add an errorList to mav
		command.validatePut();
		
		taskInstanceBean.save();

		if (taskInstanceBean.isEnded()) {
			mav.setViewName("redirect:/");
		}else{
			this.handleGet(
			    request, 
			    mav, 
			    jbpmContext, 
			    permissionsHelper, urlParameterMap
			);
		}
	}
		
}
