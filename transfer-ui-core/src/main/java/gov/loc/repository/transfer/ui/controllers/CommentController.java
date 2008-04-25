package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.dao.WorkflowDao;
import gov.loc.repository.transfer.ui.model.ProcessInstanceBean;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class CommentController extends AbstractRestController {

	public static final String PROCESSINSTANCEID = "processInstanceId";

	@Override
	public String getUrlParameterDescription() {
		return "processinstance/{processInstanceId}/comment\\.{format}";
	}

	@RequestMapping
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
	        WorkflowBeanFactory factory, 
	        WorkflowDao dao, 
	        PermissionsHelper permissionsHelper, Map<String, String> urlParameterMap) throws Exception 
	{
		if (! urlParameterMap.containsKey(PROCESSINSTANCEID)) {
			mav.setError(
			    HttpServletResponse.SC_BAD_REQUEST, 
			    "Process instance id not provided"
			); return;
		}
		String processInstanceId = urlParameterMap.get(PROCESSINSTANCEID);
		ProcessInstanceBean processInstanceBean = dao.getProcessInstanceBean(processInstanceId);
		if (processInstanceBean == null) {
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		if (request.getParameter(UIConstants.PARAMETER_MESSAGE) == null) {
			mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Message not provided");
			return;
		}
		String message =  request.getParameter(UIConstants.PARAMETER_MESSAGE);
		processInstanceBean.addComment(message);
		dao.save(processInstanceBean);
		request.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Your comment was added.");

		mav.setViewName("redirect:/processinstance/" + processInstanceId + ".html");
	}
	
}
