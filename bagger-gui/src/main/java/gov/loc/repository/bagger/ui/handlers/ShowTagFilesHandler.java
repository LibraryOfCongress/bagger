
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.NewBagFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ShowTagFilesHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(ShowTagFilesHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public ShowTagFilesHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
   		showTagFiles();
	}

    public void showTagFiles() {
    	bag = bagView.getBag();
    	bagView.tagManifestPane.updateCompositePaneTabs(bag);
    	bagView.tagFilesFrame.addComponents(bagView.tagManifestPane);
    	bagView.tagFilesFrame.setVisible(true);
    	bagView.tagFilesFrame.setAlwaysOnTop(true);
    }
}
