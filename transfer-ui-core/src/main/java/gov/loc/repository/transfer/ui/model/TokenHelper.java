package gov.loc.repository.transfer.ui.model;

import org.jbpm.JbpmContext;
import org.jbpm.graph.exe.Token;

public class TokenHelper {
	public static TokenBean getTokenBean(long tokenId, JbpmContext jbpmContext)
	{
		Token token = jbpmContext.getToken(tokenId);
		if (token == null)
		{
			return null;
		}
		TokenBean tokenBean = new TokenBean();
		tokenBean.setJbpmContext(jbpmContext);
		tokenBean.setToken(token);
		return tokenBean;
	}
	
	public static boolean hasToken(long tokenId, JbpmContext jbpmContext)
	{
		if (jbpmContext.getToken(tokenId) != null)
		{
			return true;
		}
		return false;
	}
}
