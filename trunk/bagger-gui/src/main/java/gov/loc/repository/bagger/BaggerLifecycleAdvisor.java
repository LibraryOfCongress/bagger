
package gov.loc.repository.bagger;

import java.awt.Dimension;
import java.io.IOException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.ApplicationWindow;
import org.springframework.richclient.application.config.ApplicationWindowConfigurer;
import org.springframework.richclient.application.config.DefaultApplicationLifecycleAdvisor;
import org.springframework.richclient.application.docking.vldocking.VLDockingPageDescriptor;
import org.springframework.richclient.application.setup.SetupWizard;
//import org.springframework.richclient.command.ActionCommand;

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
    	log.debug("BaggerLifeCycleAdvisor.onPreStartup");
    	if (useWizard) {
        	if (getApplication().getApplicationContext().containsBean("setupWizard"))
            {
                SetupWizard setupWizard = (SetupWizard) getApplication().getApplicationContext().getBean(
                        "setupWizard", SetupWizard.class);
                setupWizard.execute();
            }
    	}
    	
    	//Make the view layout page as read-only so the user changes to view layout will 
    	//be reset during restart.
    	if(getApplication().getApplicationContext().containsBean("proxyPage"));
    	{
    		VLDockingPageDescriptor dockingPageDesc = (VLDockingPageDescriptor)getApplication().getApplicationContext().getBean("proxyPage");
    		try {
				dockingPageDesc.getInitialLayout().getFile().setReadOnly();
			} catch (Exception e) {
				log.debug("Error setting the view layout page as read-only", e);
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
        //ActionCommand command = (ActionCommand) window.getCommandManager().getCommand("loginCommand", ActionCommand.class);
        // TODO: implement login and logout if db is on remote server
        //command.execute();
    }
}