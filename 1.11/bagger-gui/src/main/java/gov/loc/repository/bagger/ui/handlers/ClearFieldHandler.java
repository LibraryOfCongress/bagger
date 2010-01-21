
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

public class ClearFieldHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public ClearFieldHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bagView.bagProject.clearProfiles();
		bagView.infoInputPane.showTabPane(1);
	}
}
