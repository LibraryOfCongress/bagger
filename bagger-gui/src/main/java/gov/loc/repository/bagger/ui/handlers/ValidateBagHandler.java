
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.ActualTask;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.Progress;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

public class ValidateBagHandler extends AbstractAction implements Progress {
	private static final Log log = LogFactory.getLog(ValidateBagHandler.class);
   	private static final long serialVersionUID = 1L;
	private LongTask task;
	BagView bagView;
	DefaultBag bag;

	public ValidateBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}
	
	public void setBagView(BagView bagView) {
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		if (component != null) this.bagView = (BagView) component;
		else System.out.println("ValidateBagHandler error: " + component);
		this.bag = bagView.getBag();
		// TODO: there is no way to get bag from actionPerformed
		// because bagView is not retrieved from component
		validateBag("");
	}

    public void validateBag(String messages) {
    	ActualTask actualTask = new ActualTask();
    	messages += bagView.bagInfoInputPane.updateForms(bag);
    	bagView.updateBagInfoInputPaneMessages(messages);
    	bagView.bagInfoInputPane.updateSelected(bag);

    	bagView.statusBarBegin(actualTask, "Validating bag...", 1);

    	bagView.setBag(bag);
    	bagView.compositePane.updateCompositePaneTabs(bag, messages);
    	bagView.tagManifestPane.updateCompositePaneTabs(bag);
    	bagView.bagInfoInputPane.update(bag);
    	bagView.statusBarEnd();
    }

	public void setTask(LongTask task) {
		this.task = task;
	}

	public void execute() {
    	while (!task.canceled && !task.done) {
            try {
                Thread.sleep(1000); //sleep for a second
                task.current += Math.random() * 100; //make some progress
                
            	String messages = bag.validateBag(null);

            	bagView.statusBarEnd();
                task.current++;
                if (task.current >= task.lengthOfTask) {
                    task.done = true;
                    task.current = task.lengthOfTask;
                }
                task.statMessage = "Completed " + task.current +
                              " out of " + task.lengthOfTask + ".";
            } catch (InterruptedException e) {
            }
        }
	}
}
