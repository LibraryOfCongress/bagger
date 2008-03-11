package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.identity.User;
import org.jbpm.identity.hibernate.IdentitySession;

public class UserHelper {
	@SuppressWarnings("unchecked")
	public static List<UserBean> getUserBeanList(JbpmContext jbpmContext)
	{
		IdentitySession identitySession = new IdentitySession(jbpmContext.getSession());
		return toUserBeanList(identitySession.getUsers().iterator(), jbpmContext);
	}
	
	public static boolean exists(String userId, JbpmContext jbpmContext)
	{
		IdentitySession identitySession = new IdentitySession(jbpmContext.getSession());
		if (identitySession.getUserByName(userId) != null) {
			return true;
		}
		return false;
	}
	
	/*
	public static List<UserBean> getRoleLimitedActorList(JbpmContext jbpmContext, ProcessBean processBean, SecurityRoleType roleType )
	{
		String role = processBean.getRepository() + "-" + roleType.toString();
		IdentitySession identitySession = new IdentitySession(jbpmContext.getSession());
		Group group = identitySession.getGroupByName(role);
		return toActorBeanList(group.getUsers().iterator(), jbpmContext);
		
	}
	*/
	
	private static List<UserBean> toUserBeanList(Iterator<User> iter, JbpmContext jbpmContext)
	{
		List<UserBean> userBeanList = new ArrayList<UserBean>();
		while(iter.hasNext())
		{
			User user = iter.next();
			UserBean actorBean = new UserBean();
			actorBean.setJbpmContext(jbpmContext);
			actorBean.setId(user.getName());
			userBeanList.add(actorBean);
		}

		return userBeanList;
	}
}
