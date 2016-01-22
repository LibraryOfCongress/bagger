package gov.loc.repository.bagger;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.command.support.ExitCommand;

/**
 * Custom application lifecycle implementation that configures the app at well
 * defined points within
 * its lifecycle.
 *
 * @author Jon Steinbach
 */
public class BaggerExit extends ExitCommand {
  protected static final Logger log = LoggerFactory.getLogger(BaggerExit.class);

  /**
   * Closes the single {@link Application} instance.
   *
   * @see Application#close()
   */
  @Override
  public void doExecuteCommand() {
    super.doExecuteCommand();
    log.debug("BaggerExit.doExecuteCommand");
    System.exit(0);
  }
}
