package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.UrlParameterHelper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.springframework.web.servlet.mvc.AbstractController;

public abstract class AbstractRestController extends AbstractController {

	protected static final Log log = LogFactory.getLog(AbstractRestController.class);
	
	public static final String METHOD_POST      = "POST";
	public static final String METHOD_GET       = "GET";
	public static final String METHOD_PUT       = "PUT";
	public static final String METHOD_HEAD      = "HEAD";
	public static final String METHOD_DELETE    = "DELETE";
	public static final String PARAMETER_METHOD = "method";
		
	public abstract String getUrlParameterDescription();
		    
	//Subclass method should annotate with @RequestMethod and call handleRequestInternal().
	public abstract ModelAndView handleRequest(
	        HttpServletRequest request, 
	        HttpServletResponse response
	) throws Exception;
	
	@SuppressWarnings("unchecked")
	protected ModelAndView handleRequestInternal(
	        HttpServletRequest request,
			HttpServletResponse response) 
			throws Exception {
		
		JbpmContext jbpmContext = (JbpmContext)request.getAttribute("jbpmcontext");
		
		//Strip custom parameters from the url	
		Map<String,String> urlParameterMap = new HashMap<String, String>();
		if (this.getUrlParameterDescription() != null){
			urlParameterMap = UrlParameterHelper.parse(
			    request.getRequestURI(), 
			    this.getUrlParameterDescription()
			);
		    log.debug(MessageFormat.format(
		        "Parameter for request {0} is {1}", 
		        request.getRequestURI(), 
		        urlParameterMap.toString()
		    ));
		}
		
		//Decide whether the http method or a url based method is appropriate
		String method = request.getMethod();
		if (request.getParameterMap().containsKey(PARAMETER_METHOD)) {
			method = request.getParameter(PARAMETER_METHOD);
		}
		log.debug(MessageFormat.format(
		    "Method for request {0} is {1}", 
		    request.getRequestURI(), 
		    method
		));
		
		//Add basic request context info to view map
		ModelAndView mav = new ModelAndView();
		mav.addObject("contextPath", request.getContextPath());
		mav.addObject("requestURI", request.getRequestURI());
		if (request.getRequestURI().endsWith("index.html"))
		{
		    log.debug("Handling index");
			this.handleIndex(
			    request, 
			    mav, 
			    jbpmContext, 
			    urlParameterMap
			);
			
		}		
		else if (METHOD_GET.equalsIgnoreCase(method)) {
		    log.debug("Handling GET");
			this.handleGet(
			    request, 
			    mav, 
			    jbpmContext, 
			    urlParameterMap
			);
		}else if (METHOD_POST.equalsIgnoreCase(method))  {
			//User must be logged in and able to post
			if (request.getUserPrincipal() == null) {
		        log.warn("User not Authenticated, ignoring POST");
				mav.setError(
				    HttpServletResponse.SC_UNAUTHORIZED, 
				    "User not authenticated"
				);
			} else {
		        log.debug("Handling POST");
				this.handlePost(
				    request, 
				    mav, 
				    jbpmContext, 
				    urlParameterMap
				);
			}
		} else if (METHOD_PUT.equalsIgnoreCase(method)) {
			//User must be logged in and able to put
			if (request.getUserPrincipal() == null) {
		        log.warn("User not Authenticated, ignoring PUT");
				mav.setError(
				    HttpServletResponse.SC_UNAUTHORIZED,
				    "User not authenticated"
				);				
			} else {
		        log.debug("Handling PUT");
				this.handlePut(
				    request, 
				    mav, 
				    jbpmContext, 
				    urlParameterMap
				);
			}
		} else {
			mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

        //Decide whether or not we've run into an error condition
		if (mav.getStatusCode() != null) {
		    log.debug("Status code : " + mav.getStatusCode());
			if (mav.getMessage() != null) {
				response.sendError(
				    mav.getStatusCode(), 
				    mav.getMessage()
				);
			} else {
				response.sendError(mav.getStatusCode());
			}
			mav = null;
		}
		return mav;
	}

	protected void handleIndex(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext,
	        Map<String,String> urlParameterMap) throws Exception{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}	
	
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext,
	        Map<String,String> urlParameterMap) throws Exception{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	protected void handlePost(
	        HttpServletRequest request,
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        Map<String,String> urlParameterMap) throws Exception {
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);		
	}

	protected void handlePut(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        Map<String,String> urlParameterMap) throws Exception {
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
}
