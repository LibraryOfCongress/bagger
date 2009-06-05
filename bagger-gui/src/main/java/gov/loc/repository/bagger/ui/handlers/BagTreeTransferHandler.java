package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.ui.BagView;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JTree;
import javax.swing.TransferHandler;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

public class BagTreeTransferHandler extends TransferHandler {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(BagTreeTransferHandler.class);
	private static DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String", null);
    private static boolean debugImport = false;
	DataFlavor nodesFlavor;
	DataFlavor[] flavors = new DataFlavor[1];
    private BagView bagView;
    private boolean isPayload;
	private DefaultMutableTreeNode[] nodesToRemove;

    public BagTreeTransferHandler(BagView bagView, boolean isPayload) {
    	super();
		this.bagView = bagView;
    	this.isPayload = isPayload;
		try {
			String mimeType = DataFlavor.javaJVMLocalObjectMimeType + ";class=\"" +	javax.swing.tree.DefaultMutableTreeNode[].class.getName() +	"\"";
			nodesFlavor = new DataFlavor(mimeType);
			flavors[0] = nodesFlavor;
		} catch(ClassNotFoundException e) {
			log.error("ClassNotFound: " + e.getMessage());
		}
    }

    private void display(String s) {
    	String msg = "BagTreeTransferHandler." + s;
    	//System.out.println(msg);
    	log.info(msg);
    }

    public int getSourceActions(JComponent c) {
    	return TransferHandler.COPY;
    }

    public boolean canImport(JComponent comp, DataFlavor transferFlavors[]) {
    	for (int i = 0; i < transferFlavors.length; i++) {
    		Class representationclass = transferFlavors[i].getRepresentationClass();
    		// URL from Explorer or Firefox, KDE
    		if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		// Drop from Windows Explorer
    		if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		// Drop from GNOME
    		if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		if (uriListFlavor.equals(transferFlavors[i])) {
    			if (debugImport) {
    				display("canImport accepted " + transferFlavors[i]);
    			}
    			return true;
    		}
    		if (debugImport) {
    			log.error("canImport " + i + " unknown import " + transferFlavors[i]);
    		}
    	}
    	if (debugImport) {
    		log.error("canImport rejected");
    	}
    	return false;
    }

    public boolean importData(JComponent comp, Transferable t) {
    	DataFlavor[] transferFlavors = t.getTransferDataFlavors();
    	for (int i = 0; i < transferFlavors.length; i++) {
    		// Drop from Windows Explorer
    		if (DataFlavor.javaFileListFlavor.equals(transferFlavors[i])) {
    			display("importData transferFlavors: " + transferFlavors[i]);
    			try {
    				List<File> fileList = (List<File>) t.getTransferData(DataFlavor.javaFileListFlavor);
    				if (fileList != null) {
    					display("importData size: " + fileList.size());
    					if (isPayload) {
        					bagView.dropBagPayloadData(fileList);
    					} else {
        					bagView.dropBagTagFile(fileList);
    					}
    				} else if (fileList.get(0) instanceof File) {
    					log.error("importData Unknown object in list: " + fileList.get(0));
    				} else {
    					log.error("importData fileList NULL");
    				}
    			} catch (UnsupportedFlavorException e) {
    				log.error(e.toString());
    			} catch (IOException e) {
    				log.error(e.toString());
    			}
    			return true;
    		}

    		if (DataFlavor.stringFlavor.equals(transferFlavors[i])) {
    			Object data;
    			try {
    				data = t.getTransferData(DataFlavor.stringFlavor);
    			} catch (UnsupportedFlavorException e) {
    				continue;
    			} catch (IOException e) {
    				continue;
    			}
    			if (data instanceof String) {
    				display("importData importing transferFlavors: " + transferFlavors[i]);
    				String path = getPathString((String) data);
    				display("importData path: " + path);
    				//mainInstance.downloadFile(path);
    				return true;
    			}
    		}

    		if (uriListFlavor.equals(transferFlavors[i])) {
    			Object data;
    			try {
    				data = t.getTransferData(uriListFlavor);
    			} catch (UnsupportedFlavorException e) {
    				continue;
    			} catch (IOException e) {
    				continue;
    			}
    			if (data instanceof String) {
    				display("importData importing transferFlavors: " + transferFlavors[i]);
    				String path = getPathString((String) data);
    				display("importData path: " + path);
    				//mainInstance.downloadFile(path);
    				return true;
    			}
    		}
    		if (debugImport) {
    			log.error("importData [" + i + "] unknown import: " + transferFlavors[i]);
    		}
    	}
    	// This is the second best option since it works incorrectly on Max OS X
    	// making url like this [file://localhost/users/work/app.jad]
    	for (int i = 0; i < transferFlavors.length; i++) {
    		Class representationclass = transferFlavors[i].getRepresentationClass();
    		// URL from Explorer or Firefox, KDE
    		if ((representationclass != null) && URL.class.isAssignableFrom(representationclass)) {
    			display("importData importing transferFlavors: " + transferFlavors[i]);
    			try {
    				URL jadUrl = (URL) t.getTransferData(transferFlavors[i]);
    				String urlString = jadUrl.toExternalForm();
    				display("importData urlString: " + urlString);
    				//mainInstance.downloadFile(urlString);
    			} catch (UnsupportedFlavorException e) {
    				log.error(e.toString());
    			} catch (IOException e) {
    				log.error(e.toString());
    			}
    			return true;
    		}
    	}
    	return false;
    }

	private boolean haveCompleteNode(JTree tree) {
		int[] selRows = tree.getSelectionRows();
		if (selRows == null) return false;
		TreePath path = tree.getPathForRow(selRows[0]);
		DefaultMutableTreeNode first = (DefaultMutableTreeNode)path.getLastPathComponent();
		int childCount = first.getChildCount();
		// first has children and no children are selected.
		if(childCount > 0 && selRows.length == 1)
			return false;
		// first may have children.
		for(int i = 1; i < selRows.length; i++) {
			path = tree.getPathForRow(selRows[i]);
			DefaultMutableTreeNode next = (DefaultMutableTreeNode)path.getLastPathComponent();
			if(first.isNodeChild(next)) {
				// Found a child of first.
				if(childCount > selRows.length-1) {
					// Not all children of first are selected.
					return false;
				}
			}
		}
		return true;
	}

	protected Transferable createTransferable(JComponent c) {
		JTree tree = (JTree)c;
		TreePath[] paths = tree.getSelectionPaths();
		if(paths != null) {
			// Make up a node array of copies for transfer and
			// another for/of the nodes that will be removed in
			// exportDone after a successful drop.
			List<DefaultMutableTreeNode> copies = new ArrayList<DefaultMutableTreeNode>();
			List<DefaultMutableTreeNode> toRemove =	new ArrayList<DefaultMutableTreeNode>();
			DefaultMutableTreeNode node = (DefaultMutableTreeNode)paths[0].getLastPathComponent();
			DefaultMutableTreeNode copy = copy(node);
			copies.add(copy);
			toRemove.add(node);
			for(int i = 1; i < paths.length; i++) {
				DefaultMutableTreeNode next = (DefaultMutableTreeNode)paths[i].getLastPathComponent();
				// Do not allow higher level nodes to be added to list.
				if(next.getLevel() < node.getLevel()) {
					break;
				} else if(next.getLevel() > node.getLevel()) {  // child node
					copy.add(copy(next));
					// node already contains child
				} else {                                        // sibling
					copies.add(copy(next));
					toRemove.add(next);
				}
			}
			DefaultMutableTreeNode[] nodes = copies.toArray(new DefaultMutableTreeNode[copies.size()]);
			nodesToRemove =	toRemove.toArray(new DefaultMutableTreeNode[toRemove.size()]);
			return new NodesTransferable(nodes);
		}
		return null;
	}

	/** Defensive copy used in createTransferable. */
	private DefaultMutableTreeNode copy(TreeNode node) {
		return new DefaultMutableTreeNode(node);
	}

	protected void exportDone(JComponent source, Transferable data, int action) {
//		if((action & MOVE) == MOVE) {
			JTree tree = (JTree)source;
			DefaultTreeModel model = (DefaultTreeModel)tree.getModel();
			// Remove nodes saved in nodesToRemove in createTransferable.
			for(int i = 0; i < nodesToRemove.length; i++) {
				display("exportDonevnodesToRemove: " + nodesToRemove[i] );
				model.removeNodeFromParent(nodesToRemove[i]);
			}
			if (this.isPayload) {
				//bagView.removeDataHandler.removeData();
			} else {
				//bagView.removeTagFileHandler.removeTagFile();
			}
//		}
	}

    private String getPathString(String path) {
    	if (path == null) {
    		return null;
    	}
    	StringTokenizer st = new StringTokenizer(path.trim(), "\n\r");
    	if (st.hasMoreTokens()) {
    		return st.nextToken();
    	}
    	return path;
    }

    public static String getCanonicalFileURL(File file) {
    	String path = file.getAbsoluteFile().getPath();
    	if (File.separatorChar != '/') {
    		path = path.replace(File.separatorChar, '/');
    	}
    	// Not network path
    	if (!path.startsWith("//")) {
    		if (path.startsWith("/")) {
    			path = "//" + path;
    		} else {
    			path = "///" + path;
    		}
    	}
    	return "file:" + path;
    }

	public class NodesTransferable implements Transferable {
		DefaultMutableTreeNode[] nodes;

		public NodesTransferable(DefaultMutableTreeNode[] nodes) {
			this.nodes = nodes;
		}

		public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
			if(!isDataFlavorSupported(flavor))
				throw new UnsupportedFlavorException(flavor);
			return nodes;
		}

		public DataFlavor[] getTransferDataFlavors() {
			return flavors;
		}

		public boolean isDataFlavorSupported(DataFlavor flavor) {
			return nodesFlavor.equals(flavor);
		}
	}
}
