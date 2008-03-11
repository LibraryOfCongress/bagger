package gov.loc.repository.transfer.ui.controllers;

import gov.loc.repository.transfer.ui.UIConstants;
import gov.loc.repository.transfer.ui.model.TaskInstanceBean;

import javax.servlet.http.HttpServletRequest;

public class PermissionsHelper {
	HttpServletRequest req;
	public PermissionsHelper(HttpServletRequest req) {
		this.req = req;
	}
	
	public boolean canAddComment()
	{
		return isLoggedIn();
	}
	
	public boolean canAdministerJobListener()
	{
		return isAdministrator();
	}
	
	public boolean canCreateProcessInstance()
	{
		return isLoggedIn();
	}
	
	public boolean canUpdateTaskInstanceUser()
	{
		return isLoggedIn();
	}
	
	public boolean canUpdateVariables()
	{
		return isAdministrator();
	}
	
	public boolean canMoveToken()
	{
		return isAdministrator();
	}
	
	public boolean canSuspendProcessInstance()
	{
		return this.isAdministrator();
	}
	
	public boolean canUpdateTaskInstance(TaskInstanceBean taskInstanceBean)
	{
		return (isAdministrator() || (isLoggedIn() && taskInstanceBean.getUserBean() != null && req.getUserPrincipal().getName().equals(taskInstanceBean.getUserBean().getId())));
	}
	
	private boolean isAdministrator()
	{
		return req.isUserInRole(UIConstants.ROLE_ADMINISTRATOR);
	}
	
	private boolean isLoggedIn()
	{
		if (req.getUserPrincipal() != null)
		{
			return true;
		}
		return false;
	}
}
