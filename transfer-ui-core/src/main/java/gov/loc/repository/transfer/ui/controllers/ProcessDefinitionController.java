package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.model.ProcessDefinitionBean;
import gov.loc.repository.transfer.ui.model.ProcessDefinitionHelper;
import gov.loc.repository.transfer.ui.model.ProcessInstanceBean;
import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.jbpm.JbpmContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ProcessDefinitionController extends AbstractRestController {

	public static final String PROCESSDEFINITIONID = "processDefinitionId";

	@Override
	public String getUrlParameterDescription() {
		return "processdefinition/{processDefinitionId}\\.{format}";
	}

	@RequestMapping("/processdefinition/*.*")
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
		
		if(! permissionsHelper.canStartProcess()) {
			mav.setError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized to create processinstance");
			return;			
		}
				
		if (! urlParameterMap.containsKey(PROCESSDEFINITIONID)) {
			mav.setError(HttpServletResponse.SC_BAD_REQUEST, "Process definition id not provided");
			return;
		}
		String processDefinitionId = urlParameterMap.get(PROCESSDEFINITIONID);
		if (! ProcessDefinitionHelper.hasProcessDefinition(processDefinitionId, jbpmContext))
		{
			mav.setError(HttpServletResponse.SC_NOT_FOUND);
			return;
		}
		ProcessDefinitionBean processDefinitionBean = 
		    ProcessDefinitionHelper.getProcessDefinitionBean(processDefinitionId, jbpmContext);

		UserBean userBean = new UserBean();
		userBean.setJbpmContext(jbpmContext);
		userBean.setId(request.getUserPrincipal().getName());
		
		if(! userBean.getProcessDefinitionBeanList().contains(processDefinitionBean)) {
			mav.setError(HttpServletResponse.SC_UNAUTHORIZED, "User not authorized to create processinstance");
			return;			
		}
		
		ProcessInstanceBean processInstanceBean = processDefinitionBean.newInstance();
		processInstanceBean.save();
		
		mav.setViewName("redirect:/processinstance/" + processInstanceBean.getId() + ".html");
		
	}
	
}
