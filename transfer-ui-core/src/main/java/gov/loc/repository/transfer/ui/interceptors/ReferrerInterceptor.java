package gov.loc.repository.transfer.ui.interceptors;

import gov.loc.repository.transfer.ui.UIConstants;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ReferrerInterceptor extends HandlerInterceptorAdapter {

	static final Log log = LogFactory.getLog(ReferrerInterceptor.class);
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null && ! request.getRequestURI().contains("/login"))
		{
			String referer = request.getRequestURI().substring(request.getContextPath().length());
			if (request.getQueryString() != null)
			{
				referer +=  "?" + request.getQueryString();
			}
			if (request.getParameter(UIConstants.PARAMETER_REF) != null)
			{
				referer += "#" + request.getParameter(UIConstants.PARAMETER_REF);
			}
			log.debug("Setting referer to " + referer);
			modelAndView.addObject("referer", referer);
		}
	}
	
}
