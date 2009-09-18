
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.BagView;
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
	DefaultBag bag;

	public RemoveTagFileHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		bag = bagView.getBag();
		removeTagFile();
	}

    public void removeTagFile() {
    	String message = "";
    	bag = bagView.getBag();
    	Bag b = bag.getBag();

    	TreePath[] paths = bagView.bagTagFileTree.getSelectionPaths();
    	if (paths != null) {
    		DefaultTreeModel model = (DefaultTreeModel)bagView.bagTagFileTree.getModel();
        	for (int i=0; i < paths.length; i++) {
        		TreePath path = paths[i];
        		Object node = path.getLastPathComponent();
    			try {
            		if (node != null) {
                		if (node instanceof MutableTreeNode) {
                    		b.removeBagFile(node.toString());
            				model.removeNodeFromParent((MutableTreeNode)node);
            			} else {
                    		b.removeBagFile((String)node);
            				DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(node);
            				model.removeNodeFromParent((MutableTreeNode)aNode);
            			}
            		}
    			} catch (Exception e) {
    				message += "Error trying to remove file: " + node + "\n";
    				bagView.showWarningErrorDialog("Error - file not removed", "Error trying to remove file: " + node + "\n" + e.getMessage());
    			}
        	}
    		bag.isCompleteChecked(false);
            bag.isValidChecked(false);
    		bag.setBag(b);
    		bagView.setBag(bag);
            bagView.compositePane.updateCompositePaneTabs(bag, "Tag file removed.");
        	bagView.bagTagFileTree.removeSelectionPaths(paths);
        	bagView.bagTagFileTreePanel.refresh(bagView.bagTagFileTree);
    	}
	}
}
