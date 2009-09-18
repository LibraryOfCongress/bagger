
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.LongTask;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
import gov.loc.repository.bagit.writer.impl.TarBz2Writer;
import gov.loc.repository.bagit.writer.impl.TarGzWriter;
import gov.loc.repository.bagit.writer.impl.TarWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;

public class SaveBagHandler extends AbstractAction implements Progress {
	private static final Log log = LogFactory.getLog(SaveBagHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public SaveBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		if (bagView.bagRootPath.exists()) {
			bagView.tmpRootPath = bagView.bagRootPath;
			confirmWriteBag();
		} else {
			saveBag(bagView.bagRootPath);
		}
	}

	public void setTask(LongTask task) {
	}

	public void execute() {
		//bag = bagView.getBag();
		while (!bagView.task.canceled && !bagView.task.done) {
			try {
				Thread.sleep(1000); //sleep for a second
				/* */
				short mode = bag.getSerialMode();
				if (mode == DefaultBag.NO_MODE) {
					bagView.bagWriter = new FileSystemWriter(bag.getBagFactory());
				} else if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
					bagView.bagWriter = new ZipWriter(bag.getBagFactory());
				} else if (mode == DefaultBag.TAR_MODE) {
					bagView.bagWriter = new TarWriter(bag.getBagFactory());
				} else if (mode == DefaultBag.TAR_GZ_MODE) {
					bagView.bagWriter = new TarGzWriter(bag.getBagFactory());
				} else if (mode == DefaultBag.TAR_BZ2_MODE) {
					bagView.bagWriter = new TarBz2Writer(bag.getBagFactory());
				}
				bagView.bagWriter.addProgressListener(bagView.task);
				bagView.longRunningProcess = bagView.bagWriter;
				String messages = bag.write(bagView.bagWriter);

				if (bagView.task.current >= bagView.task.lengthOfTask) {
					bagView.task.done = true;
					bagView.task.current = bagView.task.lengthOfTask;
				}
				if (messages != null && !messages.trim().isEmpty()) bagView.showWarningErrorDialog("Warning - bag not saved", "Problem saving bag:\n" + messages);
				else bagView.showWarningErrorDialog("Bag saved", "Bag saved successfully.\n" );
				if (bag.isSerialized()) {
					if (bagView.progressMonitor.isCanceled() || bagView.task.isDone()) {
						bagView.progressMonitor.close();
					}
					if (bagView.clearAfterSaving) {
						bag.isSerialized(false);
						bagView.statusBarEnd();
						bagView.clearBagHandler.clearExistingBag(bagView.getPropertyMessage("compositePane.message.clear"));
					} else {
						bag.isValidateOnSave(bagView.validateOnSave);
						if (bag.isValidateOnSave()) {
							bagView.validateBagHandler.validateBag();
						}
						bagView.statusBarEnd();
						File bagFile = bag.getBagFileName();
						log.info("BagView.openExistingBag: " + bagFile);
						bagView.openBagHandler.openExistingBag(bagFile);
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
						bagView.clearExecutor.setEnabled(true);
						bagView.validateExecutor.setEnabled(true);
						bagView.completeExecutor.setEnabled(true);
						bagView.topButtonPanel.invalidate();
						bag.isNewbag(false);
					}
				} else {
					bagView.compositePane.updateCompositePaneTabs(bag, messages);
					bagView.updateManifestPane();
				}
			} catch (InterruptedException e) {
				bagView.task.done = true;
				bag.isSerialized(false);
				if (bagView.longRunningProcess.isCancelled()) {
					bagView.showWarningErrorDialog("Save cancelled", "Save cancelled.");
				} else {
					bagView.showWarningErrorDialog("Warning - save interrupted", "Problem saving bag: " + bagView.bagRootPath + "\n" + e.getMessage());
				}
				e.printStackTrace();
			} catch (Exception e) {
				bag.isSerialized(false);
				if (bagView.longRunningProcess.isCancelled()) {
					bagView.task.done = true;
					bagView.showWarningErrorDialog("Save cancelled", "Save cancelled.");
				} else {
					bagView.showWarningErrorDialog("Error - bag not saved", "Error saving bag: " + bagView.bagRootPath + "\n" + e.getMessage());
				}
				e.printStackTrace();
			}
		}
		bagView.setBag(bag);
		bagView.statusBarEnd();
	}

    public void saveBag(File file) {
    	bag = bagView.getBag();
        bag.setRootDir(file);
        bagView.statusBarBegin(this, "Writing bag...", 1L);
    }

    public void confirmWriteBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	    	boolean isCancel = true;
	        protected void onConfirm() {
	        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
	        		confirmAcceptBagSize();
	        	} else {
		        	bagView.bagRootPath = bagView.tmpRootPath;
		        	saveBag(bagView.bagRootPath);
	        	}
	        }
	        protected void onCancel() {
        		super.onCancel();
	        	if (isCancel) {
	        		cancelWriteBag();
	        		isCancel = false;
	        	}
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(bagView.getPropertyMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(bagView.getPropertyMessage("bag.dialog.message.create"));
	    dialog.showDialog();
	}

    private void cancelWriteBag() {
    	bagView.clearAfterSaving = false;
    	saveBagAs();
    }

    public void confirmAcceptBagSize() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	bagView.bagRootPath = bagView.tmpRootPath;
	        	saveBag(bagView.bagRootPath);
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle(bagView.getPropertyMessage("bag.dialog.title.create"));
	    dialog.setConfirmationMessage(bagView.getPropertyMessage("bag.dialog.message.accept"));
	    dialog.showDialog();
	}

    public void saveBagAs() {
    	bag = bagView.getBag();
        File selectFile = new File(File.separator+".");
        JFrame frame = new JFrame();
        JFileChooser fs = new JFileChooser(selectFile);
    	fs.setDialogType(JFileChooser.SAVE_DIALOG);
    	fs.setFileSelectionMode(JFileChooser.FILES_ONLY);
    	fs.addChoosableFileFilter(bagView.noFilter);
    	fs.addChoosableFileFilter(bagView.zipFilter);
        fs.addChoosableFileFilter(bagView.tarFilter);
        fs.setDialogTitle("Save Bag As");
    	fs.setCurrentDirectory(bag.getRootDir());
    	if (bag.getName() != null && !bag.getName().equalsIgnoreCase(bagView.getPropertyMessage("bag.label.noname"))) {
    		String selectedName = bag.getName();
    		if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
    			selectedName += "."+DefaultBag.ZIP_LABEL;
    			fs.setFileFilter(bagView.zipFilter);
    		}
    		else if (bag.getSerialMode() == DefaultBag.TAR_MODE) {
    			selectedName += "."+DefaultBag.TAR_LABEL;
    			fs.setFileFilter(bagView.tarFilter);
    		}
    		else {
    			fs.setFileFilter(bagView.noFilter);
    		}
    		fs.setSelectedFile(new File(selectedName));
    	}
    	int	option = fs.showSaveDialog(frame);

        if (option == JFileChooser.APPROVE_OPTION) {
            File file = fs.getSelectedFile();
            save(file);
        }
    }

    public void save(File file) {
    	bag = bagView.getBag();
        if (file == null) file = bagView.bagRootPath;
        bag.setName(file.getName());
		File bagFile = new File(file, bag.getName());
    	if (bagFile.exists()) {
    		bagView.tmpRootPath = file;
            confirmWriteBag();
    	} else {
        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
        		bagView.tmpRootPath = file;
        		confirmAcceptBagSize();
        	} else {
        		bagView.bagRootPath = file;
        		saveBag(bagView.bagRootPath);
        	}
    	}
        String fileName = bagFile.getName(); //bagFile.getAbsolutePath();
        bagView.bagNameField.setText(fileName);
        bagView.getControl().invalidate();
        bagView.bagNameField.setCaretPosition(fileName.length()-1);
        bagView.enableSettings(true);
    }
}
