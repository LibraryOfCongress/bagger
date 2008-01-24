package gov.loc.repository.console.utilities.jbpm;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class JbpmActorHandlerInterceptor extends HandlerInterceptorAdapter 
{
	private static final Log log = LogFactory.getLog(JbpmActorHandlerInterceptor.class);

	@Override
	public boolean preHandle(HttpServletRequest req, HttpServletResponse resp, Object handler) throws Exception {
		JbpmContext jbpmContext = (JbpmContext)req.getAttribute("jbpmcontext");
		if (req.getRemoteUser() != null)
		{
			log.debug("Setting actorId to " + req.getRemoteUser());
			jbpmContext.setActorId(req.getRemoteUser());
		}
		return super.preHandle(req, resp, handler);
	}
}
