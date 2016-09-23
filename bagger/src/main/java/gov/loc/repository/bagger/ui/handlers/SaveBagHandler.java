package gov.loc.repository.bagger.ui.handlers;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.writer.Writer;
import gov.loc.repository.bagit.writer.impl.FileSystemWriter;
//import gov.loc.repository.bagit.writer.impl.TarBz2Writer;
//import gov.loc.repository.bagit.writer.impl.TarGzWriter;
//import gov.loc.repository.bagit.writer.impl.TarWriter;
import gov.loc.repository.bagit.writer.impl.ZipWriter;

public class SaveBagHandler extends AbstractAction implements Progress {
  protected static final Logger log = LoggerFactory.getLogger(SaveBagHandler.class);
  private static final long serialVersionUID = 1L;
  private BagView bagView;
  private File tmpRootPath;
  private boolean clearAfterSaving = false;
  private String messages;

  public SaveBagHandler(BagView bagView) {
    super();
    this.bagView = bagView;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    DefaultBag bag = bagView.getBag();
    bagView.infoInputPane.updateBagHandler.updateBag(bag);
    if (bagView.getBagRootPath().exists()) {
      tmpRootPath = bagView.getBagRootPath();
      confirmWriteBag();
    }
    else {
      saveBag(bagView.getBagRootPath());
    }
  }

  @Override
  public void execute() {
    DefaultBag bag = bagView.getBag();

    try {
      BagFactory bagFactory = new BagFactory();
      Writer bagWriter = getWriter(bagFactory, bag);
      /*
         * else if (mode == DefaultBag.TAR_MODE) {
         * bagWriter = new TarWriter(bagFactory);
         * } else if (mode == DefaultBag.TAR_GZ_MODE) {
         * bagWriter = new TarGzWriter(bagFactory);
         * } else if (mode == DefaultBag.TAR_BZ2_MODE) {
         * bagWriter = new TarBz2Writer(bagFactory);
         * }
         */
      if(bagWriter != null){
        bagWriter.addProgressListener(bagView.task);
        bagView.longRunningProcess = bagWriter;
        messages = bag.write(bagWriter);

        if (messages != null && !messages.trim().isEmpty()) {
          bagView.showWarningErrorDialog("Warning - bag not saved", "Problem saving bag:\n" + messages);
        }
        else {
          bagView.showWarningErrorDialog("Bag saved", "Bag saved successfully.\n");
        }
      }
      else{
        bagView.showWarningErrorDialog("Warning - bag not saved", "Could not get writer for bag");
      }

      SwingUtilities.invokeLater(new Runnable() {

        @Override
        public void run() {
          DefaultBag bag = bagView.getBag();
          if (bag.isSerialized()) {
            if (clearAfterSaving) {
              bagView.statusBarEnd();
              bagView.clearBagHandler.clearExistingBag();
              setClearAfterSaving(false);
            }
            else {
              if (bag.isValidateOnSave()) {
                bagView.validateBagHandler.validateBag();
              }
              bagView.statusBarEnd();
              File bagFile = bag.getBagFile();
              log.info("BagView.openExistingBag: {}", bagFile);
              bagView.openBagHandler.openExistingBag(bagFile);
              bagView.updateSaveBag();
            }
          }
          else {
            ApplicationContextUtil.addConsoleMessage(messages);
            bagView.updateManifestPane();
          }
        }

      });
    }
    catch(Exception e){
      log.error("Failed to save bag", e);
      bagView.showWarningErrorDialog("Error - Failed to save bag", "Error trying to save bag due to:\n" + e.getMessage());
    }
    finally {
      bagView.task.done();
      bagView.statusBarEnd();
    }
  }
  
  protected Writer getWriter(BagFactory bagFactory, DefaultBag bag){
    if (bag.getSerialMode() == DefaultBag.NO_MODE) {
      return new FileSystemWriter(bagFactory);
    }
    else if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
      return new ZipWriter(bagFactory);
    }
    return null;
  }

  public void setTmpRootPath(File f) {
    this.tmpRootPath = f;
  }

  public File getTmpRootPath() {
    return this.tmpRootPath;
  }

  public void setClearAfterSaving(boolean b) {
    this.clearAfterSaving = b;
  }

  public boolean getClearAfterSaving() {
    return this.clearAfterSaving;
  }

  public void saveBag(File file) {
    DefaultBag bag = bagView.getBag();
    bag.setRootDir(file);
    bagView.statusBarBegin(this, "Writing bag...", null);
  }

  public void confirmWriteBag() {
    ConfirmationDialog dialog = new ConfirmationDialog() {
      boolean isCancel = true;

      @Override
      protected void onConfirm() {
        DefaultBag bag = bagView.getBag();
        if (bag.getSize() > DefaultBag.MAX_SIZE) {
          confirmAcceptBagSize();
        }
        else {
          bagView.setBagRootPath(tmpRootPath);
          saveBag(bagView.getBagRootPath());
        }
      }

      @Override
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
  }

  public void confirmAcceptBagSize() {
    ConfirmationDialog dialog = new ConfirmationDialog() {
      @Override
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
    DefaultBag bag = bagView.getBag();
    File selectFile = new File(File.separator + ".");
    JFrame frame = new JFrame();
    JFileChooser fs = new JFileChooser(selectFile);
    fs.setDialogType(JFileChooser.SAVE_DIALOG);
    fs.setFileSelectionMode(JFileChooser.FILES_ONLY);
    // fs.addChoosableFileFilter(bagView.infoInputPane.tarFilter);
    fs.setDialogTitle("Save Bag As");
    fs.setCurrentDirectory(bag.getRootDir());
    if (bag.getName() != null && !bag.getName().equalsIgnoreCase(bagView.getPropertyMessage("bag.label.noname"))) {
      String selectedName = bag.getName();
      if (bag.getSerialMode() == DefaultBag.ZIP_MODE) {
        selectedName += "." + DefaultBag.ZIP_LABEL;
      }
      fs.setSelectedFile(new File(selectedName));
    }
    int option = fs.showSaveDialog(frame);

    if (option == JFileChooser.APPROVE_OPTION) {
      File file = fs.getSelectedFile();
      save(file);
    }
  }

  public void save(File file) {
    DefaultBag bag = bagView.getBag();
    if (file == null){
      file = bagView.getBagRootPath();
    }
    bag.setName(file.getName());
    File bagFile = new File(file, bag.getName());
    if (bagFile.exists()) {
      tmpRootPath = file;
      confirmWriteBag();
    }
    else {
      if (bag.getSize() > DefaultBag.MAX_SIZE) {
        tmpRootPath = file;
        confirmAcceptBagSize();
      }
      else {
        bagView.setBagRootPath(file);
        saveBag(bagView.getBagRootPath());
      }
    }
    String fileName = bagFile.getAbsolutePath();
    bagView.infoInputPane.setBagName(fileName);
    bagView.getControl().invalidate();
  }
}
