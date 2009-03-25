
package gov.loc.repository.bagger;

import org.springframework.richclient.security.LoginCommand;

/**
 * Custom application lifecycle implementation that configures the app at well defined points within
 * its lifecycle.
 *
 * @author Jon Steinbach
 */
public class BaggerLogin extends LoginCommand
{
	public void doExecuteCommand() {
		super.doExecuteCommand();
	}
}
