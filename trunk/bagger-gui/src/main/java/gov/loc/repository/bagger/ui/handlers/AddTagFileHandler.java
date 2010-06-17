
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.model.Status;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
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

	public AddTagFileHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		addTagFile();
	}

    public void addTagFile() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
	    fo.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    if (bagView.getBagRootPath() != null) fo.setCurrentDirectory(bagView.getBagRootPath().getParentFile());
		fo.setDialogTitle("Tag File Chooser");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
        	DefaultBag bag = bagView.getBag();
            File file = fo.getSelectedFile();
            bag.addTagFile(file);
            bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
            Collection<BagFile> tags = bag.getTags();
            for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
            	BagFile bf = it.next();
            	bagView.bagTagFileTree.addNode(bf.getFilepath());
            }
            bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
            ApplicationContextUtil.addConsoleMessage("Tag file added: " + file.getAbsolutePath());
        }
    }

    public void addTagFiles(List<File> files) {
    	DefaultBag bag = bagView.getBag();
    	if (bagView.bagTagFileTree.isEnabled()) {
    		if (files != null) {
    			for (int i=0; i < files.size(); i++) {
    				//log.info("addBagData[" + i + "] " + files.get(i).getName());
    	            bag.addTagFile(files.get(i));
    			}
    			bagView.bagTagFileTree = new BagTree(bagView, bag.getName(), false);
	            Collection<BagFile> tags = bag.getTags();
	            for (Iterator<BagFile> it=tags.iterator(); it.hasNext(); ) {
	            	BagFile bf = it.next();
	            	bagView.bagTagFileTree.addNode(bf.getFilepath());
	            }
	            bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    		}
            ApplicationContextUtil.addConsoleMessage("Tag files changed.");
    	}
    }
}
