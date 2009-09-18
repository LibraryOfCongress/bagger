
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.BaggerProfile;
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

public class StartNewBagHandler extends AbstractAction {
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
    	String messages = "";
    	bagView.bagCount++;

    	bagView.clearBagHandler.clearExistingBag(messages);
    	bag = bagView.getBag();
		bagView.enableSettings(false);
    	bagView.infoInputPane.bagInfoInputPane.enableForms(bag, true);
    	bagView.infoInputPane.bagVersionList.setSelectedItem(bagView.infoInputPane.bagVersionValue.getText());
    	bagView.infoInputPane.bagVersionValue.setText(bagView.infoInputPane.bagVersionValue.getText());

    	String bagName = bagView.getPropertyMessage("bag.label.noname");
		bag.setName(bagName);
		bagView.infoInputPane.bagNameField.setText(bagName);
		bagView.infoInputPane.bagNameField.setCaretPosition(bagName.length()-1);

        Bag b = bag.getBag();
        bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
        Collection<BagFile> tags = b.getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
        	bagView.bagTagFileTree.addNode(bf.getFilepath());
        }
        bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
        bag.isClear(false);
		bag.getInfo().setBag(bag);
		bag.isNewbag(true);
    	bagView.bagProject.baggerProfile = new BaggerProfile();
		//bagView.bagProject.initializeProfile();
		messages = bagView.updateBaggerRules();
		bag.setRootDir(bagView.bagRootPath);

    	bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
		bagView.compositePane.updateCompositePaneTabs(bag, messages);
    	bagView.setBag(bag);
    	bagView.updateNewBag();
    }
}
