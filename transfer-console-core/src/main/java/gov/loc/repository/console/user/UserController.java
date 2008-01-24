package gov.loc.repository.console.user;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.jbpm.JbpmContext;
import org.springframework.web.servlet.ModelAndView;

import gov.loc.repository.console.AbstractRestController;
import gov.loc.repository.console.workflow.beans.UserBean;
import gov.loc.repository.console.workflow.beans.UserHelper;

public class UserController extends AbstractRestController {

	@SuppressWarnings("unchecked")
	@Override
	protected ModelAndView handleGet(HttpServletRequest request, HttpServletResponse response, JbpmContext jbpmContext, Map<String, String> urlParameterMap) throws Exception {
		Map model = new HashMap();
		if (! urlParameterMap.containsKey(PARAMETER_USER))
		{
			model.put("userBeanList", UserHelper.getUserBeanList(jbpmContext));
			return new ModelAndView(".user.list", "model", model);
		}
		String userId = urlParameterMap.get(PARAMETER_USER);
		if (! UserHelper.exists(userId, jbpmContext))
		{
			response.sendError(HttpServletResponse.SC_NOT_FOUND);
			return null;
		}
		
		//TODO:  This needs to be built out further
		UserBean userBean = new UserBean();
		userBean.setId(userId);
		userBean.setJbpmContext(jbpmContext);
		model.put("userBean", userBean);
		
		return new ModelAndView(".user.item", "model", model);
	}
}
