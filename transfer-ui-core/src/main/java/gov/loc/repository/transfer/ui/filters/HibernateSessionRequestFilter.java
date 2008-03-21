package gov.loc.repository.transfer.ui.filters;

import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;
import java.io.IOException;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;

public class HibernateSessionRequestFilter implements Filter {

	private static final Log log = LogFactory.getLog(HibernateSessionRequestFilter.class);
	private SessionFactory factory;
	
	public void destroy() {
	    log.info("Destroying SessionFactory");
		this.factory.close();
	}

	public void doFilter(
	        ServletRequest req, 
	        ServletResponse resp,
			FilterChain chain) throws IOException, ServletException 
	{
      try {
          log.debug("Beginning Current Session Transaction");
    	  this.factory.getCurrentSession().beginTransaction();
          // Call the next filter (continue request processing)
          chain.doFilter(req, resp);
          log.debug("Commiting Current Session Transaction");
          this.factory.getCurrentSession().getTransaction().commit();
      } catch (Throwable ex) {
          log.error("Caught Error in Session Transaction");
          log.error(ex.toString());
    	  if (this.factory.getCurrentSession().isOpen()) {
    	      log.warn("Rolling Back Transaction");
    		  this.factory.getCurrentSession().getTransaction().rollback();
    	  }
    	  throw new ServletException(ex);
      }
	}

	public void init(FilterConfig arg0) throws ServletException {
		this.factory = HibernateUtil.getSessionFactory(DatabaseRole.READ_ONLY);
	}

}
