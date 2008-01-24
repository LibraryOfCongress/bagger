package gov.loc.repository.console;

import gov.loc.repository.console.utilities.UrlParameterHelper;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.identity.Group;
import org.jbpm.identity.hibernate.IdentitySession;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class AbstractRestController extends AbstractController {

	protected static final Log log = LogFactory.getLog(AbstractRestController.class);
	
	public static final String METHOD_POST = "POST";
	public static final String METHOD_GET = "GET";
	public static final String METHOD_PUT = "PUT";
	public static final String METHOD_HEAD = "HEAD";
	public static final String METHOD_DELETE = "DELETE";
	
	public static final String PARAMETER_METHOD = "method";
	public static final String PARAMETER_FORMAT = "format";
	public static final String PARAMETER_USER = "user";
	
	public static final String FORMAT_XHTML = "xhtml";
	public static final String FORMAT_XHTMLFRAGMENT = "xhtmlfragment";

	public static final String CURRENTUSER = "currentuser";
	
	String urlParametersDescription = null;
	protected String applicationRoot = null;
	
	public AbstractRestController() {
		//Override default supportedMethods to all
		this.setSupportedMethods(new String[] {METHOD_DELETE, METHOD_GET, METHOD_HEAD, METHOD_POST, METHOD_PUT});
	}
	
	public void setUrlParametersDescription(String description)
	{
		this.urlParametersDescription = description;
	}
	
	public void setApplicationRoot(String applicationRoot)
	{
		this.applicationRoot = applicationRoot;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleRequestInternal(HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		
		JbpmContext jbpmContext = (JbpmContext)request.getAttribute("jbpmcontext");
				
		Map<String,String> urlParameterMap = new HashMap<String, String>();
		if (this.urlParametersDescription != null)
		{
			urlParameterMap = UrlParameterHelper.parse(request.getRequestURI(), this.urlParametersDescription);
			//Perform current user substitution
			if (urlParameterMap.containsValue(CURRENTUSER))
			{
				if (request.getUserPrincipal() == null)
				{
					response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User is not authenticated");
					return null;
				}
				
				for(String key : urlParameterMap.keySet())
				{
					if (CURRENTUSER.equals(urlParameterMap.get(key)))
					{
						urlParameterMap.put(key, request.getUserPrincipal().getName());
					}
				}
			}
		}
		//Perform current user substitution in parameters
		if (request.getParameterMap().containsKey(PARAMETER_USER))
		{
			if (CURRENTUSER.equals(request.getParameter(PARAMETER_USER)))
			{
				if (request.getUserPrincipal() == null)
				{
					response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User is not authenticated, so cannot limit to currentuser");
					return null;
				}
				request.setAttribute(PARAMETER_USER, request.getUserPrincipal().getName());
			}
			else
			{
				request.setAttribute(PARAMETER_USER, request.getParameter(PARAMETER_USER));
			}
		}
			
		String method = request.getMethod();
		if (request.getParameterMap().containsKey(PARAMETER_METHOD))
		{
			method = request.getParameter(PARAMETER_METHOD);
		}
		log.debug(MessageFormat.format("Method for request {0} is {1}", request.getRequestURI(), method));
		ModelAndView modelAndView;
		if (METHOD_GET.equalsIgnoreCase(method))
		{
			modelAndView = this.handleGet(request, response, jbpmContext, urlParameterMap);
		}
		else if (METHOD_POST.equalsIgnoreCase(method))
		{
			//User must be logged in and able to post
			if (request.getUserPrincipal() == null)
			{
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
				return null;
			}
			modelAndView = this.handlePost(request, response, jbpmContext, urlParameterMap);
		}
		else if (METHOD_PUT.equalsIgnoreCase(method))
		{
			//User must be logged in and able to put
			if (request.getUserPrincipal() == null)
			{
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "User not authenticated");
				return null;
			}

			modelAndView = this.handlePut(request, response, jbpmContext, urlParameterMap);
		}		
		else
		{
			response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
			return null;
		}
		//Adjust the view name for different formats
		String format = null;
		if (! request.getParameterMap().containsKey(PARAMETER_FORMAT) || FORMAT_XHTML.equals(request.getParameter(PARAMETER_FORMAT)))
		{
			format = FORMAT_XHTML;
		}
		else if (FORMAT_XHTMLFRAGMENT.equals(request.getParameter(PARAMETER_FORMAT)))
		{
			format = FORMAT_XHTMLFRAGMENT;
		}
		else
		{
			response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Unrecognized format");
			modelAndView = null;
		}
		if (modelAndView != null)
		{
			modelAndView.setViewName(modelAndView.getViewName() + "." + format);
			modelAndView = modelAndView.addObject("root", this.applicationRoot);
		}
		
		return modelAndView;
	}
	
	protected ModelAndView handleGet(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}
	
	protected ModelAndView handlePost(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}

	protected ModelAndView handlePut(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String,String> urlParameterMap) throws Exception
	{
		response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
		return null;
	}
	
}
