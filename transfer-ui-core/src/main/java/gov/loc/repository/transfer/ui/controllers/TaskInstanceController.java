package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.commands.DefaultTaskInstanceUpdateCommand;
import gov.loc.repository.transfer.ui.commands.TaskInstanceUpdateCommand;
import gov.loc.repository.transfer.ui.dao.WorkflowDao;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;

import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Controller;
import org.springframework.util.PatternMatchUtils;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class TaskInstanceController extends AbstractRestController {
	
	protected static final Log log = LogFactory.getLog(TaskInstanceController.class);
	public static final String NULL = "null";	
		
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
	
	@Override
	protected void handleIndex(
	        HttpServletRequest request, 
	        ModelAndView mav,
			WorkflowBeanFactory factory, 
			WorkflowDao dao, 
			PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap) throws Exception 
	{   
		mav.setViewName("tasks");
		//Add the user to the view
		UserBean userBean = factory.createUserBean(request.getUserPrincipal().getName());		
		mav.addObject("userBean", userBean);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        WorkflowBeanFactory factory, 
	        WorkflowDao dao, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap) throws Exception 
	{
		//If there is no taskinstanceid in urlParameterMap then 404
		if (! urlParameterMap.containsKey(UIConstants.PARAMETER_TASKINSTANCEID)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		//Otherwise handle taskinstanceid
		String taskId = urlParameterMap.get(UIConstants.PARAMETER_TASKINSTANCEID);
		TaskInstanceBean taskInstanceBean = dao.getTaskInstanceBean(taskId);
		if (taskInstanceBean == null){
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}	
		
		//Configure the view
		mav.setViewName("taskinstance");
		mav.addObject("taskInstanceBean", taskInstanceBean);
		mav.addObject("userBeanList", dao.getUserBeanList());
		    
		TaskInstanceUpdateCommand command = 
		    getTaskInstanceUpdateFormCommand(taskInstanceBean);
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);
		command.setModelAndView(mav);
		command.setPermissionsHelper(permissionsHelper);
		if (permissionsHelper.canUpdateTaskInstance(taskInstanceBean) && taskInstanceBean.canUpdate()) {			
			command.prepareForm();
			command.prepareInstruction();						
		}	
	}
		
	@Override
	protected void handlePut(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        WorkflowBeanFactory factory,
	        WorkflowDao dao, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap ) throws Exception 
	{
		if (! urlParameterMap.containsKey(UIConstants.PARAMETER_TASKINSTANCEID)) {			
			mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		String taskInstanceId = urlParameterMap.get(UIConstants.PARAMETER_TASKINSTANCEID);
		TaskInstanceBean taskInstanceBean = dao.getTaskInstanceBean(taskInstanceId);
		if (taskInstanceBean == null) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		TaskInstanceUpdateCommand command = this.getTaskInstanceUpdateFormCommand(taskInstanceBean);
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);
		command.setModelAndView(mav);
		command.setWorkflowDao(dao);
		command.setWorkflowBeanFactory(factory);
		command.setPermissionsHelper(permissionsHelper);
		command.preprocessPut();
		command.bindPut();
		//Check to make sure an error wasn't reported by binding
		if (mav.getStatusCode() != null) { return; }
		//This will add an errorList to mav
		command.validatePut();
		dao.save(taskInstanceBean);
		if (taskInstanceBean.isEnded()) {
			mav.setViewName("redirect:/");
		}else{
			this.handleGet(
			    request, 
			    mav, 
			    factory, 
			    dao, 
			    permissionsHelper, urlParameterMap
			);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void initApplicationContext() throws BeansException {
		ApplicationContext applicationContext = this.getApplicationContext();
		Map<String, ProcessDefinitionConfiguration> beanMap = applicationContext.getBeansOfType(ProcessDefinitionConfiguration.class);
		for(ProcessDefinitionConfiguration processDefinitionConfiguration : beanMap.values())
		{
			this.commandMap.putAll(processDefinitionConfiguration.getTaskInstanceUpdateCommandMap());
		}
		super.initApplicationContext();
	}
	
	private TaskInstanceUpdateCommand defaultCommand 
	    = new DefaultTaskInstanceUpdateCommand();
	
	private Map<String,TaskInstanceUpdateCommand> commandMap 
	    = new HashMap<String, TaskInstanceUpdateCommand>();
	    
	public void setDefaultTaskInstanceUpdateCommand(
	        TaskInstanceUpdateCommand defaultCommand ) {
		this.defaultCommand = defaultCommand;
	}
	
	private TaskInstanceUpdateCommand getTaskInstanceUpdateFormCommand(TaskInstanceBean taskInstanceBean) throws Exception 
	{
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
			        taskInstanceBean.getTaskBean().getId() )){
				command = this.commandMap.get(pattern);
				break;
			}
		}
		if (command == null) {
			if (this.defaultCommand == null){
				throw new Exception("Default task instance update form command not configured.");
			} command = this.defaultCommand;
		} return command;
	}	
}
