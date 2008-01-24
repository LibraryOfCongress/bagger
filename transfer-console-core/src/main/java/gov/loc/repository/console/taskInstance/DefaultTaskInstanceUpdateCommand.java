package gov.loc.repository.console.taskInstance;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.springframework.web.servlet.ModelAndView;

import gov.loc.repository.console.workflow.beans.TaskInstanceBean;
import gov.loc.repository.console.workflow.beans.UserBean;
import gov.loc.repository.console.workflow.beans.UserHelper;
import gov.loc.repository.console.workflow.beans.VariableBean;

public class DefaultTaskInstanceUpdateCommand implements
		TaskInstanceUpdateCommand {

	protected static final Log log = LogFactory.getLog(DefaultTaskInstanceUpdateCommand.class);
	
	protected TaskInstanceBean taskInstanceBean;
	protected HttpServletRequest request;
	protected HttpServletResponse response;
	protected JbpmContext jbpmContext;
	protected Map<String,Object> additionalParameterMap = new HashMap<String,Object>();
	
	public void setJbpmContext(JbpmContext jbpmContext) {
		this.jbpmContext = jbpmContext;
	}
	
	public void setTaskInstanceBean(TaskInstanceBean taskInstanceBean) {
		this.taskInstanceBean = taskInstanceBean;

	}

	public void setRequest(HttpServletRequest request)
	{
		this.request = request;
	}
	
	public void setResponse(HttpServletResponse response)
	{
		this.response = response;
	}
	
	public void preprocessPut() throws Exception {
		
	}
	
	public void bindPut() throws Exception {
		//Updating task's user
		if (request.getParameterMap().containsKey(TaskInstanceController.PARAMETER_USER))
		{
			if (! this.canUpdateUser())
			{
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			String userId = request.getParameter(TaskInstanceController.PARAMETER_USER);
			UserBean userBean = null;
			if (! TaskInstanceController.NULL.equals(userId))
			{
				if (! UserHelper.exists(userId, jbpmContext))
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown user");
					return;
				}

				userBean = new UserBean();
				userBean.setJbpmContext(jbpmContext);
				userBean.setId(userId);
			}						
		}
		
		if (this.requestUpdatesVariables())
		{
			//Check that can update
			if (! this.canUpdateTaskInstance())
			{
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
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
					log.debug(MessageFormat.format("Setting variable {0} with value {1} for task instance {2}", extractedKey, value, taskInstanceBean.getId()));
					
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
			//Check that can update
			if (! this.canUpdateTaskInstance())
			{
				response.sendError(HttpServletResponse.SC_FORBIDDEN);
				return;
			}
			//Make sure that task has parameter
			String transition = request.getParameter(TaskInstanceController.PARAMETER_TRANSITION);
			if (! taskInstanceBean.getTaskBean().getLeavingTransitionList().contains(transition))
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Invalid transition");
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
		if (request.getUserPrincipal() != null && request.getUserPrincipal().getName().equals(taskInstanceBean.getUserBean().getId()) && ! taskInstanceBean.isEnded())
		{
			return true;
		}
		return false;
	}
	
	public String validatePut()
	{
		String error = null;
		if (this.requestUpdatesTransition())
		{
			for(VariableBean variableBean : taskInstanceBean.getTaskBean().getVariableBeanList())
			{
				if (variableBean.isRequired())
				{
					if ((! taskInstanceBean.getVariableMap().containsKey(variableBean.getName())) || (taskInstanceBean.getVariableMap().get(variableBean.getName()) == null))
					{
						if (error == null)
						{
							error = "Required variables are missing: " + variableBean.getName();
						}
						else
						{
							error += ", " + variableBean.getName();
						}
					}
				}
			}
		}
		return error;
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
		return (request.getParameterMap().containsKey(TaskInstanceController.PARAMETER_TRANSITION) && ! TaskInstanceController.NULL.equals(request.getParameter(TaskInstanceController.PARAMETER_TRANSITION)));		
	}
	
	public ModelAndView prepareForm() throws Exception {
		return new ModelAndView(".taskinstance.form.default");
	}	

	public ModelAndView prepareInstruction() throws Exception {
		return null;
	}
}
