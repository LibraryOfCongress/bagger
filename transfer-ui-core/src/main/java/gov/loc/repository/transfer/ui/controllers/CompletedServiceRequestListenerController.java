package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import gov.loc.repository.workflow.continuations.CompletedServiceRequestListener;
import gov.loc.repository.workflow.continuations.CompletedServiceRequestListener.State;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class CompletedServiceRequestListenerController
{
	
	
	private static final Log log = LogFactory.getLog(CompletedServiceRequestListenerController.class);
	private CompletedServiceRequestListener listener;
	
	@Autowired
	@Required
	public void setCompletedServicerRequestListener(CompletedServiceRequestListener listener)
	{
		this.listener = listener;
	}
	
	@PostConstruct
	public void init()
	{
		this.listener.start();
	}
	
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView handleGet(HttpServletRequest req) throws Exception {
		PermissionsHelper permissions = new PermissionsHelper(req);
		
		ModelAndView mav = new ModelAndView("completedrequestlistener");
		mav.addObject("listener", this.listener);
		mav.addObject("permissions", permissions);
		return mav;		
	}

	@RequestMapping(method=RequestMethod.POST)
	public ModelAndView handlePost(HttpServletRequest req) throws Exception {
		try
		{
			if (req.getParameter("start") != null && this.listener.getState() == State.STOPPED) {
				this.listener.start();
				req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Started Service Request Listener");
			}
			else if (req.getParameter("stop") != null && this.listener.getState() == State.STARTED) {
				this.listener.stop();
				req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Stopped Service Request Listener");
			}
		}
		catch(Exception ex)
		{
			log.error("Error thrown starting/stop CompletedServiceRequestListener", ex);
			req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Unable to start/stop Service Request Listener");
		}
		return this.handleGet(req);
	}
	
}
