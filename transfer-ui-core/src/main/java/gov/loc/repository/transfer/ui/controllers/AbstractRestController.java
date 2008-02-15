package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.springframework.web.servlet.ModelAndView;
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
	
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_DELETE = "DELETE";
	
	public static final String PARAMETER_METHOD = "method";
		
	public abstract String getUrlParameterDescription();
		
	/*
	 * Subclass method should annotate with @RequestMethod and call handleRequestInternal().
	 */
	public abstract org.springframework.web.servlet.ModelAndView handleRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;
	
	@SuppressWarnings("unchecked")
	protected org.springframework.web.servlet.ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		JbpmContext jbpmContext = (JbpmContext)request.getAttribute("jbpmcontext");
				
		Map<String,String> urlParameterMap = new HashMap<String, String>();
		if (this.getUrlParameterDescription() != null)
		{
			urlParameterMap = UrlParameterHelper.parse(request.getRequestURI(), this.getUrlParameterDescription());
		}
			
		String method = request.getMethod();
		if (request.getParameterMap().containsKey(PARAMETER_METHOD))
		{
			method = request.getParameter(PARAMETER_METHOD);
		}
		log.debug(MessageFormat.format("Method for request {0} is {1}", request.getRequestURI(), method));
		ModelAndView mav = new ModelAndView();
		mav.addObject("contextPath", request.getContextPath());
		mav.addObject("requestURI", request.getRequestURI());
		if (METHOD_GET.equalsIgnoreCase(method))
		{
			this.handleGet(request, mav, jbpmContext, urlParameterMap);
		}
		else if (METHOD_POST.equalsIgnoreCase(method))
		{
			//User must be logged in and able to post
			if (request.getUserPrincipal() == null)
			{
				mav.setError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
			}
			else
			{
				this.handlePost(request, mav, jbpmContext, urlParameterMap);
			}
		}
		else if (METHOD_PUT.equalsIgnoreCase(method))
		{
			//User must be logged in and able to put
			if (request.getUserPrincipal() == null)
			{
				mav.setError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");				
			}
			else
			{
				this.handlePut(request, mav, jbpmContext, urlParameterMap);
			}
		}		
		else
		{
			mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		}

		if (mav.getStatusCode() != null)
		{
			if (mav.getMessage() != null)
			{
				response.sendError(mav.getStatusCode(), mav.getMessage());
			}
			else
			{
				response.sendError(mav.getStatusCode());
			}
			mav = null;
		}
		return mav;
	}
	
	protected void handleGet(HttpServletRequest request, ModelAndView mav, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception
	{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	protected void handlePost(HttpServletRequest request, ModelAndView mav, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception
	{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);		
	}

	protected void handlePut(HttpServletRequest request, ModelAndView mav, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception
	{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
}
