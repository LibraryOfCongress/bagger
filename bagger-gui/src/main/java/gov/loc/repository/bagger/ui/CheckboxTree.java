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


public class CheckboxTree extends JTree {
	public CheckboxTree() {
		super();
		initialize();
	}

	public CheckboxTree(TreeNode root) {
		super(root);
		initialize();
	}

	public CheckboxTree(Vector root) {
		super(root);
		initialize();
	}
	
	private void initialize() {
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
	}

	public static void main(String args[]) {
		JFrame frame = new JFrame("CheckBox Tree");

		CheckBoxNode accessibilityOptions[] = {
				new CheckBoxNode("Move system caret with focus/selection changes", false),
				new CheckBoxNode("Always expand alt text for images", true) 
		};
	    CheckBoxNode browsingOptions[] = {
	        new CheckBoxNode("Notify when downloads complete", true),
	        new CheckBoxNode("Disable script debugging", true),
	        new CheckBoxNode("Use AutoComplete", true),
	        new CheckBoxNode("Browse in a new process", false) 
	    };

	    Vector accessVector = new NamedVector("Accessibility", accessibilityOptions);
	    Vector browseVector = new NamedVector("Browsing", browsingOptions);
	    Object rootNodes[] = { accessVector, browseVector };
	    Vector rootVector = new NamedVector("Root", rootNodes);
	    CheckboxTree tree = new CheckboxTree(rootVector);

	    JScrollPane scrollPane = new JScrollPane(tree);
	    frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
	    frame.setSize(300, 150);
	    frame.setVisible(true);
	}
}

class NodeSelectionListener extends MouseAdapter {
	JTree tree;
    
	NodeSelectionListener(JTree tree) {
		this.tree = tree;
    }
    
    public void mouseClicked(MouseEvent e) {
    	int x = e.getX();
    	int y = e.getY();
    	int row = tree.getRowForLocation(x, y);
    	TreePath  path = tree.getPathForRow(row);
    	//TreePath  path = tree.getSelectionPath();
    	if (path != null) {
    		CheckboxTreeNode node = (CheckboxTreeNode)path.getLastPathComponent();
    		boolean isSelected = ! (node.isSelected());
    		node.setSelected(isSelected);
    		if (node.getSelectionMode() == CheckboxTreeNode.DIG_IN_SELECTION) {
    			if ( isSelected) {
    				tree.expandPath(path);
    			} else {
    				tree.collapsePath(path);
    			}
    		}
    		((DefaultTreeModel) tree.getModel()).nodeChanged(node);
    		// I need revalidate if node is root.  but why?
    		if (row == 0) {
    			tree.revalidate();
    			tree.repaint();
    		}
    	}
    }
}

class CheckRenderer extends JPanel implements TreeCellRenderer {
	  protected JCheckBox check;

	  protected TreeLabel label;

	  public CheckRenderer() {
		  setLayout(null);
		  add(check = new JCheckBox());
		  add(label = new TreeLabel());
		  check.setBackground(UIManager.getColor("Tree.textBackground"));
		  label.setForeground(UIManager.getColor("Tree.textForeground"));
	  }

	  public Component getTreeCellRendererComponent(JTree tree, Object value,
			  boolean isSelected, boolean expanded, boolean leaf, int row,
			  boolean hasFocus) {
		  String stringValue = tree.convertValueToText(value, isSelected, expanded, leaf, row, hasFocus);
		  setEnabled(tree.isEnabled());
		  check.setSelected(((CheckboxTreeNode) value).isSelected());
		  label.setFont(tree.getFont());
		  label.setText(stringValue);
		  label.setSelected(isSelected);
		  label.setFocus(hasFocus);
		  if (leaf) {
			  label.setIcon(UIManager.getIcon("Tree.leafIcon"));
		  } else if (expanded) {
			  label.setIcon(UIManager.getIcon("Tree.openIcon"));
		  } else {
			  label.setIcon(UIManager.getIcon("Tree.closedIcon"));
		  }
		  return this;
	  }

	  public Dimension getPreferredSize() {
		  Dimension d_check = check.getPreferredSize();
		  Dimension d_label = label.getPreferredSize();
		  return new Dimension(d_check.width + d_label.width,
				  				(d_check.height < d_label.height ? d_label.height
				  						: d_check.height));
	  }

	  public void doLayout() {
		  Dimension d_check = check.getPreferredSize();
		  Dimension d_label = label.getPreferredSize();
		  int y_check = 0;
		  int y_label = 0;
		  if (d_check.height < d_label.height) {
			  y_check = (d_label.height - d_check.height) / 2;
		  } else {
			  y_label = (d_check.height - d_label.height) / 2;
		  }
		  check.setLocation(0, y_check);
		  check.setBounds(0, y_check, d_check.width, d_check.height);
		  label.setLocation(d_check.width, y_label);
		  label.setBounds(d_check.width, y_label, d_label.width, d_label.height);
	  }

	  public void setBackground(Color color) {
		  if (color instanceof ColorUIResource)
			  color = null;
		  super.setBackground(color);
	  }

	public class TreeLabel extends JLabel {
		  boolean isSelected;
		  boolean hasFocus;

		  public TreeLabel() {
		  }

		  public void setBackground(Color color) {
			  if (color instanceof ColorUIResource)
				  color = null;
			  super.setBackground(color);
		  }

		  public void paint(Graphics g) {
			  String str;
			  if ((str = getText()) != null) {
				  if (0 < str.length()) {
					  if (isSelected) {
						  g.setColor(UIManager.getColor("Tree.selectionBackground"));
					  } else {
						  g.setColor(UIManager.getColor("Tree.textBackground"));
					  }
					  Dimension d = getPreferredSize();
					  int imageOffset = 0;
					  Icon currentI = getIcon();
					  if (currentI != null) {
						  imageOffset = currentI.getIconWidth() + Math.max(0, getIconTextGap() - 1);
					  }
					  g.fillRect(imageOffset, 0, d.width - 1 - imageOffset, d.height);
					  if (hasFocus) {
						  g.setColor(UIManager.getColor("Tree.selectionBorderColor"));
						  g.drawRect(imageOffset, 0, d.width - 1 - imageOffset, d.height - 1);
					  }
				  }
			  }
			  super.paint(g);
	    }

	    public Dimension getPreferredSize() {
	    	Dimension retDimension = super.getPreferredSize();
	    	if (retDimension != null) {
	    		retDimension = new Dimension(retDimension.width + 3, retDimension.height);
	    	}
	    	return retDimension;
	    }

	    public void setSelected(boolean isSelected) {
	    	this.isSelected = isSelected;
	    }

	    public void setFocus(boolean hasFocus) {
	    	this.hasFocus = hasFocus;
	    }
	}
}

class CheckBoxNodeRenderer implements TreeCellRenderer {
	private JCheckBox leafRenderer = new JCheckBox();

	private DefaultTreeCellRenderer nonLeafRenderer = new DefaultTreeCellRenderer();

	Color selectionBorderColor, selectionForeground, selectionBackground, textForeground, textBackground;

	protected JCheckBox getLeafRenderer() {
		return leafRenderer;
	}

	public CheckBoxNodeRenderer() {
		Font fontValue;
		fontValue = UIManager.getFont("Tree.font");
		if (fontValue != null) {
			leafRenderer.setFont(fontValue);
		}
		Boolean booleanValue = (Boolean) UIManager.get("Tree.drawsFocusBorderAroundIcon");
		leafRenderer.setFocusPainted((booleanValue != null) && (booleanValue.booleanValue()));

		selectionBorderColor = UIManager.getColor("Tree.selectionBorderColor");
		selectionForeground = UIManager.getColor("Tree.selectionForeground");
		selectionBackground = UIManager.getColor("Tree.selectionBackground");
		textForeground = UIManager.getColor("Tree.textForeground");
		textBackground = UIManager.getColor("Tree.textBackground");
	}

	public Component getTreeCellRendererComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {

		Component returnValue;
		if (leaf) {
			String stringValue = tree.convertValueToText(value, selected, expanded, leaf, row, false);
			leafRenderer.setText(stringValue);
			leafRenderer.setSelected(false);
			leafRenderer.setEnabled(tree.isEnabled());

			if (selected) {
				leafRenderer.setForeground(selectionForeground);
				leafRenderer.setBackground(selectionBackground);
			} else {
				leafRenderer.setForeground(textForeground);
				leafRenderer.setBackground(textBackground);
			}

			if ((value != null) && (value instanceof DefaultMutableTreeNode)) {
				Object userObject = ((DefaultMutableTreeNode) value).getUserObject();
				if (userObject instanceof CheckBoxNode) {
					CheckBoxNode node = (CheckBoxNode) userObject;
					leafRenderer.setText(node.getText());
					leafRenderer.setSelected(node.isSelected());
				}
			}
			returnValue = leafRenderer;
		} else {
			returnValue = nonLeafRenderer.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
		}
		return returnValue;
	}
}

class CheckBoxNodeEditor extends AbstractCellEditor implements TreeCellEditor {

	CheckBoxNodeRenderer renderer = new CheckBoxNodeRenderer();
	ChangeEvent changeEvent = null;
	JTree tree;

	public CheckBoxNodeEditor(JTree tree) {
		this.tree = tree;
	}

	public Object getCellEditorValue() {
		JCheckBox checkbox = renderer.getLeafRenderer();
		CheckBoxNode checkBoxNode = new CheckBoxNode(checkbox.getText(), checkbox.isSelected());
		return checkBoxNode;
	}

	public boolean isCellEditable(EventObject event) {
		boolean returnValue = false;
		if (event instanceof MouseEvent) {
			MouseEvent mouseEvent = (MouseEvent) event;
			TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
			if (path != null) {
				Object node = path.getLastPathComponent();
				if ((node != null) && (node instanceof DefaultMutableTreeNode)) {
					DefaultMutableTreeNode treeNode = (DefaultMutableTreeNode) node;
					Object userObject = treeNode.getUserObject();
					returnValue = ((treeNode.isLeaf()) && (userObject instanceof CheckBoxNode));
				}
			}
		}
		return returnValue;
	}

	public Component getTreeCellEditorComponent(JTree tree, Object value,
			boolean selected, boolean expanded, boolean leaf, int row) {

		Component editor = renderer.getTreeCellRendererComponent(tree, value, true, expanded, leaf, row, true);

		// editor always selected / focused
		ItemListener itemListener = new ItemListener() {
			public void itemStateChanged(ItemEvent itemEvent) {
				if (stopCellEditing()) {
					fireEditingStopped();
				}
			}
		};
		if (editor instanceof JCheckBox) {
			((JCheckBox) editor).addItemListener(itemListener);
		}

		return editor;
	}
}

class CheckBoxNode {
	String text;
	boolean selected;

	public CheckBoxNode(String text, boolean selected) {
		this.text = text;
		this.selected = selected;
	}

	public boolean isSelected() {
		return selected;
	}

	public void setSelected(boolean newValue) {
		selected = newValue;
	}

	public String getText() {
		return text;
	}

	public void setText(String newValue) {
		text = newValue;
	}

	public String toString() {
		return getClass().getName() + "[" + text + "/" + selected + "]";
	}
}

class NamedVector extends Vector {
	String name;

	public NamedVector(String name) {
		this.name = name;
	}

	public NamedVector(String name, Object elements[]) {
		this.name = name;
		for (int i = 0, n = elements.length; i < n; i++) {
			add(elements[i]);
		}
	}

	public String toString() {
		return "[" + name + "]";
	}
}
