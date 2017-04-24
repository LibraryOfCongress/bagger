package gov.loc.repository.bagger.ui.handlers;

import java.awt.event.ActionEvent;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.NewBagInPlaceFrame;
import gov.loc.repository.bagger.ui.Progress;
import gov.loc.repository.bagit.BagFactory;

public class CreateBagInPlaceHandler extends AbstractAction implements Progress {
  private static final long serialVersionUID = 1L;
  protected static final Logger log = LoggerFactory.getLogger(StartNewBagHandler.class);
  private BagView bagView;

  public CreateBagInPlaceHandler(BagView bagView) {
    super();
    this.bagView = bagView;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    createBagInPlace();
  }

  @Override
  public void execute() {
    bagView.statusBarEnd();
  }

  public void createBagInPlace() {
    NewBagInPlaceFrame newBagInPlaceFrame = new NewBagInPlaceFrame(bagView, bagView.getPropertyMessage("bag.frame.newbaginplace"));
    newBagInPlaceFrame.setBag(bagView.getBag());
    newBagInPlaceFrame.setVisible(true);
  }

  public void createPreBag(File dataFile, final String profileName) {
    if (dataFile != null && profileName != null){
      log.info("Creating a new bag in place with data: {}, version: {}, profile: {}", dataFile.getName(), BagFactory.LATEST.versionString, profileName);
      bagView.clearBagHandler.clearExistingBag();
      try {
        bagView.getBag().createPreBag(dataFile);
      }
      catch (Exception e) {
        log.error("Error - bagging in place", e);
        bagView.showWarningErrorDialog("Error - bagging in place", e.getMessage());
        return;
      }
      DefaultBag bag = bagView.getBag();

      String bagFileName = dataFile.getName();
      bag.setName(bagFileName);
      bagView.infoInputPane.setBagName(bagFileName);

      setProfile(profileName);

      bagView.saveBagHandler.save(dataFile);
    }
    else{
      log.warn("datafile is null? {} profileName is null? {}", 
          dataFile == null, profileName == null);
    }
  }

  /*
   * Prepares the call to Create Bag in Place and
   * adding .keep files in Empty Pay load Folder(s)
   */
  public void createPreBagAddKeepFilesToEmptyFolders(File dataFile, final String profileName) {
    if (dataFile != null && profileName != null){
      log.info("Creating a new bag in place with data: {}, version: {}, profile: {}", dataFile.getName(), BagFactory.LATEST.versionString, profileName);
      bagView.clearBagHandler.clearExistingBag();
      try {
        bagView.getBag().createPreBagAddKeepFilesToEmptyFolders(dataFile);
      }
      catch (Exception e) {
        log.error("Error creating pre bag", e);
        bagView.showWarningErrorDialog("Error - bagging in place", "No file or directory selection was made!\n");
        return;
      }
      DefaultBag bag = bagView.getBag();

      String bagFileName = dataFile.getName();
      bag.setName(bagFileName);
      bagView.infoInputPane.setBagName(bagFileName);

      setProfile(profileName);

      bagView.saveBagHandler.save(dataFile);
    }
    else{
      log.warn("datafile is null? {} profileName is null? {}", dataFile == null, profileName == null);
    }
  }

  private void setProfile(String selected) {
    Profile profile = bagView.getProfileStore().getProfile(selected);
    log.info("bagProject: {}", profile.getName());

    DefaultBag bag = bagView.getBag();
    bag.setProfile(profile, true);

    Map<String, String> map = new HashMap<>();
    map.put(DefaultBagInfo.FIELD_LC_PROJECT, profile.getName());
    bagView.getBag().updateBagInfo(map);
  }

}
