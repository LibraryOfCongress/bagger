
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class CompleteBagHandler extends AbstractAction implements Progress {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public CompleteBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		completeBag();
	}

    public void completeBag() {
    	bagView.statusBarBegin(this, "Checking if complete...", 1L);
    }

    public void setTask(LongTask task) {
	}

	public void execute() {
		bag = bagView.getBag();
    	while (!bagView.task.canceled && !bagView.task.done) {
            try {
                Thread.sleep(1000); //sleep for a second

                CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();
                completeVerifier.addProgressListener(bagView.task);
        		bagView.longRunningProcess = completeVerifier;

        		Bag completeBag = bag.getBag();
                String messages = bag.completeBag(completeVerifier, completeBag);
        	    if (messages != null && !messages.trim().isEmpty()) {
        	    	bagView.showWarningErrorDialog("Warning - incomplete", "Is complete result: " + messages);
        	    	bagView.task.current = bagView.task.lengthOfTask;
        	    }
        	    else {
        	    	bagView.showWarningErrorDialog("Is Complete Dialog", "Bag is complete.");
        	    	bagView.task.current = bagView.task.lengthOfTask;
        	    }
            	bagView.setBag(bag);
            	bagView.compositePane.updateCompositePaneTabs(bag, messages);
                if (bagView.task.current >= bagView.task.lengthOfTask) {
                	bagView.task.done = true;
                	bagView.task.current = bagView.task.lengthOfTask;
                }
                bagView.task.statMessage = "Completed " + bagView.task.current +
                              " out of " + bagView.task.lengthOfTask + ".";
            } catch (InterruptedException e) {
            	e.printStackTrace();
            	bagView.task.current = bagView.task.lengthOfTask;
        	    bagView.showWarningErrorDialog("Warning - complete check interrupted", "Error checking bag completeness: " + e.getMessage());
            }
        }
    	bagView.statusBarEnd();
	}
}
