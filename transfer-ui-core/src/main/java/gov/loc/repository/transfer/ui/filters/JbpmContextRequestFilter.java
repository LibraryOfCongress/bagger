package gov.loc.repository.transfer.ui.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;

public class JbpmContextRequestFilter implements Filter {

	static JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();
	private static final Log log = LogFactory.getLog(JbpmContextRequestFilter.class);
		
	public void destroy() { }

	public void doFilter(
	        ServletRequest req, 
	        ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
	
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		log.debug("Created new jbpmConfiguration for " + req.toString());
		try {
			req.setAttribute("jbpmcontext", jbpmContext);
			HttpServletRequest httpReq = (HttpServletRequest)req;
			if (httpReq.getRemoteUser() != null) {
				log.debug("Setting actorId to " + httpReq.getRemoteUser());
				jbpmContext.setActorId(httpReq.getRemoteUser());
			}
			// Call the next filter (continue request processing)
			chain.doFilter(req, resp);	
		}catch(Exception e){
		    log.error("Error creating jbpmContext for " + req.toString(), e);
		}finally{			
			try {
				jbpmContext.close();
				log.debug("Closed jbpmConfiguration for " + req.toString());
			} catch(Exception ex) {
				log.error("Error closing jbpmContext for " + req.toString(), ex);
			}
		}
	}

	public void init(FilterConfig config) throws ServletException { }

}
