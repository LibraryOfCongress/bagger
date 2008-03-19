package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.model.ProcessInstanceHelper;
import gov.loc.repository.transfer.ui.model.UserBean;

import javax.servlet.http.HttpServletRequest;

import org.jbpm.JbpmContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@RequestMapping("/**")
	public ModelAndView home(HttpServletRequest req)
	{
		JbpmContext jbpmContext = (JbpmContext)req.getAttribute("jbpmcontext");
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home");
		mav.addObject("contextPath", req.getContextPath());
		if (req.getUserPrincipal() == null) {
			return mav;
		}
		
		/*UserBean userBean = new UserBean();
		userBean.setId(req.getUserPrincipal().getName());
		userBean.setJbpmContext(jbpmContext);
		
		mav.addObject("groupTaskInstanceBeanList", userBean.getGroupTaskInstanceBeanList());
		mav.addObject("userTaskInstanceBeanList", userBean.getUserTaskInstanceBeanList());
		mav.addObject("currentUser", req.getUserPrincipal().getName());*/
		
		return mav;
	}
	
}
