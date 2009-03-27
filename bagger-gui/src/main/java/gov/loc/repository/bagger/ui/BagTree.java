package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.BaggerFileEntity;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.util.RecursiveFileListIterator;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.*;

import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.tree.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BagTree extends CheckboxTree {
	private static final long serialVersionUID = -5361474872106399068L;
	private static final Log log = LogFactory.getLog(BagTree.class);
	private static final int BAGTREE_WIDTH = 400; // 500 - 60 for Add button width
	private static final int BAGTREE_HEIGHT = 160;
	private static final int BAGTREE_ROW_MODIFIER = 22;

	private File bagDir;
	private DefaultTreeModel bagTreeModel;
	private TreePath rootPath;
	private DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(AbstractBagConstants.DATA_DIRECTORY);
	private ArrayList<DefaultMutableTreeNode> srcNodes = new ArrayList<DefaultMutableTreeNode>();
	private ArrayList<BaggerFileEntity> rootTree = new ArrayList<BaggerFileEntity>();
	
	public BagTree() {
		super();
		initialize();
	}
	
	private void initialize() {
		setModel(new DefaultTreeModel(parentNode));
        rootPath = new TreePath(parentNode.getPath());

		setCheckingPath(rootPath);
        setAnchorSelectionPath(rootPath);
        makeVisible(rootPath);
        getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE);
		setLargeModel(true);
        setPreferredSize(getTreeSize());
        initListeners();
		requestFocus();
		setScrollsOnExpand(true);		
	}
	
	public void populateNodes(DefaultBag bag, File rootSrc) {
		if (bag.getRootPayload() == null && rootSrc.listFiles() != null) {
			File[] listFiles = rootSrc.listFiles();
			if (listFiles != null) {
				for (int i=0; i<listFiles.length; i++) {
					File f = listFiles[i];
					try {
				        this.addTree(rootSrc, f, bag.getRootDir());
					} catch(Exception e) {
						try {
					        this.addNode(f.getAbsolutePath());
						} catch (Exception ex) {
							log.error("BagView.openExistingBag: " + e.getMessage());						
						}
					}
				}
			}
		} else {
			List<String> payload = null;
			if (bag.getRootPayload() == null)
				payload = bag.getPayloadPaths();
			else
				payload = bag.getRootPayloadPaths();
            for (Iterator<String> it=payload.iterator(); it.hasNext(); ) {
            	String filePath = it.next();
				try {
			        this.addNode(filePath);
				} catch(Exception e) {
					log.error("BagView.openExistingBag: " + e.getMessage());
					try {
				        this.addNode(filePath);
					} catch (Exception ex) {
						log.error("BagView.openExistingBag: " + ex.getMessage());						
					}
				}
            }
		}		
	}

	public void addNode(String filePath) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(filePath);
		log.info("buildNodes getNode: " + node);
		srcNodes.add(node);
		parentNode.add(node);
		initialize();
	}
	
	public void addNodes(File file) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode = addNodes(null, null, file);
		//log.info("buildNodes rootNode parent: " + rootNode.getParent());
		//log.info("buildNodes getRoot: " + rootNode.getRoot());
		srcNodes.add(rootNode);
		parentNode.add(rootNode);
		initialize();
	}

	public void addParentNode(File file) {
		DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
		rootNode = addNodes(null, null, file);
		//log.info("buildNodes rootNode parent: " + rootNode.getParent());
		//log.info("buildNodes getRoot: " + rootNode.getRoot());
		srcNodes.add(rootNode);
		parentNode = rootNode;
		initialize();
	}

	public void addTree(File parent, File file, File bagRoot) {
        //log.debug("BagTree.addTree: " + file.getAbsolutePath());
        RecursiveFileListIterator fit = new RecursiveFileListIterator(file);
        if (fit != null) {
        	for (Iterator<File> it=fit; it.hasNext(); ) {
                File f = it.next();
                //log.debug("BagTree.addRootTree: " + f.getAbsolutePath());
                BaggerFileEntity bfe = new BaggerFileEntity(parent, f, bagRoot);
                rootTree.add(bfe);
            }
        }
	}

	public void setParentNode(DefaultMutableTreeNode parent) {
		this.parentNode = parent;
	}
	
	public DefaultMutableTreeNode getParentNode() {
		return this.parentNode;
	}
	
	public void setRootTree(ArrayList<BaggerFileEntity> rootTree) {
		this.rootTree = rootTree;
	}
	
	public ArrayList<BaggerFileEntity> getRootTree() {
		return this.rootTree;
	}
	
	private void initListeners() {
        addTreeCheckingListener(new TreeCheckingListener() {
            public void valueChanged(TreeCheckingEvent e) {
            	TreePath epath = new TreePath(e.getLeadingPath().getLastPathComponent());
                //log.info("BagTree Checked paths changed: user clicked on " + (e.getLeadingPath().getLastPathComponent()));
                scrollPathToVisible(epath);
                makeVisible(epath);
            }
        });	
        addTreeExpansionListener(new TreeExpansionListener() {
        	public void treeExpanded(TreeExpansionEvent e) {
                int rows = BAGTREE_ROW_MODIFIER * getRowCount();
                //log.info("BagTree rows: " + rows);
                setPreferredSize(new Dimension(BAGTREE_WIDTH, rows));
                invalidate();
        	}
        	public void treeCollapsed(TreeExpansionEvent e) {
                int rows = BAGTREE_ROW_MODIFIER * getRowCount();
                //log.info("BagTree rows: " + rows);
                setPreferredSize(new Dimension(BAGTREE_WIDTH, rows));
                invalidate();
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
	private DefaultMutableTreeNode addNodes(DefaultMutableTreeNode curTop, DefaultMutableTreeNode displayTop, File dir) {
		String curPath = dir.getPath();
		String displayPath = dir.getName();
		DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
		DefaultMutableTreeNode displayDir = new DefaultMutableTreeNode(displayPath);
		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
			displayTop.add(displayDir);
		}
		Vector<String> ol = new Vector<String>();
		//log.info("addNodes: " + dir.list());
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
    	//log.info("createBagManagerTree: files.size: " + files.size());
		for (int fnum = 0; fnum < files.size(); fnum++) {
			String elem = files.elementAt(fnum);
			DefaultMutableTreeNode elemNode = new DefaultMutableTreeNode(elem);
			//log.info("addNodes curDir: " + elem);
			curDir.add(elemNode);
			displayDir.add(elemNode);
		}

		//return curDir;
		return displayDir;
	}
}
