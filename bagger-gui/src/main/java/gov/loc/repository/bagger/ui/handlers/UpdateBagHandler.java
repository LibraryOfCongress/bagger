
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

public class UpdateBagHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(UpdateBagHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public UpdateBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		this.bag = bagView.getBag();
		updateBag();
	}

    private void updateBag() {
        String messages = bagView.bagInfoInputPane.updateForms(bag);
        bagView.updateBagInfoInputPaneMessages(messages);
        bagView.bagInfoInputPane.updateSelected(bag);
        messages += bagView.updateProfile();
        bagView.compositePane.updateCompositePaneTabs(bag, messages);
        bagView.tagManifestPane.updateCompositePaneTabs(bag);
        bag.copyFieldsToBag();
        //bag.copyFormToBag();
        bagView.setBag(bag);
        bagView.updatePropButton.setEnabled(false);
    }
}
