package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.Manifest;
import gov.loc.repository.bagger.util.RecursiveFileListIterator;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.*;

import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.progress.BusyIndicator;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;

public class BagTree extends CheckboxTree {
	private static final long serialVersionUID = -5361474872106399068L;
	private static final Log log = LogFactory.getLog(Manifest.class);
	private static final int BAGTREE_WIDTH = 420; // 500 - 60 for Add button width
	private static final int BAGTREE_HEIGHT = 180;
	private static final int BAGTREE_ROW_MODIFIER = 22;

	private File bagDir;
	private DefaultTreeModel bagTreeModel;
	private TreePath rootPath;
	private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
	private DefaultMutableTreeNode displayNode = null;
	private ArrayList<File> rootTree;
	
	public BagTree() {
		super();
        init(new File("data"));
	}

	public BagTree(File file) {
		super();
		init(file);
	}
	
	public void init(File file) {
		rootNode = addNodes(null, null, file);
		setModel(new DefaultTreeModel(rootNode));
        rootPath = new TreePath(rootNode.getPath());

		setCheckingPath(rootPath);
        setAnchorSelectionPath(rootPath);
        makeVisible(rootPath);
        getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE);
		setLargeModel(true);
        setPreferredSize(getTreeSize());
        initListeners();
	}
	
	public void update(File file) {
		// TODO: Will need to make rootTree and bagTree additive so won't create anew each time.
        rootTree = new ArrayList<File>();
    	RecursiveFileListIterator fit = new RecursiveFileListIterator(file);
    	for (Iterator<File> it=fit; it.hasNext(); ) {
            File f = it.next();
            rootTree.add(f);
        }
		init(file);
		requestFocus();
		setScrollsOnExpand(true);
	}

	
	public void setRootTree(ArrayList<File> rootTree) {
		this.rootTree = rootTree;
	}
	
	public ArrayList<File> getRootTree() {
		return this.rootTree;
	}
	
	private void initListeners() {
        addTreeCheckingListener(new TreeCheckingListener() {
            public void valueChanged(TreeCheckingEvent e) {
            	TreePath epath = new TreePath(e.getLeadingPath().getLastPathComponent());
                log.info("BagTree Checked paths changed: user clicked on " + (e.getLeadingPath().getLastPathComponent()));
                scrollPathToVisible(epath);
                makeVisible(epath);
            }
        });	
        addTreeExpansionListener(new TreeExpansionListener() {
        	public void treeExpanded(TreeExpansionEvent e) {
            	TreePath epath = new TreePath(e.getPath().getLastPathComponent());
                int rows = BAGTREE_ROW_MODIFIER * getRowCount();
                //System.out.println("BagTree rows: " + rows);
                setPreferredSize(new Dimension(BAGTREE_WIDTH, rows));
                invalidate();
                repaint();
        	}
        	public void treeCollapsed(TreeExpansionEvent e) {
            	TreePath epath = new TreePath(e.getPath().getLastPathComponent());
                int rows = BAGTREE_ROW_MODIFIER * getRowCount();
                //System.out.println("BagTree rows: " + rows);
                setPreferredSize(new Dimension(BAGTREE_WIDTH, rows));
                invalidate();
                repaint();
        	}
        });
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

    public Dimension getTreeSize() {
    	return new Dimension(BAGTREE_WIDTH, BAGTREE_HEIGHT);
    }

	/** Add nodes from under "dir" into curTop. Highly recursive. */
	DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, DefaultMutableTreeNode displayTop, File dir) {
		String curPath = dir.getPath();
		String displayPath = dir.getName();
		DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
		DefaultMutableTreeNode displayDir = new DefaultMutableTreeNode(displayPath);
		if (curTop != null) { // should only be null at root
//			System.out.println("addNodes curTop: " + curPath);
			curTop.add(curDir);
			displayTop.add(displayDir);
		}
		Vector<String> ol = new Vector<String>();
		//System.out.println("addNodes: " + dir.list());
		String[] tmp = dir.list();
		if (tmp != null && tmp.length > 0) {
			for (int i = 0; i < tmp.length; i++)
				ol.addElement(tmp[i]);
		}

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
				addNodes(curDir, displayDir, f);
			else
				files.addElement(thisObject);
		}
		// Pass two: for files.
    	//display("createBagManagerTree: files.size: " + files.size());
		for (int fnum = 0; fnum < files.size(); fnum++) {
			String elem = files.elementAt(fnum);
			DefaultMutableTreeNode elemNode = new DefaultMutableTreeNode(elem);
			//System.out.println("addNodes curDir: " + elem);
			curDir.add(elemNode);
			displayDir.add(elemNode);
		}

		//return curDir;
		return displayDir;
	}
}
