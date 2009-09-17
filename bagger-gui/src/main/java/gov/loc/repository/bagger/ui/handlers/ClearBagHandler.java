
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Iterator;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;

public class ClearBagHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(ClearBagHandler.class);
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
    	bag = bagView.getBag();
    	bagView.clearAfterSaving = false;
    	bagView.bagInfoInputPane.enableForms(bag, false);
    	newDefaultBag(null);
    	bag.getInfo().setFieldMap(null);
    	bag.getInfo().setProfileMap(null);
        bagView.holeyCheckbox.setSelected(false);
        bagView.holeyValue.setText("false");
        bagView.baggerRules.clear();
        bagView.clearProfiles();
		bagView.updateProject(bagView.getPropertyMessage("bag.project.noproject"));
    	bag.isNewbag(true);
    	bagView.bagPayloadTree = new BagTree(bagView, AbstractBagConstants.DATA_DIRECTORY, true);
    	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    	bagView.bagTagFileTree = new BagTree(bagView, bagView.getPropertyMessage("bag.label.noname"), false);
    	bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    	bagView.enableBagSettings(false);
    	bagView.noneButton.setSelected(true);
    	bagView.addDataButton.setEnabled(false);
    	bagView.addDataExecutor.setEnabled(false);
    	bagView.updatePropButton.setEnabled(false);
    	bagView.saveButton.setEnabled(false);
    	bagView.saveBagExecutor.setEnabled(false);
    	bagView.saveAsButton.setEnabled(false);
    	bagView.saveBagAsExecutor.setEnabled(false);
    	bagView.removeDataButton.setEnabled(false);
    	bagView.showTagButton.setEnabled(false);
    	bagView.addTagFileButton.setEnabled(false);
    	bagView.removeTagFileButton.setEnabled(false);
    	bagView.closeButton.setEnabled(false);
    	bagView.validateButton.setEnabled(false);
    	bagView.completeButton.setEnabled(false);
    	bagView.clearExecutor.setEnabled(false);
    	bagView.validateExecutor.setEnabled(false);
    	bagView.completeExecutor.setEnabled(false);
    	bagView.bagButtonPanel.invalidate();
    	bagView.topButtonPanel.invalidate();

    	bagView.setBag(bag);
    	bagView.bagNameField.setText(bag.getName());
    	bagView.enableSettings(false);
    	bagView.bagInfoInputPane.populateForms(bag, false);
    	bagView.compositePane.updateCompositePaneTabs(bag, messages);
    }

    public void newDefaultBag(File f) {
    	String bagName = "";
    	bag = new DefaultBag(f, bagView.bagVersionValue.getText());
    	bag.isClear(true);
    	if (f == null) {
        	bagName = bagView.getPropertyMessage("bag.label.noname");
    	} else {
	    	bagName = f.getName();
	        String fileName = f.getAbsolutePath();
	        bagView.bagNameField.setText(fileName);
	        bagView.bagNameField.setCaretPosition(fileName.length()-1);
	        bagView.enableSettings(true);
    	}
		bag.setName(bagName);
		bagView.setBag(bag);
    }
}
