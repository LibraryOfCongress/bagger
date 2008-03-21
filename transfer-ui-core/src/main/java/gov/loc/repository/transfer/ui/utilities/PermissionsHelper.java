package gov.loc.repository.transfer.ui.utilities ;

import gov.loc.repository.transfer.ui.UIConstants;

import javax.servlet.http.HttpServletRequest;

public class PermissionsHelper {
	HttpServletRequest req;
	public PermissionsHelper(HttpServletRequest req) {
		this.req = req;
	}
	
	public boolean canAddComment() {
		return isLoggedIn();
	}
	public boolean canAdministerJobListener() {
		return isAdministrator();
	}
	public boolean canStartProcess() {
		return isLoggedIn();
	}
	public boolean canReassignTask() {
		return isLoggedIn();
	}
	public boolean canUpdateVariables() {
		return isAdministrator();
	}
	public boolean canMoveToken() {
		return isAdministrator();
	}
	public boolean canSuspendProcess() {
		return isAdministrator();
	}
	public boolean canUpdateTask(String userName){
		return ( 
		    isAdministrator()  || (  
		    isLoggedIn()  &&
		    req.getUserPrincipal().getName().equals(
		        userName
		    ))
		);
	}
	private boolean isAdministrator() {
		return req.isUserInRole(UIConstants.ROLE_ADMINISTRATOR);
	}
	private boolean isLoggedIn() {
		if (req.getUserPrincipal() != null){
			return true;
		} return false;
	}
}
