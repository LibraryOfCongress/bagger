package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.workflow.listeners.JmsCompletedJobListener;
import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class JmsCompletedJobListenerController
{
	private static final Log log = LogFactory.getLog(JmsCompletedJobListenerController.class);
	private JmsCompletedJobListener listener;

	
	@Autowired
	public void setJmsCompletedJobListener(JmsCompletedJobListener listener)
	{
		this.listener = listener;
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView handleGet(HttpServletRequest req) throws Exception {
		PermissionsHelper permissions = new PermissionsHelper(req);
		
		ModelAndView mav = new ModelAndView("completedjoblistener");
		mav.addObject("listener", this.listener);
		mav.addObject("permissions", permissions);
		return mav;		
	}

	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView handlePost(HttpServletRequest req) throws Exception {
		try
		{
			if (req.getParameter("start") != null && ! this.listener.isStarted()) {
				this.listener.start();
				req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Started Job Listener");
			}
			else if (req.getParameter("stop") != null && this.listener.isStarted()) {
				this.listener.stop();
				req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Stopped Job Listener");
			}
		}
		catch(Exception ex)
		{
			log.error("Error thrown starting/stop JmsCompletedJobListener", ex);
			req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Unable to start/stop Job Listener");
		}
		return this.handleGet(req);
	}
	
    
}
