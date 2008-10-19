
package gov.loc.repository.bagger;

import gov.loc.repository.bagger.bag.Bag;
import java.awt.Dimension;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.setup.SetupWizard;
import org.springframework.richclient.command.ActionCommand;

/**
 * Custom application lifecycle implementation that configures the app at well defined points within
 * its lifecycle.
 *
 * @author Jon Steinbach
 */
public class BaggerLifecycleAdvisor extends DefaultApplicationLifecycleAdvisor
{
	private static final Log log = LogFactory.getLog(BaggerLifecycleAdvisor.class);

	boolean useWizard = false;

    /**
     * Show a setup wizard before actual applicationWindow is created. This should happen only on Application
     * startup and only once. (note: for this to happen only once, a state should be preserved, which is not
     * the case with this sample)
     */
    public void onPreStartup()
    {
    	if (useWizard) {
        	if (getApplication().getApplicationContext().containsBean("setupWizard"))
            {
                SetupWizard setupWizard = (SetupWizard) getApplication().getApplicationContext().getBean(
                        "setupWizard", SetupWizard.class);
                setupWizard.execute();
            }
    	}
    }

    /**
     * Additional window configuration before it is created.
     */
    public void onPreWindowOpen(ApplicationWindowConfigurer configurer)
    {
        super.onPreWindowOpen(configurer);
        // comment out to hide the menubar, or reduce window size...
        //configurer.setShowMenuBar(false);
        configurer.setInitialSize(new Dimension(1024, 768));
    }

    /**
     * When commands are created, lookup the login command and execute it.
     */
    public void onCommandsCreated(ApplicationWindow window)
    {
        ActionCommand command = (ActionCommand) window.getCommandManager().getCommand("loginCommand", ActionCommand.class);
//        command.execute();
    }
}