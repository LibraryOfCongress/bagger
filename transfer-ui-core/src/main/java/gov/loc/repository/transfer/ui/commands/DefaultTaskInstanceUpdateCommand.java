package gov.loc.repository.transfer.ui.commands;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.controllers.TaskInstanceController;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.UserHelper;
import gov.loc.repository.transfer.ui.model.VariableBean;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;

public class DefaultTaskInstanceUpdateCommand implements
		TaskInstanceUpdateCommand {

	protected static final Log log = LogFactory.getLog(DefaultTaskInstanceUpdateCommand.class);
	
	protected TaskInstanceBean taskInstanceBean;
	protected HttpServletRequest request;
	protected JbpmContext jbpmContext;
	protected Map<String,Object> additionalParameterMap = new HashMap<String,Object>();
	protected ModelAndView mav;
	
	public void setJbpmContext(JbpmContext jbpmContext) {
		this.jbpmContext = jbpmContext;
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
	
	
	public void preprocessPut() throws Exception {
		
	}
	
	@SuppressWarnings("unchecked")
	public void bindPut() throws Exception {
		//Updating task's user
		if (request.getParameterMap().containsKey(UIConstants.PARAMETER_USER))
		{
			log.debug("Updating user");
			if (! this.canUpdateUser())
			{
				mav.setError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			String userId = request.getParameter(UIConstants.PARAMETER_USER);
			UserBean userBean = null;
			if (! TaskInstanceController.NULL.equals(userId))
			{
				if (! UserHelper.exists(userId, jbpmContext))
				{
					mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Unknown user");
					return;
				}

				userBean = new UserBean();
				userBean.setJbpmContext(jbpmContext);
				userBean.setId(userId);
			}
			taskInstanceBean.setUserBean(userBean);
		}
		
		if (this.requestUpdatesVariables())
		{
			log.debug("Updating variables");
			//Check that can update
			if (! this.canUpdateTaskInstance())
			{
				mav.setError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			Iterator iter = request.getParameterMap().keySet().iterator();
			while(iter.hasNext())
			{
				String key = (String)iter.next();
				if (key.startsWith(TaskInstanceController.VARIABLE_PREFIX))
				{
					String extractedKey = key.substring(TaskInstanceController.VARIABLE_PREFIX.length());
					String value = request.getParameter(key);
					if (value != null && value.length() == 0)
					{
						value = null;
					}
					taskInstanceBean.setVariable(extractedKey, value);					
					
				}
			}
			for(String key : additionalParameterMap.keySet())
			{
				taskInstanceBean.setVariable(key, additionalParameterMap.get(key));
				log.debug(MessageFormat.format("Setting variable {0} with value {1}", key, additionalParameterMap.get(key).toString()));				
			}

		}
		
		//If update transition
		if (this.requestUpdatesTransition())
		{
			log.debug("Updating transition");
			//Check that can update
			if (! this.canUpdateTaskInstance())
			{
				mav.setError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			//Make sure that task has transition
			String transition = request.getParameter(UIConstants.PARAMETER_TRANSITION);
			if (! taskInstanceBean.getTaskBean().getLeavingTransitionList().contains(transition))
			{
				mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transition");
				return;
			}
			taskInstanceBean.setTransition(transition);
			log.debug(MessageFormat.format("Setting transition {0} for task instance {1}", transition, taskInstanceBean.getId()));
			
		}		
	}
	
	public boolean canUpdateUser()
	{
		//Anyone who is logged in can re-assign task
		if (request.getUserPrincipal() != null && ! taskInstanceBean.isEnded())
		{
			return true;
		}
		return false;
		
	}
	
	public boolean canUpdateTaskInstance()
	{
		if (request.getUserPrincipal() != null && taskInstanceBean.getUserBean() != null && request.getUserPrincipal().getName().equals(taskInstanceBean.getUserBean().getId()) && ! taskInstanceBean.isEnded())
		{
			return true;
		}
		return false;
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
		
		Iterator<String> keyIter = request.getParameterMap().keySet().iterator();
		while(keyIter.hasNext())
		{
			if (keyIter.next().startsWith(TaskInstanceController.VARIABLE_PREFIX))
			{
				return true;
			}
		}
		return false;
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
