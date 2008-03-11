package gov.loc.repository.transfer.ui.controllers;

import java.util.Map;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.model.ProcessInstanceBean;
import gov.loc.repository.transfer.ui.model.ProcessInstanceHelper;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.JbpmContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommentController extends AbstractRestController {

	public static final String PROCESSINSTANCEID = "processInstanceId";

	@Override
	public String getUrlParameterDescription() {
		return "processinstance/{processInstanceId}/comment\\.{format}";
	}

	@RequestMapping("/processinstance/*/comment.*")
	@Override
	public ModelAndView handleRequest(
			HttpServletRequest request, 
			HttpServletResponse response) throws Exception 
	{
		return this.handleRequestInternal(request, response);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected void handlePost(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap) throws Exception 
	{
		if (! permissionsHelper.canAddComment())
		{
			mav.setError(HttpServletResponse.SC_UNAUTHORIZED);
			return;
		}
		
		if (! urlParameterMap.containsKey(PROCESSINSTANCEID)) {
			mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Process instance id not provided");
			return;
		}
		String processInstanceId = urlParameterMap.get(PROCESSINSTANCEID);		
		if (! ProcessInstanceHelper.hasProcessInstance(Long.parseLong(processInstanceId), jbpmContext))
		{
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		ProcessInstanceBean processInstanceBean = ProcessInstanceHelper.getProcessInstanceBean(Long.parseLong(processInstanceId), jbpmContext);
				
		if (request.getParameter(UIConstants.PARAMETER_MESSAGE) == null)
		{
			mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Message not provided");
			return;
			
		}
		
		String message =  request.getParameter(UIConstants.PARAMETER_MESSAGE);
		processInstanceBean.addComment(message);
		processInstanceBean.save();
				
		mav.setViewName("redirect:/processinstance/" + processInstanceId + ".html");
		
	}
	
}
