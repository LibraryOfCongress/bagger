
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.model.Status;
import gov.loc.repository.bagger.ui.BagView;
import gov.loc.repository.bagger.ui.util.ApplicationContextUtil;
import gov.loc.repository.bagit.Bag;

import java.awt.event.ActionEvent;

import javax.swing.AbstractAction;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;

public class RemoveTagFileHandler extends AbstractAction {
   	private static final long serialVersionUID = 1L;
	BagView bagView;

	public RemoveTagFileHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		removeTagFile();
	}

    public void removeTagFile() {
    	String message = "";
    	DefaultBag bag = bagView.getBag();

    	TreePath[] paths = bagView.bagTagFileTree.getSelectionPaths();
    	if (paths != null) {
    		DefaultTreeModel model = (DefaultTreeModel)bagView.bagTagFileTree.getModel();
        	for (int i=0; i < paths.length; i++) {
        		TreePath path = paths[i];
        		Object node = path.getLastPathComponent();
    			try {
            		if (node != null) {
                		if (node instanceof MutableTreeNode) {
                    		bag.removeBagFile(node.toString());
                    		ApplicationContextUtil.addConsoleMessage("Tag file removed: " + node.toString());
            				model.removeNodeFromParent((MutableTreeNode)node);
            			} else {
                    		bag.removeBagFile((String)node);
                    		ApplicationContextUtil.addConsoleMessage("Tag file removed: " + node.toString());
            				DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
            				model.removeNodeFromParent((MutableTreeNode)aNode);
            			}
            		}
    			} catch (Exception e) {
    				message += "Error trying to remove file: " + node + "\n";
    				bagView.showWarningErrorDialog("Error - file not removed", "Error trying to remove file: " + node + "\n" + e.getMessage());
    			}
        	}
        	bagView.bagTagFileTree.removeSelectionPaths(paths);
        	bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    	}
	}
}
