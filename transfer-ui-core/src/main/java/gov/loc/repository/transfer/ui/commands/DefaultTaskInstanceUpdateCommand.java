package gov.loc.repository.transfer.ui.commands;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.controllers.TaskInstanceController;
import gov.loc.repository.transfer.ui.controllers.VariableUpdateHelper;
import gov.loc.repository.transfer.ui.dao.WorkflowDao;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.VariableBean;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultTaskInstanceUpdateCommand implements
		TaskInstanceUpdateCommand {

	protected static final Log log = LogFactory.getLog(DefaultTaskInstanceUpdateCommand.class);
	
	protected TaskInstanceBean taskInstanceBean;
	protected HttpServletRequest request;
	protected Map<String,Object> additionalParameterMap = new HashMap<String,Object>();
	protected ModelAndView mav;
	protected PermissionsHelper permissionsHelper;
	protected WorkflowDao dao;
	protected WorkflowBeanFactory factory;
	
	public void setWorkflowBeanFactory(WorkflowBeanFactory factory)
	{
		this.factory = factory;
	}

	public void setWorkflowDao(WorkflowDao dao)
	{
		this.dao = dao;
	}
	
	public void setTaskInstanceBean(TaskInstanceBean taskInstanceBean) {
		this.taskInstanceBean = taskInstanceBean;
	}

	public void setModelAndView(ModelAndView mav) {
		this.mav = mav;		
	}
	
	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}
	
	public void setPermissionsHelper(PermissionsHelper permissionsHelper) {
		this.permissionsHelper = permissionsHelper;
	}
	
	public void preprocessPut() throws Exception {
		
	}
	
	@SuppressWarnings("unchecked")
	public void bindPut() throws Exception {
		//Updating task's user
		UserBean userBean = null;
		if (request.getParameterMap().containsKey(UIConstants.PARAMETER_USER))
		{
			log.debug("Updating user");
			if (! permissionsHelper.canUpdateTaskInstanceUser())
			{
				mav.setError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			
			if (! taskInstanceBean.canUpdateUserBean())
			{
				mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Unknown user");
				return;				
			}
			
			String userId = request.getParameter(UIConstants.PARAMETER_USER);
			if (! TaskInstanceController.NULL.equals(userId))
			{				
				if (! dao.userBeanExists(userId))
				{
					mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Unknown user");
					return;
				}

				userBean = factory.createUserBean(userId);
			}
			taskInstanceBean.setUserBean(userBean);
		}
		
		if (this.requestUpdatesVariables())
		{
			log.debug("Updating variables");
			//Check that can update
			if (! permissionsHelper.canUpdateTaskInstance(taskInstanceBean)){
				mav.setError(HttpServletResponse.SC_FORBIDDEN);
				return;				
			}
			if (! taskInstanceBean.canUpdate())
			{
				mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transition");
				return;				
			}

			VariableUpdateHelper.update(request, taskInstanceBean);
			for(String key : additionalParameterMap.keySet())
			{
				taskInstanceBean.setVariable(key, additionalParameterMap.get(key));
				log.debug(MessageFormat.format(
				    "Setting variable {0} with value {1}", 
				    key, additionalParameterMap.get(key).toString()
				));				
			}			
		}
		
		//If update transition
		if (this.requestUpdatesTransition())
		{
			log.debug("Updating transition");
			//Check that can update
			if (! permissionsHelper.canUpdateTaskInstance(taskInstanceBean)){
				mav.setError(HttpServletResponse.SC_FORBIDDEN);
				return;				
			}
			if (! taskInstanceBean.canUpdate()){
				mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transition");
				return;				
			}
			
			//Make sure that task has transition
			String transition = request.getParameter(UIConstants.PARAMETER_TRANSITION);
			if (! taskInstanceBean.getTaskBean().hasLeavingTransition(transition))
			{
				mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transition");
				return;
			}
			taskInstanceBean.setTransition(transition);
			log.debug(MessageFormat.format(
			    "Setting transition {0} for task instance {1}", 
			    transition, taskInstanceBean.getId())
			);
			
		}		
	}
			
	public void validatePut()
	{
		List<String> errorList = new ArrayList<String>();
		if (this.requestUpdatesTransition())
		{
			for(VariableBean variableBean : taskInstanceBean.getTaskBean().getVariableBeanList())
			{
				if (variableBean.isRequired())
				{
					if ((! taskInstanceBean.getVariableMap().containsKey(variableBean.getName())) || (taskInstanceBean.getVariableMap().get(variableBean.getName()) == null))
					{
						errorList.add("Required variables are missing: " + variableBean.getName());
					}
				}
			}
		}
		if (! errorList.isEmpty())
		{
			this.taskInstanceBean.setTransition(null);
		}
		mav.addObject("errorList", errorList);
	}	
	
	@SuppressWarnings("unchecked")
	private boolean requestUpdatesVariables()
	{
		if (! this.additionalParameterMap.isEmpty())
		{
			return true;
		}
		return VariableUpdateHelper.requestUpdatesVariables(request);
	}
	
	private boolean requestUpdatesTransition()
	{
		return (request.getParameterMap().containsKey(UIConstants.PARAMETER_TRANSITION) && ! TaskInstanceController.NULL.equals(request.getParameter(UIConstants.PARAMETER_TRANSITION)));		
	}
	
	public void prepareForm() throws Exception {
		
		mav.addObject("formViewName", "default_form");
	}	

	public void prepareInstruction() throws Exception {
	}
}
