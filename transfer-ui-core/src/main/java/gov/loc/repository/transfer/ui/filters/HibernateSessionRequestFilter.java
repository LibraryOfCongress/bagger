package gov.loc.repository.transfer.ui.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.hibernate.SessionFactory;

import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;

public class HibernateSessionRequestFilter implements Filter {

	private SessionFactory factory;
	
	
	public void destroy() {
		this.factory.close();

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
      try
      {
    	  this.factory.getCurrentSession().beginTransaction();

          // Call the next filter (continue request processing)
          chain.doFilter(req, resp);

          this.factory.getCurrentSession().getTransaction().commit();
      }
      catch (Throwable ex)
      {
    	  if (this.factory.getCurrentSession().isOpen())
    	  {
    		  this.factory.getCurrentSession().getTransaction().rollback();
    	  }
    	  throw new ServletException(ex);
      }
	}

	public void init(FilterConfig arg0) throws ServletException {
		this.factory = HibernateUtil.getSessionFactory(DatabaseRole.READ_ONLY);

	}

}
