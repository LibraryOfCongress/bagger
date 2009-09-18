
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFile;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

public class AddTagFileHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public AddTagFileHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		addTagFile();
	}

    public void addTagFile() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
	    fo.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    if (bagView.bagRootPath != null) fo.setCurrentDirectory(bagView.bagRootPath.getParentFile());
		fo.setDialogTitle("Tag File Chooser");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fo.getSelectedFile();
            bag.addTagFile(file);
            bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
            Bag b = bag.getBag();
            Collection<BagFile> tags = b.getTags();
            for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
            	BagFile bf = it.next();
            	bagView.bagTagFileTree.addNode(bf.getFilepath());
            }
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            bagView.setBag(bag);
            bagView.compositePane.updateCompositePaneTabs(bag, "Tag file added.");
            bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
        }
    }

    public void addTagFiles(List<File> files) {
    	bag = bagView.getBag();
    	if (bagView.bagTagFileTree.isEnabled()) {
    		if (files != null) {
    			for (int i=0; i < files.size(); i++) {
    				//log.info("addBagData[" + i + "] " + files.get(i).getName());
    	            bag.addTagFile(files.get(i));
    			}
    			bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
	            Bag b = bag.getBag();
	            Collection<BagFile> tags = b.getTags();
	            for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
	            	BagFile bf = it.next();
	            	bagView.bagTagFileTree.addNode(bf.getFilepath());
	            }
	            bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    		}
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            bagView.setBag(bag);
            bagView.compositePane.updateCompositePaneTabs(bag, "Tag files changed.");
    	}
    }
}
