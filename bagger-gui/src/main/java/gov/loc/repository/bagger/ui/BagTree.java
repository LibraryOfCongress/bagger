package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.Manifest;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Dimension;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

public class BagTree extends it.cnr.imaa.essi.lablib.gui.checkboxtree.CheckboxTree {
	private static final long serialVersionUID = -5361474872106399068L;
	private static final Log log = LogFactory.getLog(Manifest.class);

	private File bagDir;
	private DefaultTreeModel bagTreeModel;
	
	public BagTree() {
		super();
        setPreferredSize(getTreeSize());
	}

	public BagTree(File file) {
		super();
    	DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode = addNodes(null, file);
		setModel(new DefaultTreeModel(rootNode));
		setLargeModel(true);
        setPreferredSize(getTreeSize());
        addTreeCheckingListener(new TreeCheckingListener() {
            public void valueChanged(TreeCheckingEvent e) {
                log.info("BagTree Checked paths changed: user clicked on " + (e.getLeadingPath().getLastPathComponent()));
            }
        });	
        getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE);
	}

	public File getBagDir() {
		return this.bagDir;
	}
	
	public void setBagDir(File file) {
		this.bagDir = file;
	}
	
	public DefaultTreeModel getBagTreeModel() {
		return this.bagTreeModel;
	}
	
	public void setBagTreeModel(DefaultTreeModel model) {
		this.bagTreeModel = model;
	}

    private Dimension getTreeSize() {
    	return new Dimension(480, 200);
    }

	/** Add nodes from under "dir" into curTop. Highly recursive. */
	DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, File dir) {
    	//display("createBagManagerTree: addNodes: " + dir.toString());
		String curPath = dir.getPath();
		DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
		}
		Vector<String> ol = new Vector<String>();
		String[] tmp = dir.list();
		for (int i = 0; i < tmp.length; i++)
			ol.addElement(tmp[i]);

		Collections.sort(ol, String.CASE_INSENSITIVE_ORDER);
		File f;
		Vector<String> files = new Vector<String>();
		// Make two passes, one for Dirs and one for Files. This is #1.
		for (int i = 0; i < ol.size(); i++) {
			String thisObject = (String) ol.elementAt(i);
			String newPath;
			if (curPath.equals("."))
				newPath = thisObject;
			else
				newPath = curPath + File.separator + thisObject;
			if ((f = new File(newPath)).isDirectory())
				addNodes(curDir, f);
			else
				files.addElement(thisObject);
		}
		// Pass two: for files.
    	//display("createBagManagerTree: files.size: " + files.size());
		for (int fnum = 0; fnum < files.size(); fnum++)
			curDir.add(new DefaultMutableTreeNode(files.elementAt(fnum)));

		return curDir;
	}
}
