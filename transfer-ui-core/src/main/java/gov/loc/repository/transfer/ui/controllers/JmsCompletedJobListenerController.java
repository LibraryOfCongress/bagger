package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.workflow.listeners.JmsCompletedJobListener;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JmsCompletedJobListenerController
{

	private JmsCompletedJobListener listener;

	
	@Autowired
	public void setJmsCompletedJobListener(JmsCompletedJobListener listener)
	{
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/admin/completedjoblistener.html")
	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		PermissionsHelper permissions = new PermissionsHelper(req);
		if ((req.getParameter("start") != null || req.getParameter("stop") != null)
		    && ! permissions.canAdministerJobListener()){
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		if (req.getParameter("start") != null && ! this.listener.isStarted()) {
			this.listener.start();
		}
		else if (req.getParameter("stop") != null && this.listener.isStarted()) {
			this.listener.stop();
		}
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("contextPath", req.getContextPath());
		mav.addObject("listener", this.listener);
		mav.addObject("permissions", permissions);
				
		mav.setViewName("completedjoblistener");
		return mav;		
	}

    
}
