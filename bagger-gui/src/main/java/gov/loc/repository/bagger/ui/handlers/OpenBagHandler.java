
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
import gov.loc.repository.bagit.impl.AbstractBagConstants;

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

	public OpenBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		openBag();
	}

	public void openBag() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
		JFileChooser fo = new JFileChooser(selectFile);
		fo.setDialogType(JFileChooser.OPEN_DIALOG);
    	fo.addChoosableFileFilter(bagView.infoInputPane.noFilter);
    	fo.addChoosableFileFilter(bagView.infoInputPane.zipFilter);
        fo.addChoosableFileFilter(bagView.infoInputPane.tarFilter);
		fo.setFileFilter(bagView.infoInputPane.noFilter);
	    fo.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
	    if (bagView.getBagRootPath() != null) fo.setCurrentDirectory(bagView.getBagRootPath().getParentFile());
		fo.setDialogTitle("Existing Bag Location");
    	int option = fo.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fo.getSelectedFile();
            if (file == null) file = bagView.getBagRootPath();
            openExistingBag(file);
        }
	}

    public void openExistingBag(File file) {
    	bagView.infoInputPane.bagInfoInputPane.enableForms(true);
    	bagView.clearBagHandler.clearExistingBag();

		try {
	    	bagView.clearBagHandler.newDefaultBag(file);
	    	ApplicationContextUtil.addConsoleMessage("Opened the bag " + file.getAbsolutePath());
		} catch (Exception ex) {
        	ApplicationContextUtil.addConsoleMessage("Failed to create bag: " + ex.getMessage());
    	    //showWarningErrorDialog("Warning - file not opened", "Error trying to open file: " + file + "\n" + ex.getMessage());
    	    return;
		}
		DefaultBag bag = bagView.getBag();
        bagView.infoInputPane.setBagVersion(bag.getVersion());
        bagView.infoInputPane.setProfile(bag.getProfile().getName());
        String fileName = file.getName();
        fileName = file.getAbsolutePath();
        bagView.infoInputPane.setBagName(fileName);

    	String s = file.getName();
	    int i = s.lastIndexOf('.');
	    if (i > 0 && i < s.length() - 1) {
	    	String sub = s.substring(i + 1).toLowerCase();
	    	if (sub.contains("gz")) {
	    		bagView.infoInputPane.serializeValue.setText(DefaultBag.TAR_GZ_LABEL);
	    		//bagView.infoInputPane.tarGzButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_GZ_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains("bz2")) {
	    		bagView.infoInputPane.serializeValue.setText(DefaultBag.TAR_BZ2_LABEL);
	    		//bagView.infoInputPane.tarBz2Button.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_BZ2_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains(DefaultBag.TAR_LABEL)) {
	    		bagView.infoInputPane.serializeValue.setText(DefaultBag.TAR_LABEL);
	    		//bagView.infoInputPane.tarButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains(DefaultBag.ZIP_LABEL)) {
	    		bagView.infoInputPane.serializeValue.setText(DefaultBag.ZIP_LABEL);
	    		//bagView.infoInputPane.zipButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.ZIP_MODE);
	    		bag.isSerial(true);
	    	} else {
	    		bagView.infoInputPane.serializeValue.setText(DefaultBag.NO_LABEL);
	    		//bagView.infoInputPane.noneButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.NO_MODE);
	    		bag.isSerial(false);
	    	}
	    } else {
	    	bagView.infoInputPane.serializeValue.setText(DefaultBag.NO_LABEL);
	    	//bagView.infoInputPane.noneButton.setSelected(true);
    		bag.setSerialMode(DefaultBag.NO_MODE);
    		bag.isSerial(false);
	    }
	    bagView.infoInputPane.serializeValue.invalidate();

	    if (bag.isHoley()) {
	    	bagView.infoInputPane.setHoley("true");
	    } else {
	    	bagView.infoInputPane.setHoley("false");
	    }
	    bagView.infoInputPane.holeyValue.invalidate();

		bagView.updateBaggerRules();
		bagView.setBagRootPath(file);
		File rootSrc = new File(file, bag.getDataDirectory());
		String path = null;
    	if (bag.getFetchTxt() != null) {
    		path = bag.getFetch().getBaseURL();
    		rootSrc = new File(file, bag.getFetchTxt().getFilepath());
    	} else {
    		path = AbstractBagConstants.DATA_DIRECTORY;
    		rootSrc = new File(file, bag.getDataDirectory());
    	}
    	bagView.bagPayloadTree.populateNodes(bag, path, rootSrc, true);
    	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    	bagView.updateManifestPane();
    	bagView.enableBagSettings(true);
		String msgs = bag.validateMetadata();
		if (msgs != null) {
			ApplicationContextUtil.addConsoleMessage(msgs);
		}
		bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
		bagView.updateOpenBag();
		bagView.statusBarEnd();
    }
}
