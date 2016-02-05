package gov.loc.repository.bagger.ui.handlers;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.TagFilesFrame;

public class ShowTagFilesHandler extends AbstractAction {
  private static final long serialVersionUID = 1L;
  private TagFilesFrame tagFilesFrame;
  BagView bagView;
  DefaultBag bag;

  public ShowTagFilesHandler(BagView bagView) {
    super();
    this.bagView = bagView;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    showTagFiles();
  }

  public void showTagFiles() {
    bag = bagView.getBag();
    bagView.tagManifestPane.updateCompositePaneTabs(bag);
    tagFilesFrame = new TagFilesFrame(bagView.getPropertyMessage("bagView.tagFrame.title"));
    tagFilesFrame.addComponents(bagView.tagManifestPane);
    tagFilesFrame.addComponents(bagView.tagManifestPane);
    tagFilesFrame.setVisible(true);
    tagFilesFrame.setAlwaysOnTop(true);
  }
}
