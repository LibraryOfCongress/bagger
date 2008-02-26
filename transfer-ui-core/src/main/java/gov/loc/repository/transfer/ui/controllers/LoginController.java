package gov.loc.repository.transfer.ui.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class LoginController {

	@RequestMapping("/login/login_form")
	@SuppressWarnings("unchecked")	
	public String login_form() {		
		
		return "login/login";
	}

	@RequestMapping("/login/login_error")
	@SuppressWarnings("unchecked")	
	public String login_error() {		
		
		return "login/login";
	}
	
	@RequestMapping("/login/login")
	@SuppressWarnings("unchecked")	
	public String login() {		
		
		return "redirect:/index.html";
	}

	@RequestMapping("/login/logout")
	public String logout(HttpServletRequest req) throws Exception {
		req.getSession().invalidate();
		return "redirect:/index.html";		
	}
}
