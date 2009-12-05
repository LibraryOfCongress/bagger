
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
    private File tmpRootPath;
    private boolean clearAfterSaving = false;
	private boolean validateOnSave = false;

	public SaveBagHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		if (bagView.getBagRootPath().exists()) {
			tmpRootPath = bagView.getBagRootPath();
			confirmWriteBag();
		} else {
			saveBag(bagView.getBagRootPath());
		}
	}

	public void setTask(LongTask task) {
	}

	public void execute() {
		boolean isValidating = false;
		while (!bagView.task.canceled && !bagView.task.done && !isValidating) {
			try {
				Thread.sleep(1000); //sleep for a second
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

				if (bagView.progressMonitor.isCanceled() || bagView.task.canceled) {
					bagView.progressMonitor.close();
					bagView.task.canceled = true;
					bagView.longRunningProcess.cancel();
					bagView.task.current = bagView.task.lengthOfTask;
					bag.isSerialized(false);
					bagView.showWarningErrorDialog("Save cancelled", "Save cancelled.");
				} else {
					if (messages != null && !messages.trim().isEmpty()) bagView.showWarningErrorDialog("Warning - bag not saved", "Problem saving bag:\n" + messages);
					else bagView.showWarningErrorDialog("Bag saved", "Bag saved successfully.\n" );
					if (bag.isSerialized()) {
						if (this.clearAfterSaving) {
							bag.isSerialized(false);
							bagView.statusBarEnd();
							bagView.clearBagHandler.clearExistingBag(bagView.getPropertyMessage("compositePane.message.clear"));
							this.setClearAfterSaving(false);
						} else {
							bag.isValidateOnSave(this.validateOnSave);
							if (bag.isValidateOnSave()) {
					        	isValidating = true;
								bagView.validateBagHandler.validateBag();
							}
							bagView.statusBarEnd();
							File bagFile = bag.getBagFileName();
							log.info("BagView.openExistingBag: " + bagFile);
							bagView.openBagHandler.openExistingBag(bagFile);
							bagView.updateSaveBag();
							bag.isNewbag(false);
						}
					} else {
						bagView.compositePane.updateCompositePaneTabs(bag, messages);
						bagView.updateManifestPane();
					}
				}
				if (bagView.task.current >= bagView.task.lengthOfTask) {
					bagView.task.done = true;
					bagView.task.current = bagView.task.lengthOfTask;
				}
			} catch (InterruptedException e) {
				bag.isSerialized(false);
				bagView.task.done = true;
				if (bagView.longRunningProcess.isCancelled()) {
					bagView.task.canceled = true;
					bagView.showWarningErrorDialog("Save cancelled", "Save cancelled.");
				} else {
					bagView.showWarningErrorDialog("Warning - save interrupted", "Problem saving bag: " + bagView.getBagRootPath() + "\n" + e.getMessage());
				}
				e.printStackTrace();
			} catch (Exception e) {
				bag.isSerialized(false);
				bagView.task.done = true;
				if (bagView.longRunningProcess.isCancelled()) {
					bagView.task.canceled = true;
					bagView.showWarningErrorDialog("Save cancelled", "Save cancelled.");
				} else {
					bagView.showWarningErrorDialog("Error - bag not saved", "Error saving bag: " + bagView.getBagRootPath() + "\n" + e.getMessage());
				}
				e.printStackTrace();
			}
		}
		bagView.setBag(bag);
		bagView.statusBarEnd();
	}

	public void setTmpRootPath(File f) {
		this.tmpRootPath = f;
	}
	
	public File getTmpRootPath() {
		return this.tmpRootPath;
	}

	public void setValidateOnSave(boolean b) {
		this.validateOnSave = b;
	}
	
	public boolean getValidateOnSave() {
		return this.validateOnSave;
	}

	public void setClearAfterSaving(boolean b) {
		this.clearAfterSaving = b;
	}

	public boolean getClearAfterSaving() {
		return this.clearAfterSaving;
	}

    public void saveBag(File file) {
    	bag = bagView.getBag();
        bag.setRootDir(file);
        bagView.setBag(bag);
        bagView.statusBarBegin(this, "Writing bag...", 1L);
    }

    public void confirmWriteBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	    	boolean isCancel = true;
	        protected void onConfirm() {
	        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
	        		confirmAcceptBagSize();
	        	} else {
		        	bagView.setBagRootPath(tmpRootPath);
		        	saveBag(bagView.getBagRootPath());
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
    	clearAfterSaving = false;
    	saveBagAs();
    }

    public void confirmAcceptBagSize() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	bagView.setBagRootPath(tmpRootPath);
	        	saveBag(bagView.getBagRootPath());
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
    	fs.addChoosableFileFilter(bagView.infoInputPane.noFilter);
    	fs.addChoosableFileFilter(bagView.infoInputPane.zipFilter);
        fs.addChoosableFileFilter(bagView.infoInputPane.tarFilter);
        fs.setDialogTitle("Save Bag As");
    	fs.setCurrentDirectory(bag.getRootDir());
    	if (bag.getName() != null && !bag.getName().equalsIgnoreCase(bagView.getPropertyMessage("bag.label.noname"))) {
    		String selectedName = bag.getName();
    		if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
    			selectedName += "."+DefaultBag.ZIP_LABEL;
    			fs.setFileFilter(bagView.infoInputPane.zipFilter);
    		}
    		else if (bag.getSerialMode() == DefaultBag.TAR_MODE) {
    			selectedName += "."+DefaultBag.TAR_LABEL;
    			fs.setFileFilter(bagView.infoInputPane.tarFilter);
    		}
    		else {
    			fs.setFileFilter(bagView.infoInputPane.noFilter);
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
        if (file == null) file = bagView.getBagRootPath();
        bag.setName(file.getName());
		File bagFile = new File(file, bag.getName());
    	if (bagFile.exists()) {
    		tmpRootPath = file;
            confirmWriteBag();
    	} else {
        	if (bag.getSize() > DefaultBag.MAX_SIZE) {
        		tmpRootPath = file;
        		confirmAcceptBagSize();
        	} else {
        		bagView.setBagRootPath(file);
        		saveBag(bagView.getBagRootPath());
        	}
    	}
        String fileName = bagFile.getName(); //bagFile.getAbsolutePath();
        bagView.infoInputPane.setBagName(fileName);
        bagView.getControl().invalidate();
        bagView.enableSettings(true);
    }
}
