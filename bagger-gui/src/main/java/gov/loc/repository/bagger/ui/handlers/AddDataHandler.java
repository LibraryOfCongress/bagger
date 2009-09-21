
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.Progress;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.progress.BusyIndicator;

public class AddDataHandler extends AbstractAction implements Progress {
	private static final Log log = LogFactory.getLog(AddDataHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public AddDataHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void setTask(LongTask task) {
	}

	public void execute() {
		bagView.statusBarEnd();
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		addData();
	}

    public void addData() {
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fc = new JFileChooser(selectFile);
        fc.setDialogType(JFileChooser.OPEN_DIALOG);
    	fc.setMultiSelectionEnabled(true);
        fc.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        fc.setDialogTitle("Add File or Directory");
    	int option = fc.showOpenDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File[] files = fc.getSelectedFiles();
            if (files != null && files.length >0) {
                addBagData(files);
            } else {
            	File file = fc.getSelectedFile();
            	addBagData(file, true);
            }
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            bagView.setBag(bag);
        	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
        	bagView.compositePane.updateCompositePaneTabs(bag, bagView.getPropertyMessage("bag.message.filesadded"));
        	bagView.updateAddData();
        }
    }

    public void addBagData(File[] files) {
    	if (files != null) {
        	for (int i=0; i < files.length; i++) {
        		log.info("addBagData[" + i + "] " + files[i].getName());
        		if (i < files.length-1) addBagData(files[i], false);
        		else addBagData(files[i], true);
        	}
    	}
    }

    public void addBagData(File file, boolean lastFileFlag) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
    	//bagView.parentSrc = file.getParentFile().getAbsoluteFile();
        try {
        	bag.getBag().addFileToPayload(file);
        	boolean alreadyExists = bagView.bagPayloadTree.addNodes(file, false);
        	if (alreadyExists) {
        		bagView.showWarningErrorDialog("Warning - file already exists", "File: " + file.getName() + "\n" + "already exists in bag.");
        	}
        } catch (Exception e) {
        	log.error("BagView.addBagData: " + e);
        	bagView.showWarningErrorDialog("Error - file not added", "Error adding bag file: " + file + "\ndue to:\n" + e.getMessage());
        }
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
    }
    
    public void addPayloadData(List<File> files) {
    	bag = bagView.getBag();
    	if (bagView.bagPayloadTree.isEnabled()) {
        	if (files != null) {
            	for (int i=0; i < files.size(); i++) {
            		//log.info("addBagData[" + i + "] " + files.get(i).getName());
            		if (i < files.size()-1) addBagData(files.get(i), false);
            		else addBagData(files.get(i), true);
            	}
        	}
        	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
            bagView.setBag(bag);
            bagView.compositePane.updateCompositePaneTabs(bag, bagView.getPropertyMessage("bag.message.filesadded"));
            bagView.updateAddData();
    	}
    }

}
