
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.TagFilesFrame;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.JFrame;

import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationWindow;

public class ShowTagFilesHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	private TagFilesFrame tagFilesFrame;
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
    	ApplicationWindow window = Application.instance().getActiveWindow();
    	JFrame f = window.getControl();
		tagFilesFrame = new TagFilesFrame(f, bagView.getPropertyMessage("bagView.tagFrame.title"));
		tagFilesFrame.addComponents(bagView.tagManifestPane);
    	tagFilesFrame.addComponents(bagView.tagManifestPane);
    	tagFilesFrame.setVisible(true);
    	tagFilesFrame.setAlwaysOnTop(true);
    }
}
