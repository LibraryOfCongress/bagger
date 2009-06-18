
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagit.BagFile;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

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
        bagView.updateBagInfoInputPaneMessages(messages);
        messages += bagView.updateProfile();
//        bagView.compositePane.updateCompositePaneTabs(bag, messages);
//        bagView.tagManifestPane.updateCompositePaneTabs(bag);
        bagView.updatePropButton.setEnabled(false);
	}
}
