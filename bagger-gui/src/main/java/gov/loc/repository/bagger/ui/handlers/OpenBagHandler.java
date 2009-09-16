
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OpenBagHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(OpenBagHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public OpenBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		openBag();
	}

	public void openBag() {
		bag = bagView.getBag();
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
    	fo.addChoosableFileFilter(bagView.noFilter);
    	fo.addChoosableFileFilter(bagView.zipFilter);
        fo.addChoosableFileFilter(bagView.tarFilter);
		fo.setFileFilter(bagView.noFilter);
	    fo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    if (bagView.bagRootPath != null) fo.setCurrentDirectory(bagView.bagRootPath.getParentFile());
		fo.setDialogTitle("Existing Bag Location");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fo.getSelectedFile();
            if (file == null) file = bagView.bagRootPath;
            bagView.openExistingBag(file);
            bagView.addDataButton.setEnabled(true);
            bagView.addDataExecutor.setEnabled(true);
            bagView.updatePropButton.setEnabled(false);
            bagView.saveButton.setEnabled(true);
            bagView.saveBagExecutor.setEnabled(true);
            bagView.saveAsButton.setEnabled(true);
            bagView.removeDataButton.setEnabled(true);
            bagView.addTagFileButton.setEnabled(true);
            bagView.removeTagFileButton.setEnabled(true);
            bagView.showTagButton.setEnabled(true);
            bagView.saveBagAsExecutor.setEnabled(true);
            bagView.bagButtonPanel.invalidate();
            bagView.closeButton.setEnabled(true);
            bagView.validateButton.setEnabled(true);
            bagView.completeButton.setEnabled(true);
            bagView.completeExecutor.setEnabled(true);
            bagView.validateExecutor.setEnabled(true);
            bagView.topButtonPanel.invalidate();
            bag.isNewbag(false);
            bagView.setBag(bag);
        }
	}
}
