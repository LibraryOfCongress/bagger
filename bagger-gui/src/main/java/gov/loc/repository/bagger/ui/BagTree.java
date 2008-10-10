package gov.loc.repository.bagger.ui;

import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;

import java.awt.Dimension;
import java.io.File;
import java.util.Collections;
import java.util.Vector;

public class BagTree extends JTree {
	private static final long serialVersionUID = -5361474872106399068L;
	private File bagDir;
	private DefaultTreeModel bagTreeModel;
	
	public BagTree() {
		super();
		initialize();
	}
	
	public BagTree(File file) {
    	DefaultMutableTreeNode rootDir = new DefaultMutableTreeNode();
		rootDir = addNodes(null, file);
		System.out.println("BagTree: files.size: " + rootDir.getChildCount());
		setModel(new DefaultTreeModel(rootDir));
		initialize();
	}

	public BagTree(TreeNode root) {
		super(root);
		initialize();
	}
	
    private void initialize() {
		setLargeModel(true);
        setPreferredSize(getTreeSize());
/*
	    CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
	    setCellRenderer(renderer);
		//setCellRenderer(new CheckRenderer());
	    setCellEditor(new CheckBoxNodeEditor(this));
	    setEditable(true);
	    getSelectionModel().setSelectionMode(
	    		TreeSelectionModel.SINGLE_TREE_SELECTION
	    );
	    putClientProperty("JTree.lineStyle", "Angled");
	    addMouseListener(new NodeSelectionListener(this));
 */
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
