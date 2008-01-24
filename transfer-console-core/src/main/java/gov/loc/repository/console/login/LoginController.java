package gov.loc.repository.console.login;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.multiaction.MultiActionController;

public class LoginController extends MultiActionController {

	@SuppressWarnings("unchecked")
	public ModelAndView login_form(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {		
		
		return new ModelAndView(".login.form");
	}

	@SuppressWarnings("unchecked")
	public ModelAndView login_error(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		return new ModelAndView(".login.error");
	}
	
	public ModelAndView login(HttpServletRequest req, HttpServletResponse resp) throws Exception
	{
		
		return new ModelAndView("redirect:/user/currentuser");
	}
	
	public ModelAndView logout(HttpServletRequest req,
			HttpServletResponse resp) throws Exception {
		req.getSession().invalidate();
		return new ModelAndView("redirect:/index.html");		
	}
}
