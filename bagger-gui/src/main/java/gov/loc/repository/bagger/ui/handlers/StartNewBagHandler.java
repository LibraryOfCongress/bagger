
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.NewBagFrame;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.Iterator;

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
    	bag = bagView.getBag();
    	NewBagFrame newBagFrame = new NewBagFrame(bagView, bagView.getPropertyMessage("bag.frame.new"));
        newBagFrame.setBag(bag);
        newBagFrame.setVisible(true);
    }

    public void createNewBag() {
    	bag = bagView.getBag();
    	String messages = "";
    	bagView.bagCount++;

    	bagView.bagInfoInputPane.enableForms(bag, true);
    	bagView.clearBagHandler.clearExistingBag(messages);
    	bagView.bagVersionList.setSelectedItem(bagView.bagVersionValue.getText());
    	bagView.bagVersionValue.setText(bagView.bagVersionValue.getText());

    	String bagName = bagView.getPropertyMessage("bag.label.noname");
		bag.setName(bagName);
		bagView.bagNameField.setText(bagName);
		bagView.bagNameField.setCaretPosition(bagName.length()-1);
		bagView.enableSettings(false);
		bag.setRootDir(bagView.bagRootPath);
		messages = bagView.updateBaggerRules();
		bagView.initializeProfile();

        Bag b = bag.getBag();
        bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
        Collection<BagFile> tags = b.getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
        	bagView.bagTagFileTree.addNode(bf.getFilepath());
        }
        bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
        bagView.showTagButton.setEnabled(true);
        bagView.enableBagSettings(true);
        bag.isClear(false);
		bag.getInfo().setBag(bag);
		bagView.bagInfoInputPane.populateForms(bag, true);
        //bagInfoInputPane.updateSelected(bag);
		bagView.compositePane.updateCompositePaneTabs(bag, messages);

		bag.isNewbag(true);
		bagView.addDataButton.setEnabled(true);
		bagView.addDataExecutor.setEnabled(true);
		bagView.addTagFileButton.setEnabled(true);
		bagView.closeButton.setEnabled(true);
		bagView.removeTagFileButton.setEnabled(true);
		bagView.bagButtonPanel.invalidate();
    	bagView.setBag(bag);
    }
}
