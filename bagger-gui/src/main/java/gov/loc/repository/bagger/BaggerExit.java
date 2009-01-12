
package gov.loc.repository.bagger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.ExitCommand;

/**
 * Custom application lifecycle implementation that configures the app at well defined points within
 * its lifecycle.
 *
 * @author Jon Steinbach
 */
public class BaggerExit extends ExitCommand
{
	private static final Log log = LogFactory.getLog(BaggerExit.class);
    /**
     * Closes the single {@link Application} instance.
     *
     * @see Application#close()
     */
    public void doExecuteCommand() {
		super.doExecuteCommand();
		log.debug("BaggerExit.doExecuteCommand");
		System.exit(0);
    }
}
