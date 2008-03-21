package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.commands.DefaultTaskInstanceUpdateCommand;
import gov.loc.repository.transfer.ui.commands.TaskInstanceUpdateCommand;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;
import gov.loc.repository.transfer.ui.model.TaskInstanceHelper;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.UserHelper;
import gov.loc.repository.transfer.ui.models.Comment;
import gov.loc.repository.transfer.ui.models.ProcessDef;
import gov.loc.repository.transfer.ui.models.Task;
import gov.loc.repository.transfer.ui.models.User;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
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
		//Add the user to the view
		Map params = new HashMap();
	    params.put( "name", request.getUserPrincipal().getName() );
	    User user = userDao.findByUserName(params);
		mav.addObject("user", user);
		//Add the userTasks to the view
		params = new HashMap();
	    params.put( "name", user.getName() );
	    List<Task> userTasks = taskDao.findCurrentByUserName(params);
		mav.addObject("userTasks", userTasks);
		//Add the groupTasks to the view
		params = new HashMap();
	    params.put( "groupNames", user.getGroupNames() );
	    List<Task> groupTasks = taskDao.findCurrentByGroupNames(params);
		mav.addObject("groupTasks", groupTasks);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        PermissionsHelper permissionsHelper, 
	        Map<String, String> urlParameterMap) throws Exception 
	{
		//If there is no taskinstanceid in urlParameterMap then 404
		if (! urlParameterMap.containsKey(UIConstants.PARAMETER_TASKINSTANCEID)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		//Otherwise handle taskinstanceid
		String taskId = urlParameterMap.get(UIConstants.PARAMETER_TASKINSTANCEID);
		if (! TaskInstanceHelper.exists(taskId, jbpmContext)){
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}	
		
		//Configure the view
		mav.setViewName("taskinstance");
        Task task                   = taskDao.find(Long.parseLong(taskId));
		mav.addObject( "task",      task );
        List<User> users            = userDao.findAll();
		mav.addObject( "users",     users );
        Map params = new HashMap();
	    params.put( "processId",    task.getProcessIdRef() );
        List<Comment> comments      = commentDao.findAllByProcessId(params);
		mav.addObject( "comments",  comments );
        ProcessDef processDef       = processDefDao.findByProcessId(params);			
		
		//Trying to refactor the *Beans out right now the command
		//still holds a reference to it
		TaskInstanceBean taskInstanceBean = 
		    TaskInstanceHelper.getTaskInstanceBean(
		        taskId, 
		        jbpmContext
		    );
		    
		mav.addObject("taskInstanceBean", taskInstanceBean);
		mav.addObject("userBeanList", UserHelper.getUserBeanList(jbpmContext));
		TaskInstanceUpdateCommand command = 
		    getTaskInstanceUpdateFormCommand(
		        task.getName(), 
		        processDef.getId().toString()
		    );
		command.setTaskInstanceBean(taskInstanceBean);
		command.setRequest(request);
		command.setModelAndView(mav);
		command.setPermissionsHelper(permissionsHelper);
		if (permissionsHelper.canUpdateTask(task.getAssignedUserName()) && task.isUpdateable()) {			
			command.prepareForm();
			command.prepareInstruction();						
		}	
	}
		
	@Override
	protected void handlePut(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext,
	        PermissionsHelper permissionsHelper, 
	        Map<String, String> urlParameterMap ) throws Exception 
	{
		if (! urlParameterMap.containsKey(UIConstants.PARAMETER_TASKINSTANCEID)) {			
			mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return;
		}
		String taskId = urlParameterMap.get(UIConstants.PARAMETER_TASKINSTANCEID);
		if (! TaskInstanceHelper.exists(taskId, jbpmContext)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		
		Task task                   = taskDao.find(Long.parseLong(taskId));
		Map params                  = new HashMap();
	    params.put( "processId",    task.getProcessIdRef() );
        ProcessDef processDef       = processDefDao.findByProcessId(params);
        
		TaskInstanceBean taskInstanceBean = 
		    TaskInstanceHelper.getTaskInstanceBean(
		        taskId, 
		        jbpmContext
		    );
		TaskInstanceUpdateCommand command = 
		    this.getTaskInstanceUpdateFormCommand(
		        task.getName(),
		        processDef.getId().toString()
		    );
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
			    permissionsHelper, 
			    urlParameterMap
			);
		}
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
		} super.initApplicationContext();
	}
	
	private TaskInstanceUpdateCommand defaultCommand 
	    = new DefaultTaskInstanceUpdateCommand();
	
	private Map<String,TaskInstanceUpdateCommand> commandMap 
	    = new HashMap<String, TaskInstanceUpdateCommand>();
	    
	public void setDefaultTaskInstanceUpdateCommand(
	        TaskInstanceUpdateCommand defaultCommand ) {
		this.defaultCommand = defaultCommand;
	}
	
	private TaskInstanceUpdateCommand getTaskInstanceUpdateFormCommand(
	        String taskName,
	        String processDefId ) throws Exception 
	{
		TaskInstanceUpdateCommand command = null;
		for(String pattern : this.commandMap.keySet()) {
			String[] patternArray = pattern.split("\\.");
			if (patternArray.length != 2) {
				throw new Exception("Invalid pattern: " + pattern);
			}
			if ( PatternMatchUtils.simpleMatch(
			        patternArray[0],
			        processDefId ) 
			    && PatternMatchUtils.simpleMatch(
			        patternArray[1], 
			        taskName )){
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
