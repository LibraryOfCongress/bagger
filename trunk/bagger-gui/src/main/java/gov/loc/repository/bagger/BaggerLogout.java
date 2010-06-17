
package gov.loc.repository.bagger;

import org.acegisecurity.Authentication;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.security.LogoutCommand;

/**
 * Custom application lifecycle implementation that configures the app at well defined points within
 * its lifecycle.
 *
 * @author Jon Steinbach
 */
public class BaggerLogout extends LogoutCommand
{
	public void doExecuteCommand() {
		super.doExecuteCommand();
		Application.instance().getActiveWindow().getControl().invalidate();
	}

	public void onLogout(Authentication loggedOut) {
		super.onLogout(loggedOut);
		Application.instance().getActiveWindow().getControl().invalidate();
	}
}
