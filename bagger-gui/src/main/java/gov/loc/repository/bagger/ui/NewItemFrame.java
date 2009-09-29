/*
 * Copyright 2002-2004 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.impl.DefaultBag;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

public class NewItemFrame extends JFrame implements ActionListener {
	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(550, 200);
	JPanel addPanel;
	JButton okButton;
	JButton cancelButton;
    JTextField itemName;
    JPanel itemPanel;
    JComboBox itemList;

	public NewItemFrame(BagView bagView, JComboBox itemList, String title) {
        super(title);
		this.bagView = bagView;
		this.itemList = itemList;
		bag = bagView.getBag();
		getContentPane().removeAll();
		addPanel = createComponents();
        addPanel.setPreferredSize(preferredDimension);
        getContentPane().add(addPanel, BorderLayout.CENTER);
        pack();
    }

    private JPanel createComponents() {
    	JLabel itemLabel = new JLabel("Add Item");
    	itemName = new JTextField(10);
    	itemName.setEnabled(true);
    	GridBagLayout gridLayout = new GridBagLayout();
    	GridBagConstraints gbc = new GridBagConstraints();
    	buildConstraints(gbc, 0, 0, 1, 1, 20, 50, GridBagConstraints.NONE, GridBagConstraints.WEST);
    	gridLayout.setConstraints(itemLabel, gbc);
    	buildConstraints(gbc, 1, 0, 1, 1, 80, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
    	gridLayout.setConstraints(itemName, gbc);
    	JPanel itemPanel = new JPanel(gridLayout);
    	itemPanel.add(itemLabel);
    	itemPanel.add(itemName);

    	okButton = new JButton("Add");
    	okButton.addActionListener(new OkAddFieldHandler());
        okButton.setEnabled(true);

    	cancelButton = new JButton("Cancel");
    	cancelButton.addActionListener(new CancelAddFieldHandler());
    	cancelButton.setEnabled(true);

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        int row = 0;
        buildConstraints(glbc, 0, row, 2, 1, 100, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        layout.setConstraints(itemPanel, glbc);
    	row++;
        buildConstraints(glbc, 0, row, 1, 1, 20, 50, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(cancelButton, glbc);
        buildConstraints(glbc, 1, row, 1, 1, 80, 50, GridBagConstraints.NONE, GridBagConstraints.CENTER);
        layout.setConstraints(okButton, glbc);

        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(itemPanel);
    	panel.add(cancelButton);
    	panel.add(okButton);

    	return panel;
    }

    public void actionPerformed(ActionEvent e) {
    	invalidate();
    	repaint();
    }

    private class OkAddFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
    		String name = itemName.getText().trim();
    		boolean b = false;
    		for (int i=0; i < itemList.getItemCount(); i++) {
    			String s = (String) itemList.getItemAt(i);
    			if (s != null && name.equalsIgnoreCase(s.trim())) {
    				b = true;
    				break;
    			}
    		}
    		setVisible(false);
    		if (b) {
    			bagView.showWarningErrorDialog("New Item Dialog", "Item already exists!");
    			return;
    		} else {
        		itemList.addItem(name);
        		itemList.invalidate();
        		itemList.setSelectedItem(name);
    		}
        }
    }

    private class CancelAddFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			setVisible(false);
        }
    }

    private void buildConstraints(GridBagConstraints gbc,int x, int y, int w, int h, int wx, int wy, int fill, int anchor) {
    	gbc.gridx = x; // start cell in a row
    	gbc.gridy = y; // start cell in a column
    	gbc.gridwidth = w; // how many column does the control occupy in the row
    	gbc.gridheight = h; // how many column does the control occupy in the column
    	gbc.weightx = wx; // relative horizontal size
    	gbc.weighty = wy; // relative vertical size
    	gbc.fill = fill; // the way how the control fills cells
    	gbc.anchor = anchor; // alignment
    }

}