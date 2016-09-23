package gov.loc.repository.bagger.ui.handlers;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.bag.BaggerFileEntity;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;

public class RemoveDataHandler extends AbstractAction {
  protected static final Logger log = LoggerFactory.getLogger(RemoveDataHandler.class);
  private static final long serialVersionUID = 1L;
  BagView bagView;

  public RemoveDataHandler(BagView bagView) {
    super();
    this.bagView = bagView;
  }

  @Override
  public void actionPerformed(ActionEvent e) {
    removeData();
  }

  public void removeData() {
    String message = "";
    DefaultBag bag = bagView.getBag();

    TreePath[] paths = bagView.bagPayloadTree.getSelectionPaths();

    if (paths != null) {
      DefaultTreeModel model = (DefaultTreeModel) bagView.bagPayloadTree.getModel();
      for (int i = 0; i < paths.length; i++) {
        TreePath path = paths[i];
        Object node = path.getLastPathComponent();
        log.debug("removeData: {}", path.toString());
        log.debug("removeData pathCount: {}", path.getPathCount());
        File filePath = null;
        String fileName = null;
        if (path.getPathCount() > 0) {
          filePath = new File("" + path.getPathComponent(0));
          for (int j = 1; j < path.getPathCount(); j++) {
            filePath = new File(filePath, "" + path.getPathComponent(j));
            log.debug("filepath: {}", filePath);
          }
        }
        if (filePath != null){
          fileName = BaggerFileEntity.normalize(filePath.getPath());
        }
        log.debug("removeData filePath: {}", fileName);
        if (fileName != null && !fileName.isEmpty()) {
          try {
            bag.removeBagFile(fileName);
            ApplicationContextUtil.addConsoleMessage("Payload data removed: " + fileName);
            if (node instanceof MutableTreeNode) {
              model.removeNodeFromParent((MutableTreeNode) node);
            }
            else {
              DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
              model.removeNodeFromParent(aNode);
            }
          }
          catch (Exception e) {
            log.error("Failed to remove data, trying again", e);
            try {
              bag.removePayloadDirectory(fileName);
              if (node instanceof MutableTreeNode) {
                model.removeNodeFromParent((MutableTreeNode) node);
              }
              else {
                DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
                model.removeNodeFromParent(aNode);
              }
            }
            catch (Exception ex) {
              message = "Error trying to remove: " + fileName + "\n";
              bagView.showWarningErrorDialog("Error - file not removed", message + ex.getMessage());
              log.error("Failed to remove file {}", fileName, e);
            }
          }
        }
      }

      bagView.bagPayloadTree.removeSelectionPaths(paths);
      bagView.bagPayloadTreePanel.refresh(bagView.bagPayloadTree);
    }
  }

}
