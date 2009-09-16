
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.ParallelManifestChecksumVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ValidateBagHandler extends AbstractAction implements Progress {
	private static final Log log = LogFactory.getLog(ValidateBagHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;
	//private LongTask task;

	public ValidateBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		validateBag();
	}

    public void validateBag() {
    	bagView.statusBarBegin(this, "Validating bag...", 1L);
    }

    public void setTask(LongTask task) {
//		this.task = task;
	}

	public void execute() {
		bag = bagView.getBag();
    	while (!bagView.task.canceled && !bagView.task.done) {
            try {
                Thread.sleep(1000); //sleep for a second
                /* */
        		CompleteVerifierImpl completeVerifier = new CompleteVerifierImpl();
        		completeVerifier.addProgressListener(bagView.task);
        		
        		ParallelManifestChecksumVerifier manifestVerifier = new ParallelManifestChecksumVerifier();
        		manifestVerifier.addProgressListener(bagView.task);
        		
        		ValidVerifierImpl validVerifier = new ValidVerifierImpl(completeVerifier, manifestVerifier);
        		validVerifier.addProgressListener(bagView.task);
        		bagView.longRunningProcess = validVerifier;
        		/* */
                String messages = bag.validateBag(validVerifier);
        	    if (messages != null && !messages.trim().isEmpty()) {
        	    	bagView.showWarningErrorDialog("Warning - validation failed", "Validation result: " + messages);
        	    	bagView.task.current = bagView.task.lengthOfTask;
        	    }
        	    else {
        	    	bagView.showWarningErrorDialog("Validation Dialog", "Validation successful.");
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
        	    bagView.showWarningErrorDialog("Warning - validation interrupted", "Error trying validate bag: " + e.getMessage());
            }
        }
    	bagView.statusBarEnd();
	}
}
