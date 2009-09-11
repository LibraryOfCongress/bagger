package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.BaggerFileEntity;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.ui.handlers.BagTreeTransferHandler;
import gov.loc.repository.bagger.util.RecursiveFileListIterator;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import it.cnr.imaa.essi.lablib.gui.checkboxtree.*;

import javax.swing.DropMode;
import javax.swing.JTextField;
import javax.swing.event.TreeExpansionListener;
import javax.swing.event.TreeExpansionEvent;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.awt.Dimension;
import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

public class BagTree extends CheckboxTree {
	private static final long serialVersionUID = -5361474872106399068L;
	private static final Log log = LogFactory.getLog(BagTree.class);
	private int BAGTREE_WIDTH = 400;
	private int BAGTREE_HEIGHT = 160;
	private int BAGTREE_ROW_MODIFIER = 22;

	private BagView bagView;
	private File bagDir;
	private DefaultTreeModel bagTreeModel;
	private TreePath rootPath;
	private String basePath;
	private DefaultMutableTreeNode parentNode = new DefaultMutableTreeNode(AbstractBagConstants.DATA_DIRECTORY);
	private ArrayList<DefaultMutableTreeNode> srcNodes = new ArrayList<DefaultMutableTreeNode>();
	
	public BagTree(BagView bagView, String path, boolean isPayload) {
		super();
		this.bagView = bagView;
		basePath = path;
		parentNode = new DefaultMutableTreeNode(basePath);
		initialize();
        JTextField nameTextField = new JTextField();
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight() + 5;
        BAGTREE_ROW_MODIFIER = fieldHeight;
        this.setDragEnabled(true);
        this.setDropMode(DropMode.ON_OR_INSERT);
        this.setTransferHandler(new BagTreeTransferHandler(bagView, isPayload));
        this.getSelectionModel().setSelectionMode(TreeSelectionModel.CONTIGUOUS_TREE_SELECTION);
	}
	
	private void initialize() {
		setModel(new DefaultTreeModel(parentNode));
        rootPath = new TreePath(parentNode.getPath());

		//setCheckingPath(rootPath);
        setAnchorSelectionPath(rootPath);
        makeVisible(rootPath);
        getCheckingModel().setCheckingMode(TreeCheckingModel.CheckingMode.PROPAGATE);
		setLargeModel(true);
        setPreferredSize(getTreeSize());
        initListeners();
		requestFocus();
		setScrollsOnExpand(true);		
	}
	
	public void populateNodes(DefaultBag bag, File rootSrc, boolean isParent) {
		log.debug("BagTree.populateNodes");
		if (bag.getBag().getPayload() != null && rootSrc.listFiles() != null) {
			addNodes(rootSrc, isParent);
		} else {
			log.debug("BagTree.populateNodes listFiles NULL:" );
			List<String> payload = null;
		    if (!bag.isHoley()) {
				log.debug("BagTree.populateNodes getPayloadPaths:" );
				payload = bag.getPayloadPaths();
		    } else {
				log.debug("BagTree.populateNodes getFetchPayload:");
				payload = bag.getPayloadPaths(); //bag.getFetchPayload();
				//basePath = bag.getFetch().getBaseURL();
		    }
		    for (Iterator<String> it=payload.iterator(); it.hasNext(); ) {
		    	String filePath = it.next();
		    	try {
		    		String normalPath;
		    		if (bag.isHoley()) {
			    		normalPath = BaggerFileEntity.removeBasePath("data", filePath);
		    		} else {
			    		normalPath = BaggerFileEntity.removeBasePath(basePath, filePath);
		    		}
		    		if (!nodeAlreadyExists(normalPath)) {
		    			this.addNode(normalPath);
		    		}
		    	} catch(Exception e) {
		    		if (!nodeAlreadyExists(filePath)) {
			    		this.addNode(filePath);
		    		}
		    		log.error("BagTree.populateNodes: " + e.getMessage());
		    	}
		    }
		    log.debug("BagTree rows: " + payload.size());
		    BAGTREE_HEIGHT = BAGTREE_ROW_MODIFIER * (payload.size() + 1);
		    setPreferredSize(getTreeSize());
		    invalidate();
		}
	}

	public boolean addNodes(File file, boolean isParent) {
		if (!nodeAlreadyExists(file.getName())) {
			DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode();
			rootNode = createNodeTree(null, null, file);
			//log.info("buildNodes rootNode parent: " + rootNode.getParent());
			//log.info("buildNodes getRoot: " + rootNode.getRoot());
			srcNodes.add(rootNode);
			if (isParent) 
				parentNode = rootNode;
			else
				parentNode.add(rootNode);
			initialize();
		} else {
			return true;
		}
		return false;
	}
	
	private boolean nodeAlreadyExists(String path) {
		boolean b = false;
		DefaultMutableTreeNode aNode = new DefaultMutableTreeNode(path);
		String node = aNode.toString();
		b = parentNode.isNodeChild(aNode);
		if (b) return b;
		for (int i=0; i < parentNode.getChildCount(); i++) {
			DefaultMutableTreeNode childNode = (DefaultMutableTreeNode) parentNode.getChildAt(i);
			String child = childNode.toString();
			if (child.equalsIgnoreCase(node)) {
				b = true;
				break;
			}
		}
		return b;
	}

	public void addNode(String filePath) {
		DefaultMutableTreeNode node = new DefaultMutableTreeNode(filePath);
		//log.info("buildNodes getNode: " + node);
		srcNodes.add(node);
		parentNode.add(node);
		initialize();
	}
	
	/** Add nodes from under "dir" into curTop. Highly recursive. */
	private DefaultMutableTreeNode createNodeTree(DefaultMutableTreeNode curTop, DefaultMutableTreeNode displayTop, File dir) {
		String curPath = dir.getPath();
		String displayPath = dir.getName();
		DefaultMutableTreeNode curDir = new DefaultMutableTreeNode(curPath);
		DefaultMutableTreeNode displayDir = new DefaultMutableTreeNode(displayPath);
		if (curTop != null) { // should only be null at root
			curTop.add(curDir);
			displayTop.add(displayDir);
		}
		Vector<String> ol = new Vector<String>();
		//display("addNodes: " + dir.list());
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
				createNodeTree(curDir, displayDir, f);
			else
				files.addElement(thisObject);
		}
		// Pass two: for files.
    	//display("createBagManagerTree: files.size: " + files.size());
		for (int fnum = 0; fnum < files.size(); fnum++) {
			String elem = files.elementAt(fnum);
			DefaultMutableTreeNode elemNode = new DefaultMutableTreeNode(elem);
			curDir.add(elemNode);
			displayDir.add(elemNode);
		}

		//return curDir;
		return displayDir;
	}

	private void display(String msg) {
		log.info("BagTree: " + msg);
	}
	
	public void setParentNode(DefaultMutableTreeNode parent) {
		this.parentNode = parent;
	}
	
	public DefaultMutableTreeNode getParentNode() {
		return this.parentNode;
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

	private void initListeners() {
        addTreeCheckingListener(new TreeCheckingListener() {
            public void valueChanged(TreeCheckingEvent e) {
            	TreePath epath = new TreePath(e.getLeadingPath().getLastPathComponent());
                //log.info("BagTree.addTreeCheckingListener valueChanged: " + e.getLeadingPath().getLastPathComponent());
            	if (!e.isChecked()) {
            		//log.info("BagTree.addTreeCheckingListener remove: " + (e.getLeadingPath().getLastPathComponent()));
            	}
                scrollPathToVisible(epath);
                makeVisible(epath);
            }
        });	
        addTreeSelectionListener(new TreeSelectionListener() {
        	public void valueChanged(TreeSelectionEvent e) {
        		DefaultMutableTreeNode node = (DefaultMutableTreeNode) getLastSelectedPathComponent();
        		if (node == null) return;
        		Object nodeInfo = node.getUserObject();
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
	
}
