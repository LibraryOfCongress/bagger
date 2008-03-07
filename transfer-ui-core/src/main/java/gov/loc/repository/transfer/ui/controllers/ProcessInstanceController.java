package gov.loc.repository.transfer.ui.controllers;

import java.util.Map;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.model.ProcessDefinitionBean;
import gov.loc.repository.transfer.ui.model.ProcessDefinitionHelper;
import gov.loc.repository.transfer.ui.model.ProcessInstanceBean;
import gov.loc.repository.transfer.ui.model.ProcessInstanceHelper;
import gov.loc.repository.transfer.ui.model.UserBean;
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
	protected void handleIndex(HttpServletRequest request, ModelAndView mav,
			JbpmContext jbpmContext, Map<String, String> urlParameterMap)
			throws Exception {
		mav.addObject("processInstanceBeanList", ProcessInstanceHelper.getProcessInstanceBeanList(jbpmContext));
		mav.setViewName("processinstancelist");
	}
	
}
