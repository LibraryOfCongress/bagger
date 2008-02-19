package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.model.UserBean;

import javax.servlet.http.HttpServletRequest;

import org.jbpm.JbpmContext;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@RequestMapping("/*")
	public ModelAndView home(HttpServletRequest req)
	{
		ModelAndView mav = new ModelAndView();
		if (req.getUserPrincipal() == null)
		{
			mav.setViewName("/home/not_logged_in");
			return mav;
		}
		mav.setViewName("/home/logged_in");
		
		UserBean userBean = new UserBean();
		userBean.setId(req.getUserPrincipal().getName());
		userBean.setJbpmContext((JbpmContext)req.getAttribute("jbpmcontext"));
		mav.addObject("groupTaskInstanceBeanList", userBean.getGroupTaskInstanceBeanList());
		mav.addObject("userTaskInstanceBeanList", userBean.getUserTaskInstanceBeanList());
		mav.addObject("processDefinitionBeanList", userBean.getProcessDefinitionBeanList());
		mav.addObject("currentUser", req.getUserPrincipal().getName());
		
		return mav;
	}
	
}
