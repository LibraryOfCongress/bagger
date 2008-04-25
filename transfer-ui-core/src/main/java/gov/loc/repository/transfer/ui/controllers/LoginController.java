package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	static final Log log = LogFactory.getLog(LoginController.class);
	
	@RequestMapping("login_*.html")
	@SuppressWarnings("unchecked")	
	public String login_form(HttpSession session) {		
		if (session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY) != null)
		{
			log.debug("Login failed:" + ((AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY)).getMessage());
			session.setAttribute(UIConstants.SESSION_MESSAGE, "Login failed");
		}
		return "login";
	}
	
	@RequestMapping("login.html")
	@SuppressWarnings("unchecked")	
	public String login(HttpServletRequest req) {		
		
		if (req.getParameter(UIConstants.PARAMETER_REFERER) != null)
		{
			return "redirect:" + req.getParameter(UIConstants.PARAMETER_REFERER);
		}
		
		return "redirect:/index.html";
	}

}
