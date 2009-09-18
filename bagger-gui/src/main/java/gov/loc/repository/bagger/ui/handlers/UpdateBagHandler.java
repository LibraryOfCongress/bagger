
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UpdateBagHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public UpdateBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}
	
	public void setBagView(BagView bagView) {
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		this.bag = bagView.getBag();
		updateBag(this.bag);
	}

	public void updateBag(DefaultBag bag) {
        String messages = "";
		messages = bagView.bagInfoInputPane.updateForms(bag);
        messages += bagView.bagProject.updateProfile();
        bagView.updatePropButton.setEnabled(false);
	}
}
