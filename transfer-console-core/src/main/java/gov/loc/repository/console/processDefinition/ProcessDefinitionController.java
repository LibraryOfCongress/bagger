package gov.loc.repository.console.processDefinition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import gov.loc.repository.console.AbstractRestController;
import gov.loc.repository.console.workflow.beans.ProcessDefinitionBean;
import gov.loc.repository.console.workflow.beans.ProcessDefinitionHelper;
import gov.loc.repository.console.workflow.beans.ProcessInstanceBean;
import gov.loc.repository.console.workflow.beans.UserBean;
import gov.loc.repository.console.workflow.beans.UserHelper;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.JbpmContext;
import org.springframework.web.servlet.ModelAndView;

public class ProcessDefinitionController extends AbstractRestController {

	public static final String PROCESSDEFINITIONID = "processDefinitionId";
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleGet(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception {
		Map model = new HashMap();

		if (urlParameterMap.containsKey(PROCESSDEFINITIONID))
		{
			String processDefinitionId = urlParameterMap.get(PROCESSDEFINITIONID);
			if (! ProcessDefinitionHelper.hasProcessDefinition(processDefinitionId, jbpmContext))
			{
				response.sendError(HttpServletResponse.SC_NOT_FOUND);
				return null;
			}
			
			model.put("processDefinitionBean", ProcessDefinitionHelper.getProcessDefinitionBean(processDefinitionId, jbpmContext));
			model.put("canCreate", this.canCreate(processDefinitionId, request, jbpmContext));
			
			return new ModelAndView(".processdefinition.item", "model", model);
			
		}
		List<ProcessDefinitionBean> processDefinitionBeanList = null;
		String userId = (String)request.getAttribute(PARAMETER_USER);
		if (userId != null)
		{
			if (! UserHelper.exists(userId, jbpmContext))
			{
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unknown user");
				return null;
			}
			UserBean userBean = new UserBean();
			userBean.setId(userId);
			userBean.setJbpmContext(jbpmContext);
			processDefinitionBeanList = userBean.getProcessDefinitionBeanList();
		}
		else
		{
			processDefinitionBeanList = ProcessDefinitionHelper.getProcessDefinitionBeanList(jbpmContext);
		}
		model.put("processDefinitionBeanList", processDefinitionBeanList);
		return new ModelAndView(".processdefinition.list", "model", model);
				
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handlePost(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String, String> urlParameterMap) throws Exception {
		Map model = new HashMap();
		
		if (! urlParameterMap.containsKey(PROCESSDEFINITIONID))
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Process definition id not provided");
			return null;
		}
		String processDefinitionId = urlParameterMap.get(PROCESSDEFINITIONID);
		if (! ProcessDefinitionHelper.hasProcessDefinition(processDefinitionId, jbpmContext))
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		ProcessDefinitionBean processDefinitionBean = ProcessDefinitionHelper.getProcessDefinitionBean(processDefinitionId, jbpmContext);

		UserBean userBean = new UserBean();
		userBean.setJbpmContext(jbpmContext);
		userBean.setId(request.getUserPrincipal().getName());
		
		if(! userBean.getProcessDefinitionBeanList().contains(processDefinitionBean))
		{
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized to create processinstance");
			return null;			
		}
		
		ProcessInstanceBean processInstanceBean = processDefinitionBean.newInstance();
		processInstanceBean.save();
		response.setStatus(HttpServletResponse.SC_CREATED);
		response.addHeader("Location", this.applicationRoot + "/processinstance/" + processInstanceBean.getId());
		
		model.put("processDefinitionBean", processDefinitionBean);
		model.put("canCreate", true);
		return new ModelAndView(".processdefinition.item", "model", model);		
	}
	
	private boolean canCreate(String processDefinitionId, HttpServletRequest request, JbpmContext jbpmContext) throws Exception
	{
		ProcessDefinitionBean processDefinitionBean = ProcessDefinitionHelper.getProcessDefinitionBean(processDefinitionId, jbpmContext);

		//User must be logged in and able to create
		if (request.getUserPrincipal() == null)
		{
			return false;
		}
		UserBean userBean = new UserBean();
		userBean.setJbpmContext(jbpmContext);
		userBean.setId(request.getUserPrincipal().getName());
		
		if(! userBean.getProcessDefinitionBeanList().contains(processDefinitionBean))
		{
			return false;
		}
		return true;

	}
}
