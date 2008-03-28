package gov.loc.repository.transfer.ui.controllers;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class HomeController {

	@RequestMapping("/**")
	public ModelAndView home(HttpServletRequest req)
	{
		ModelAndView mav = new ModelAndView();
		mav.setViewName("home");
		mav.addObject("contextPath", req.getContextPath());
		if (req.getUserPrincipal() == null) {
			return mav;
		}
				
		return mav;
	}
	
}
