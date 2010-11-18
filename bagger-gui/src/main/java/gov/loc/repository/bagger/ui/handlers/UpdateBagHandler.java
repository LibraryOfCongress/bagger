
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class UpdateBagHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	private BagView bagView;

	public UpdateBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}
	
	public void setBagView(BagView bagView) {
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		updateBag(bagView.getBag());
	}

	public void updateBag(DefaultBag bag) {
		bagView.infoInputPane.bagInfoInputPane.updateForms(bag);
	}
}
