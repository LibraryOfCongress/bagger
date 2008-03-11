package gov.loc.repository.transfer.ui.controllers;

import java.util.HashMap;
import java.util.Map;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.workflow.listeners.JmsCompletedJobListener;
import gov.loc.repository.workflow.listeners.impl.ActiveMQJmsCompletedJobListener;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JmsCompletedJobListenerController
{

	private JmsCompletedJobListener listener;

	public JmsCompletedJobListenerController() throws Exception {
		listener = new ActiveMQJmsCompletedJobListener();
	}	
	
	@SuppressWarnings("unchecked")
	@RequestMapping("/services/listener*")
	public ModelAndView handleRequest(HttpServletRequest req, HttpServletResponse resp) throws Exception {
		PermissionsHelper permissionsHelper = new PermissionsHelper(req);
		if ((req.getParameter("start") != null || req.getParameter("stop") != null) && ! permissionsHelper.canAdministerJobListener())			
		{
			resp.sendError(HttpServletResponse.SC_FORBIDDEN);
			return null;
		}
		if (req.getParameter("start") != null && ! this.listener.isStarted())
		{
			this.listener.start();
		}
		else if (req.getParameter("stop") != null && this.listener.isStarted())
		{
			this.listener.stop();
		}
		
		ModelAndView mav = new ModelAndView();
		mav.addObject("contextPath", req.getContextPath());
		mav.addObject("listener", this.listener);
		mav.addObject("permissionsHelper", permissionsHelper);
				
		mav.setViewName("/services/listener");
		return mav;		
	}

}
