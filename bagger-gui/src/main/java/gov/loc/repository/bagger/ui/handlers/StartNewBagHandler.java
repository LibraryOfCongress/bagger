
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.NewBagFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class StartNewBagHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(StartNewBagHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public StartNewBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		newBag();
	}

    public void newBag() {
    	NewBagFrame newBagFrame = new NewBagFrame(bagView, bagView.getPropertyMessage("bag.frame.new"));
        newBagFrame.setBag(bag);
        newBagFrame.setVisible(true);
    }
}
