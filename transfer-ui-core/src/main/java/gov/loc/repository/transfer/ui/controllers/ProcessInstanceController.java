package gov.loc.repository.transfer.ui.controllers;

import java.util.Map;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.ProcessInstanceBean;
import gov.loc.repository.transfer.ui.model.ProcessInstanceHelper;
import gov.loc.repository.transfer.ui.model.UserHelper;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.JbpmContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProcessInstanceController extends AbstractRestController {

	public static final String PROCESSINSTANCEID = "processInstanceId";

	@Override
	public String getUrlParameterDescription() {
		return "processinstance/{processInstanceId}\\.{format}";
	}

	@RequestMapping("/processinstance/*.*")
	@Override
	public ModelAndView handleRequest(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception 
	{
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
		
		UserBean userBean = new UserBean();
		userBean.setId(request.getUserPrincipal().getName());
		userBean.setJbpmContext(jbpmContext);
		
		mav.setViewName("processinstancelist");
		mav.addObject(
		    "processInstanceBeanList", 
		    ProcessInstanceHelper.getProcessInstanceBeanList(jbpmContext)
		);
		mav.addObject(
		    "suspendedProcessInstanceBeanList", 
		    ProcessInstanceHelper.getSuspendedProcessInstanceBeanList(jbpmContext)
		);
		mav.addObject(
		    "processDefinitionBeanList", 
		    userBean.getProcessDefinitionBeanList()
		);
	}

	@Override
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav,
			JbpmContext jbpmContext, 
			PermissionsHelper permissionsHelper,
			 Map<String, String> urlParameterMap) throws Exception 
	{
		ProcessInstanceBean processInstanceBean = processProcessInstance(mav, jbpmContext, urlParameterMap);
		if (processInstanceBean == null) { return; }
		
		mav.addObject("processInstanceBean", processInstanceBean);
		if (permissionsHelper.canUpdateTaskInstanceUser()) {
			mav.addObject(
			    "userBeanList", 
			    UserHelper.getUserBeanList(jbpmContext)
			);
		}
		if (permissionsHelper.canMoveToken()) {
			mav.addObject(
			    "nodeBeanList", 
			    processInstanceBean.getProcessDefinitionBean().getNodeBeanList()
			);
		}
		mav.setViewName("processinstance");
	}
	
	@Override
	protected void handlePut(HttpServletRequest request, ModelAndView mav,
			JbpmContext jbpmContext, PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap)
			throws Exception {
		ProcessInstanceBean processInstanceBean = processProcessInstance(mav, jbpmContext, urlParameterMap);
		if (processInstanceBean == null)
		{
			return;
		}
	
		if (request.getParameter(UIConstants.PARAMETER_SUSPENDED) != null)
		{
			if (! permissionsHelper.canSuspendProcessInstance())
			{
				mav.setError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			if (UIConstants.VALUE_TRUE.equalsIgnoreCase(request.getParameter(UIConstants.PARAMETER_SUSPENDED)))
			{
				if (! processInstanceBean.isSuspended())
				{
					processInstanceBean.suspended(true);					
					processInstanceBean.save();
				}
			}
			else if (UIConstants.VALUE_FALSE.equalsIgnoreCase(request.getParameter(UIConstants.PARAMETER_SUSPENDED)))
			{
				if (processInstanceBean.isSuspended())
				{
					processInstanceBean.suspended(false);
					processInstanceBean.save();
				}
			} 
		}
		if (VariableUpdateHelper.requestUpdatesVariables(request))
		{
			if (! permissionsHelper.canUpdateVariables())
			{
				mav.setError(HttpServletResponse.SC_UNAUTHORIZED);
				return;
			}
			
			VariableUpdateHelper.update(request, processInstanceBean);
			processInstanceBean.save();
		}
		this.handleGet(request, mav, jbpmContext, permissionsHelper, urlParameterMap);
	}
	
	private ProcessInstanceBean processProcessInstance(ModelAndView mav, JbpmContext jbpmContext, Map<String, String> urlParameterMap)
	{
		//If there is no processInstanceId in urlParameterMap then 404
		if (! urlParameterMap.containsKey(PROCESSINSTANCEID)) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		//Otherwise handle processinstanceid
		String processInstanceId = urlParameterMap.get(PROCESSINSTANCEID);
		if (! ProcessInstanceHelper.hasProcessInstance(Long.parseLong(processInstanceId), jbpmContext)){
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		return ProcessInstanceHelper.getProcessInstanceBean(Long.parseLong(processInstanceId), jbpmContext);
		
	}
}
