package gov.loc.repository.transfer.ui.interceptors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import static gov.loc.repository.transfer.ui.UIConstants.SESSION_MESSAGE;

public class MessageInterceptor extends HandlerInterceptorAdapter {

	
	@Override
	public void postHandle(HttpServletRequest request,
			HttpServletResponse response, Object handler,
			ModelAndView modelAndView) throws Exception {
		if (modelAndView != null && ! modelAndView.getViewName().startsWith("redirect") && ! modelAndView.getModel().containsKey(SESSION_MESSAGE) && request.getSession() != null && request.getSession().getAttribute(SESSION_MESSAGE) != null)
		{
			//Make a copy
			modelAndView.addObject(SESSION_MESSAGE, new String((String)request.getSession().getAttribute(SESSION_MESSAGE)));
			//Clear
			request.getSession().removeAttribute(SESSION_MESSAGE);
		}
	}
	
}
