
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;

import java.awt.event.ActionEvent;
import java.io.File;

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

	public AddDataHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void execute() {
		bagView.statusBarEnd();
	}

	public void actionPerformed(ActionEvent e) {
    	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
		addData();
    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
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
            String message = ApplicationContextUtil.getMessage("bag.message.filesadded");
            if (files != null && files.length >0) {
                addBagData(files);
                ApplicationContextUtil.addConsoleMessage(message + " " + getFileNames(files));
            } else {
            	File file = fc.getSelectedFile();
            	addBagData(file, true);
            	ApplicationContextUtil.addConsoleMessage(message + " " + file.getAbsolutePath());
            }
        	bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
			bagView.updateAddData();
        }
    }

    private String getFileNames(File[] files) {
    	StringBuffer stringBuff = new StringBuffer();
    	int totalFileCount = files.length;
    	int displayCount = 20;
    	if (totalFileCount < 20) {
    		displayCount = totalFileCount;
    	}
    	for (int i = 0; i < displayCount; i++) {
    		if (i != 0) {
    			stringBuff.append("\n");
    		}
    		stringBuff.append(files[i].getAbsolutePath());
    	}
    	if (totalFileCount > displayCount) {
    		stringBuff.append("\n" + (totalFileCount - displayCount) + " more...");
    	}
		return stringBuff.toString();
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
        try {
        	bagView.getBag().addFileToPayload(file);
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
    
}
