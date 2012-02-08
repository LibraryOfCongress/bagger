
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
import gov.loc.repository.bagit.impl.AbstractBagConstants;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;

import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;

public class ClearBagHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	private boolean confirmSaveFlag = false;

	public ClearBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		closeExistingBag();
	}

    public void closeExistingBag() {
    	// Closes Bag without popping up the Save Dialog Box for a Holey and Serialized Bag 
    	// For all other types of Bags the Save Dialog Box pops up
    	if (bagView.getBag().isHoley() || bagView.getBag().isSerial())
        	clearExistingBag();
    	else    		    	
    		confirmCloseBag();
	    if (isConfirmSaveFlag()){
        	bagView.saveBagHandler.setClearAfterSaving(true);
    		bagView.saveBagAsHandler.openSaveBagAsFrame();
    		setConfirmSaveFlag(false);
	    }


    }

    private void confirmCloseBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	setConfirmSaveFlag(true);
	        }
	        protected void onCancel() {
        		super.onCancel();
	        	clearExistingBag();
	        }
	    };
	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(bagView.getPropertyMessage("bag.dialog.title.close"));
	    dialog.setConfirmationMessage(bagView.getPropertyMessage("bag.dialog.message.close"));
	    dialog.showDialog();
	}
    
    public void clearExistingBag() {
    	newDefaultBag(null);
    	DefaultBag bag = bagView.getBag();
    	bag.clear();
        bagView.baggerRules.clear();
    	bagView.bagPayloadTree = new BagTree(bagView, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    	bagView.bagTagFileTree = new BagTree(bagView, ApplicationContextUtil.getMessage("bag.label.noname"), false);
    	bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    	bagView.infoInputPane.setBagName(bag.getName());
    	bagView.infoInputPane.updateInfoForms();
    	bagView.updateClearBag();
    }

    public void newDefaultBag(File f) {
    	DefaultBag bag = null;
    	String bagName = "";
    	try {
        	bag = new DefaultBag(f, bagView.infoInputPane.getBagVersion());
    	} catch (Exception e) {
        	bag = new DefaultBag(f, null);    		
    	}
    	if (f == null) {
        	bagName = bagView.getPropertyMessage("bag.label.noname");
    	} else {
	    	bagName = f.getName();
	        String fileName = f.getAbsolutePath();
	        bagView.infoInputPane.setBagName(fileName);
    	}
		bag.setName(bagName);
		bagView.setBag(bag);
    }

	public void setConfirmSaveFlag(boolean confirmSaveFlag) {
		this.confirmSaveFlag = confirmSaveFlag;
	}

	public boolean isConfirmSaveFlag() {
		return confirmSaveFlag;
	}
}
