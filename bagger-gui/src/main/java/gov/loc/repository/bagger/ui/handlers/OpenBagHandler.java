
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.BaggerProfile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.ui.BagTree;
import gov.loc.repository.bagger.ui.BagView;
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
	DefaultBag bag;

	public OpenBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
    	bag = bagView.getBag();
		openBag();
	}

	public void openBag() {
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
            openExistingBag(file);
        }
	}

    public void openExistingBag(File file) {
    	String messages = "";
    	bagView.bagInfoInputPane.enableForms(bag, true);
    	bagView.clearBagHandler.clearExistingBag(messages);

		try {
	    	bagView.clearBagHandler.newDefaultBag(file);
		} catch (Exception ex) {
			log.error("openExistingBag DefaultBag: " + ex.getMessage());
        	messages +=  "Failed to create bag: " + ex.getMessage() + "\n";
    	    //showWarningErrorDialog("Warning - file not opened", "Error trying to open file: " + file + "\n" + ex.getMessage());
    	    return;
		}
		bag = bagView.getBag();
		bagView.enableSettings(true);
        bagView.bagVersionValue.setText(bag.getVersion());
        bagView.bagVersionList.setSelectedItem(bagView.bagVersionValue.getText());
        String fileName = file.getName();
        fileName = file.getAbsolutePath();
        bagView.bagNameField.setText(fileName);
        bagView.bagNameField.setCaretPosition(fileName.length());
        bagView.bagNameField.invalidate();

    	String s = file.getName();
	    int i = s.lastIndexOf('.');
	    if (i > 0 && i < s.length() - 1) {
	    	String sub = s.substring(i + 1).toLowerCase();
	    	if (sub.contains("gz")) {
	    		bagView.serializeValue.setText(DefaultBag.TAR_GZ_LABEL);
	    		bagView.tarGzButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_GZ_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains("bz2")) {
	    		bagView.serializeValue.setText(DefaultBag.TAR_BZ2_LABEL);
	    		bagView.tarBz2Button.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_BZ2_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains(DefaultBag.TAR_LABEL)) {
	    		bagView.serializeValue.setText(DefaultBag.TAR_LABEL);
	    		bagView.tarButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.TAR_MODE);
	    		bag.isSerial(true);
	    	} else if (sub.contains(DefaultBag.ZIP_LABEL)) {
	    		bagView.serializeValue.setText(DefaultBag.ZIP_LABEL);
	    		bagView.zipButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.ZIP_MODE);
	    		bag.isSerial(true);
	    	} else {
	    		bagView.serializeValue.setText(DefaultBag.NO_LABEL);
	    		bagView.noneButton.setSelected(true);
	    		bag.setSerialMode(DefaultBag.NO_MODE);
	    		bag.isSerial(false);
	    	}
	    } else {
	    	bagView.serializeValue.setText(DefaultBag.NO_LABEL);
	    	bagView.noneButton.setSelected(true);
    		bag.setSerialMode(DefaultBag.NO_MODE);
    		bag.isSerial(false);
	    }
	    bagView.serializeValue.invalidate();

	    if (bag.isHoley()) {
	    	bagView.holeyCheckbox.setSelected(true);
	    	bagView.holeyValue.setText("true");
	    	bagView.holeyValue.invalidate();
	    }

	    bag.isClear(false);
        bag.getInfo().setBag(bag);
    	bag.copyBagToForm();
    	bagView.bagProject.baggerProfile = new BaggerProfile();
	    if (!bag.getInfo().getLcProject().trim().isEmpty()){
    		String name = bag.getInfo().getLcProject().trim();
    		Project project = new Project();
    		project.setName(name);
    		if (!bagView.bagProject.projectExists(project)) {
    			bagView.bagProject.addProject(project);
    		}
    		messages += bagView.bagProject.updateProject(name);
    		bag.isNoProject(false);
    	} else {
    		messages += bagView.bagProject.updateProject(bagView.getPropertyMessage("bag.project.noproject"));
    		bag.isNoProject(true);
    	}
	    DefaultBagInfo bagInfo = bag.getInfo();
		bagInfo.createExistingFieldMap(true);
		bag.setInfo(bagInfo);
		bagView.bagProject.baggerProfile.setOrganization(bagInfo.getBagOrganization());
    	if (bagInfo.getBagSize() != null && bagInfo.getBagSize().isEmpty()) {
        	bag.setSize(bag.getDataSize());
    	} 
    	bagView.bagInfoInputPane.updateProject(bagView);
    	bag.copyBagToForm();
	    if (bag.getProject() != null && bag.getProject().getIsDefault()) {
	    	bagView.defaultProject.setSelected(true);
	    } else {
	    	bagView.defaultProject.setSelected(false);
	    }
		messages = bagView.updateBaggerRules();
		bagView.bagRootPath = file;
    	bag.setRootDir(bagView.bagRootPath);
		File rootSrc = new File(file, bag.getDataDirectory());
    	if (bag.getBag().getFetchTxt() != null) {
    		bagView.bagPayloadTree = new BagTree(bagView, bag.getFetch().getBaseURL(), true);
    		rootSrc = new File(file, bag.getBag().getFetchTxt().getFilepath());
    	} else {
    		bagView.bagPayloadTree = new BagTree(bagView, AbstractBagConstants.DATA_DIRECTORY, true);
    		rootSrc = new File(file, bag.getDataDirectory());
    	}
    	bagView.bagPayloadTree.populateNodes(bag, rootSrc, true);
    	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    	bagView.updateManifestPane();
    	bagView.enableBagSettings(true);
		bag.isSerialized(true);
        bag.isNewbag(false);
		String msgs = bag.validateMetadata();
		if (msgs != null) {
			if (messages != null) messages += msgs;
			else messages = msgs;
		}
		bagView.bagInfoInputPane.populateForms(bag, true);
		bagView.compositePane.updateCompositePaneTabs(bag, messages);
		bagView.setBag(bag);
		bagView.updateOpenBag();
		bagView.statusBarEnd();
    }
}
