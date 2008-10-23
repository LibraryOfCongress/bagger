
package gov.loc.repository.bagger;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.command.ActionCommand;
import org.springframework.util.Assert;
import gov.loc.repository.bagger.Bagger;

public class SaveCommand extends ActionCommand
{
	private static final Log log = LogFactory.getLog(BaggerLifecycleAdvisor.class);
	private Bagger bagger;

	public void doExecuteCommand() {
		// TODO: Call this to persist in memory db to a file
		log.info("SaveCommand.doExecuteCommand");
        //Assert.notNull(bagger, "The bagger property is required");
	}

    public void setBagger(Bagger bagger) {
        log.info("SaveCommand.setBagger" );
        this.bagger = bagger;
    }
}
