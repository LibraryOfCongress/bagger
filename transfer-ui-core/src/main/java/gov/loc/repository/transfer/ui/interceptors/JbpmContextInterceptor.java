package gov.loc.repository.transfer.ui.interceptors;

import gov.loc.repository.transfer.ui.UIConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class JbpmContextInterceptor extends HandlerInterceptorAdapter {

	private static final Log log = LogFactory.getLog(JbpmContextInterceptor.class);
	
	@Autowired
	JbpmConfiguration jbpmConfiguration;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler) throws Exception {
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		log.debug("Created new jbpmConfiguration for " + request.getRequestURI());
		request.setAttribute(UIConstants.PARAMETER_JBPMCONTEXT, jbpmContext);			
		if (request.getUserPrincipal() != null)
		{
			log.debug("Setting actorId to " + request.getUserPrincipal().getName());
			jbpmContext.setActorId(request.getUserPrincipal().getName());
		}
		return true;
	}
		
	@Override
	public void afterCompletion(HttpServletRequest request,
			HttpServletResponse response, Object handler, Exception ex)
			throws Exception {
		JbpmContext jbpmContext = (JbpmContext)request.getAttribute(UIConstants.PARAMETER_JBPMCONTEXT);
		if (jbpmContext != null)
		{
			if (ex != null)
			{
				log.debug("Rolling back jbpmContext for " + request.getRequestURI());
				jbpmContext.setRollbackOnly();
			}
			log.debug("Closing jbpmContext for " + request.getRequestURI());
			jbpmContext.close();
		}
	}
	
}
