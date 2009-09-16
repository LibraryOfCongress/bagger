
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.NewBagInPlaceFrame;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagger.ui.SaveBagFrame;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.AbstractAction;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CreateBagInPlaceHandler extends AbstractAction implements Progress {
	private static final Log log = LogFactory.getLog(CreateBagInPlaceHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public CreateBagInPlaceHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		createBagInPlace();
	}

	public void setBagView(BagView bagView) {
		this.bag = bagView.getBag();
	}

	public void setTask(LongTask task) {
	//	this.task = task;
	}

	public void execute() {
		bagView.statusBarEnd();
	}

    public void createBagInPlace() {
    	NewBagInPlaceFrame newBagInPlaceFrame = new NewBagInPlaceFrame(bagView, bagView.getPropertyMessage("bag.frame.newbaginplace"));
        newBagInPlaceFrame.setBag(bag);
        newBagInPlaceFrame.setVisible(true);
    }

    public void createPreBag(File data) {
    	bag = bagView.getBag();
    	String messages = "";
    	bagView.clearBagHandler.clearExistingBag(messages);
    	try {
    		bag.createPreBag(data, bagView.bagVersionValue.getText());
    	} catch (Exception e) {
    	    bagView.showWarningErrorDialog("Error - bagging in place", "No file or directory selection was made!\n");
    		return;
    	}
        bag.getInfo().setBag(bag);
    	bag.getBag().addFileToPayload(data);
    	boolean alreadyExists = bagView.bagPayloadTree.addNodes(data, false);
    	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);

    	File bagDir = data.getParentFile();
    	String bagFileName = "bag_" + data.getName();
    	bag.isClear(false);
        bag.setName(bagFileName);
        bagView.bagNameField.invalidate();
        File bagFile = new File(bagDir, bagFileName);
        bagView.setBag(bag);
        bagView.saveBagHandler.save(bagFile);

        bagView.compositePane.setBag(bag);
        bagView.compositePane.updateCompositePaneTabs(bag, bagView.getPropertyMessage("bag.message.filesadded"));
        bagView.updateManifestPane();
        bagView.enableBagSettings(true);
		bag.isSerialized(true);
		String msgs = bag.validateMetadata();
		if (msgs != null) {
			if (messages != null) messages += msgs;
			else messages = msgs;
		}
		bag.getInfo().setBag(bag);
        Bag b = bag.getBag();
        bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
        Collection<BagFile> tags = b.getTags();
        for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
        	bagView.bagTagFileTree.addNode(bf.getFilepath());
        }
        bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
        bagView.setBag(bag);

        bagView.addDataButton.setEnabled(true);
        bagView.addDataExecutor.setEnabled(true);
        bagView.updatePropButton.setEnabled(false);
        bagView.saveButton.setEnabled(false);
        bagView.saveBagExecutor.setEnabled(false);
        bagView.saveAsButton.setEnabled(true);
        bagView.saveBagAsExecutor.setEnabled(true);
        bagView.removeDataButton.setEnabled(true);
        bagView.addTagFileButton.setEnabled(true);
        bagView.removeTagFileButton.setEnabled(true);
        bagView.showTagButton.setEnabled(true);
        bagView.bagButtonPanel.invalidate();
        bagView.closeButton.setEnabled(true);
        bagView.validateButton.setEnabled(true);
        bagView.completeButton.setEnabled(true);
        bagView.completeExecutor.setEnabled(true);
        bagView.validateExecutor.setEnabled(true);
        bagView.bagButtonPanel.invalidate();
        bagView.topButtonPanel.invalidate();
        bag.isNewbag(true);
        bagView.bagInfoInputPane.populateForms(bag, true);
        bagView.compositePane.updateCompositePaneTabs(bag, messages);

        bagView.statusBarEnd();
    }
}
