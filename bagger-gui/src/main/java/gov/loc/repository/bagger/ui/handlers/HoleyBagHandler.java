
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JCheckBox;

public class HoleyBagHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public HoleyBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		this.bag = bagView.getBag();

		JCheckBox cb = (JCheckBox)e.getSource();

		// Determine status
		boolean isSelected = cb.isSelected();
		if (isSelected) {
			bag.isHoley(true);
		} else {
			bag.isHoley(false);
		}
/*
		String messages = "";
		bagView.updateBaggerRules();
		
        bagView.bagInfoInputPane.populateForms(bag, true);
        messages = bagView.bagInfoInputPane.updateForms(bag);
        bagView.updateBagInfoInputPaneMessages(messages);
        bagView.bagInfoInputPane.update(bag);
        
		bagView.bagInfoInputPane.updateSelected(bag);
		bagView.compositePane.updateCompositePaneTabs(bag, messages);
		bagView.tagManifestPane.updateCompositePaneTabs(bag);
		bagView.setBag(bag);
*/
   	}
}
