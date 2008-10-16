package gov.loc.repository.bagger.ui;

/*
Definitive Guide to Swing for Java 2, Second Edition
By John Zukowski
ISBN: 1-893115-78-X
Publisher: APress
*/

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.EventObject;
import java.util.Vector;
import java.io.File;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Enumeration;

import javax.swing.AbstractCellEditor;
import javax.swing.Icon;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellEditor;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreePath;
import javax.swing.tree.TreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeSelectionModel;


public class CheckboxTreeNode extends DefaultMutableTreeNode {

	  public final static int SINGLE_SELECTION = 0;

	  public final static int DIG_IN_SELECTION = 4;

	  protected int selectionMode;

	  protected boolean isSelected;

	  public CheckboxTreeNode() {
	    this(null);
	  }

	  public CheckboxTreeNode(Object userObject) {
	    this(userObject, true, false);
	  }

	  public CheckboxTreeNode(Object userObject, boolean allowsChildren, boolean isSelected) {
	    super(userObject, allowsChildren);
	    this.isSelected = isSelected;
	    setSelectionMode(DIG_IN_SELECTION);
	  }

	  public void setSelectionMode(int mode) {
	    selectionMode = mode;
	  }

	  public int getSelectionMode() {
	    return selectionMode;
	  }

	  public void setSelected(boolean isSelected) {
	    this.isSelected = isSelected;

	    if ((selectionMode == DIG_IN_SELECTION) && (children != null)) {
	      Enumeration e = children.elements();
//	      while (e.hasMoreElements()) {
//	        CheckNode node = (CheckNode) e.nextElement();
//	        node.setSelected(isSelected);
//	      }
	    }
	  }

	  public boolean isSelected() {
	    return isSelected;
	  }

	  public CheckboxTreeNode addNodes(File file) {
		  CheckboxTreeNode treeNode = new CheckboxTreeNode();
		  return treeNode;
	  }

	  // If you want to change "isSelected" by CellEditor,
	  /*
	   public void setUserObject(Object obj) { if (obj instanceof Boolean) {
	   * setSelected(((Boolean)obj).booleanValue()); } else {
	   * super.setUserObject(obj); } }
	   */
}
