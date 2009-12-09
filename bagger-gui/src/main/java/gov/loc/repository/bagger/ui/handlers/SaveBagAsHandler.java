
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.SaveBagFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class SaveBagAsHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	private SaveBagFrame saveBagFrame;
	BagView bagView;
	DefaultBag bag;

	public SaveBagAsHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		openSaveBagAsFrame();
	}
	
	public void openSaveBagAsFrame() {
		bag = bagView.getBag();
		saveBagFrame = new SaveBagFrame(bagView, bagView.getPropertyMessage("bag.frame.save"));
		saveBagFrame.setBag(bag);
		saveBagFrame.setVisible(true);
	}
}
