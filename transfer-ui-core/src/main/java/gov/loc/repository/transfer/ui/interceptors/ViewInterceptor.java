package gov.loc.repository.transfer.ui.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

public class ViewInterceptor extends HandlerInterceptorAdapter {

	String viewName;
	
	@Required
	public void setView(String viewName)
	{
		this.viewName = viewName;
	}
	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null)
		{
			modelAndView.setViewName(viewName);
		}
	}
	
}
