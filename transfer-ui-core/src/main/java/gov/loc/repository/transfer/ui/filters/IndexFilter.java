package gov.loc.repository.transfer.ui.filters;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class IndexFilter implements Filter {

	public void destroy() {

	}

	public void doFilter(ServletRequest req, ServletResponse resp,
			FilterChain chain) throws IOException, ServletException {
		String uri = ((HttpServletRequest)req).getRequestURI();
		if (uri.endsWith("/"))
		{
			((HttpServletResponse)resp).sendRedirect(uri + "index.html");
			return;
		}
		chain.doFilter(req, resp);
		
	}

	public void init(FilterConfig config) throws ServletException {

	}

}
