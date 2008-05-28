package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.serviceBroker.dao.ServiceRequestDAO;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class ServiceRequestBrokerController
{
	
	
	private static final Log log = LogFactory.getLog(ServiceRequestBrokerController.class);
	private ServiceRequestDAO dao;
	
	@Resource(name="serviceBroker")
	@Required
	public void setServiceRequestDAO(ServiceRequestDAO dao)
	{
		this.dao = dao;
	}
		
	@SuppressWarnings("unchecked")
	@RequestMapping(method=RequestMethod.GET)
	public ModelAndView handleGet(HttpServletRequest req) throws Exception {
		//PermissionsHelper permissions = new PermissionsHelper(req);
		
		ModelAndView mav = new ModelAndView("servicerequestbroker");
		try {
			mav.addObject("serviceRequestList", this.dao.findServiceRequests(true, false, false));
			//mav.addObject("permissions", permissions);
		} catch (Exception e) {
			log.error("ServiceRequestBrokerController.handleGet: " + e.getMessage());
			//req.getSession().setAttribute(UIConstants.SESSION_MESSAGE, "Unable to get Service Request Broker");
		}
		return mav;		
	}
	
}
