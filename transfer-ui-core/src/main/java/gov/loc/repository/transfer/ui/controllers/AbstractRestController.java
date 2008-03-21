package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.dao.CommentDao;
import gov.loc.repository.transfer.ui.dao.ProcessDao;
import gov.loc.repository.transfer.ui.dao.ProcessDefDao;
import gov.loc.repository.transfer.ui.dao.TaskDao;
import gov.loc.repository.transfer.ui.dao.UserDao;
import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.springframework.ModelAndView;
import gov.loc.repository.transfer.ui.utilities.UrlParameterHelper;
import gov.loc.repository.transfer.ui.utilities.PermissionsHelper;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.springframework.beans.factory.annotation.Autowired;  
import org.springframework.beans.factory.annotation.Qualifier; 
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
	
    @Autowired(required=true)
    protected TaskDao taskDao;
	public void setTaskDao(TaskDao taskDao){
	    this.taskDao = taskDao;
	}
	
	@Autowired(required=true)
    protected UserDao userDao;
	public void setUserDao(UserDao userDao){
	    this.userDao = userDao;
	}
	
	@Autowired(required=true)
    protected CommentDao commentDao;
	public void setCommentDao(CommentDao commentDao){
	    this.commentDao = commentDao;
	}
	
	@Autowired(required=true)
    protected ProcessDefDao processDefDao;
	public void setProcessDefDao(ProcessDefDao processDefDao){
	    this.processDefDao = processDefDao;
	}
	
	@Autowired(required=true)
	private ProcessDao processDao;
	public void setProcessDao(ProcessDao processDao){
	    this.processDao = processDao;
	}
	
	//Subclass method should annotate with @RequestMethod and call handleRequestInternal().
	public abstract ModelAndView handleRequest(
	        HttpServletRequest request, 
	        HttpServletResponse response
	) throws Exception;
	
	
	@SuppressWarnings("unchecked")
	protected ModelAndView handleRequestInternal(
	        HttpServletRequest request,
			HttpServletResponse response) throws Exception 
	{	
		JbpmContext jbpmContext = (JbpmContext)request.getAttribute("jbpmcontext");
		commentDao.setJbpmContext(jbpmContext);
		processDefDao.setJbpmContext(jbpmContext);
		taskDao.setJbpmContext(jbpmContext);
		userDao.setJbpmContext(jbpmContext);
		log.debug("Set JbpmContext in Data Access Objects: " + jbpmContext);
		
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
		
		PermissionsHelper permissions = new PermissionsHelper(request); 
		
		//Add basic request context info to view map
		ModelAndView mav = new ModelAndView();
		mav.addObject("contextPath", request.getContextPath());
		mav.addObject("requestURI", request.getRequestURI());
		mav.addObject("permissions", permissions);
		if (request.getRequestURI().endsWith("index.html")){
		    log.debug("Handling index");
			this.handleIndex(
			    request, 
			    mav, 
			    jbpmContext, 
			    permissions, 
			    urlParameterMap
			);
		} else if (METHOD_GET.equalsIgnoreCase(method)) {
		    log.debug("Handling GET");
			this.handleGet(
			    request, 
			    mav, 
			    jbpmContext, 
			    permissions, 
			    urlParameterMap
			);
		} else if (METHOD_POST.equalsIgnoreCase(method))  {
	        log.debug("Handling POST");
			this.handlePost(
			    request, 
			    mav, 
			    jbpmContext, 
			    permissions, 
			    urlParameterMap
			);
		} else if (METHOD_PUT.equalsIgnoreCase(method)) {
	        log.debug("Handling PUT");
			this.handlePut(
			    request, 
			    mav, 
			    jbpmContext, 
			    permissions, 
			    urlParameterMap
			);
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
		//Postprocess for referer
		else if (request.getParameter(UIConstants.PARAMETER_REFERER) != null) {
			mav.setViewName("redirect:" +
			    request.getParameter(UIConstants.PARAMETER_REFERER)
			);
		}
				
		return mav;
	}

	protected void handleIndex(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext,
	        PermissionsHelper permissionsHelper, 
	        Map<String,String> urlParameterMap) throws Exception{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}	
	
	protected void handleGet(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext,
	        PermissionsHelper permissionsHelper, 
	        Map<String,String> urlParameterMap) throws Exception{
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
	protected void handlePost(
	        HttpServletRequest request,
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        PermissionsHelper permissionsHelper, 
	        Map<String,String> urlParameterMap) throws Exception {
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);		
	}

	protected void handlePut(
	        HttpServletRequest request, 
	        ModelAndView mav, 
	        JbpmContext jbpmContext, 
	        PermissionsHelper permissionsHelper, 
	        Map<String,String> urlParameterMap) throws Exception {
		mav.setError(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
	}
	
}
