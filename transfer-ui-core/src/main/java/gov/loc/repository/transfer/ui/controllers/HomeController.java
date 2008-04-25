package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.model.UserBean;
import gov.loc.repository.transfer.ui.model.WorkflowBeanFactory;

import javax.servlet.http.HttpServletRequest;

import org.jbpm.JbpmContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {
	MessageSource messageSource;
	
	@Autowired
	public void setMessageSource(MessageSource messageSource)
	{
		this.messageSource = messageSource;
	}
		
	@RequestMapping
	public ModelAndView home(HttpServletRequest req)
	{
		ModelAndView mav = new ModelAndView("home");
		if (req.getUserPrincipal() != null)
		{
			//Add the user to the view
			JbpmContext jbpmContext = (JbpmContext)req.getAttribute("jbpmcontext");
			
			WorkflowBeanFactory factory = new WorkflowBeanFactory();
			factory.setJbpmContext(jbpmContext);		
			factory.setMessageSource(this.messageSource);
			
			UserBean userBean = factory.createUserBean(req.getUserPrincipal().getName());		
			mav.addObject("userBean", userBean);
		}

		return mav;
	}
	
}
