
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagit.impl.AbstractBagConstants;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;

public class ClearBagHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;
	//private LongTask task;

	public ClearBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		closeExistingBag();
	}

    public void closeExistingBag() {
    	confirmCloseBag();
    }

    private void confirmCloseBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	bagView.clearAfterSaving = true;
	    		bagView.saveBagAsHandler.openSaveBagAsFrame();
	        }
	        protected void onCancel() {
        		super.onCancel();
	        	clearExistingBag(bagView.getPropertyMessage("compositePane.message.clear"));
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(bagView.getPropertyMessage("bag.dialog.title.close"));
	    dialog.setConfirmationMessage(bagView.getPropertyMessage("bag.dialog.message.close"));
	    dialog.showDialog();
	}
    
    public void clearExistingBag(String messages) {
    	bagView.clearAfterSaving = false;
    	newDefaultBag(null);
    	bag.getInfo().setFieldMap(null);
    	bag.getInfo().setProfileMap(null);
    	bag.isNewbag(true);
    	bagView.setBag(bag);
        bagView.baggerRules.clear();
        bagView.bagProject.clearProfiles();
		bagView.bagProject.updateProject(bagView.getPropertyMessage("bag.project.noproject"));
    	bagView.bagPayloadTree = new BagTree(bagView, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    	bagView.bagTagFileTree = new BagTree(bagView, bagView.getPropertyMessage("bag.label.noname"), false);
    	bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    	bagView.infoInputPane.setBagName(bag.getName());
    	bagView.infoInputPane.updateInfoForms();
    	bagView.compositePane.updateCompositePaneTabs(bag, messages);
    	bagView.compositePane.invalidate();
    	bagView.updateClearBag(messages);
    }

    public void newDefaultBag(File f) {
    	String bagName = "";
    	bag = new DefaultBag(f, bagView.infoInputPane.getBagVersion());
    	bag.isClear(true);
    	if (f == null) {
        	bagName = bagView.getPropertyMessage("bag.label.noname");
    	} else {
	    	bagName = f.getName();
	        String fileName = f.getAbsolutePath();
	        bagView.infoInputPane.setBagName(fileName);
	        bagView.enableSettings(true);
    	}
		bag.setName(bagName);
    }
}
