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

import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;

public class NewFieldFrame extends JFrame implements ActionListener {
	private static final Log log = LogFactory.getLog(NewFieldFrame.class);
	private static final long serialVersionUID = 1L;
	private static final String TEXTFIELD = "Brief Text";
	private static final String TEXTAREA = "Extended Text";
	BagView bagView;
	DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(550, 200);
	BagInfoField field;
	JPanel addPanel;
	JButton okButton;
	JButton cancelButton;
    JComboBox fieldList;
    JTextField fieldName;
    JComboBox typeList;
	JCheckBox isRequiredCheckbox;
	JRadioButton stndFieldButton;
	JRadioButton newFieldButton;
	JPanel fieldGroupPanel;

	public NewFieldFrame(BagView bagView, String title) {
        super(title);
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		if (component != null) this.bagView = (BagView) component;
		else this.bagView = bagView;
		field = new BagInfoField();
		field.isEnabled(true);
		if (bagView != null) {
			bag = bagView.getBag();
	        getContentPane().removeAll();
	        addPanel = createComponents();
		} else {
			addPanel = new JPanel();
		}
        addPanel.setPreferredSize(preferredDimension);
        getContentPane().add(addPanel, BorderLayout.CENTER);
        pack();
    }

    private JPanel createComponents() {
    	FieldListHandler fieldListHandler = new FieldListHandler();
    	List<String> listModel = bagView.getBag().getInfo().getStandardBagFields();
        fieldList = new JComboBox(listModel.toArray());
        fieldList.setName(getMessage("baginfo.field.fieldlist"));
        fieldList.setSelectedItem("");
        fieldList.addActionListener(fieldListHandler);
        fieldList.setToolTipText(getMessage("baginfo.field.fieldlist.help"));

        fieldName = new JTextField(10);
        fieldName.setPreferredSize(fieldList.getPreferredSize());
        fieldName.addActionListener(new FieldNameHandler());
        fieldName.setToolTipText(getMessage("baginfo.field.name.help"));
        fieldName.setEnabled(false);

    	FieldButtonHandler fieldButtonHandler = new FieldButtonHandler();
        ButtonGroup fieldGroup = new ButtonGroup();
    	stndFieldButton = new JRadioButton("Standard");
    	stndFieldButton.setSelected(true);
    	fieldList.setEnabled(true);
    	fieldGroup.add(stndFieldButton);
    	stndFieldButton.addActionListener(fieldButtonHandler);
    	newFieldButton = new JRadioButton("New");
    	newFieldButton.setSelected(false);
    	newFieldButton.addActionListener(fieldButtonHandler);
    	fieldGroup.add(newFieldButton);
        fieldGroupPanel = new JPanel(new FlowLayout());
        fieldGroupPanel.add(stndFieldButton);
        fieldGroupPanel.add(fieldList);
        fieldGroupPanel.add(newFieldButton);
        fieldGroupPanel.add(fieldName);

        ArrayList<String> typeModel = new ArrayList<String>();
        typeModel.add(TEXTFIELD);
        typeModel.add(TEXTAREA);
        JLabel typeListLabel = new JLabel(bagView.getPropertyMessage("baginfo.field.typelist"));
        typeListLabel.setToolTipText(getMessage("baginfo.field.typelist.help"));
        typeList = new JComboBox(typeModel.toArray());
        typeList.setName(getMessage("baginfo.field.typelist"));
        typeList.setSelectedItem(TEXTFIELD);
        typeList.addActionListener(new TypeListHandler());
        typeList.setToolTipText(getMessage("baginfo.field.typelist.help"));

    	okButton = new JButton("Add");
    	okButton.addActionListener(new OkAddFieldHandler());
        okButton.setEnabled(true);

    	cancelButton = new JButton("Cancel");
    	cancelButton.addActionListener(new CancelAddFieldHandler());
    	cancelButton.setEnabled(true);

    	GridBagLayout layout = new GridBagLayout();
        GridBagConstraints glbc = new GridBagConstraints();

        int row = 0;
        buildConstraints(glbc, 0, row, 3, 1, 100, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.WEST);
        layout.setConstraints(fieldGroupPanel, glbc);
        row++;
        buildConstraints(glbc, 0, row, 1, 1, 20, 50, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(typeListLabel, glbc);
        buildConstraints(glbc, 1, row, 2, 1, 80, 50, GridBagConstraints.HORIZONTAL, GridBagConstraints.CENTER);
        layout.setConstraints(typeList, glbc);
        row++;
        buildConstraints(glbc, 0, row, 1, 1, 20, 50, GridBagConstraints.NONE, GridBagConstraints.WEST);
        layout.setConstraints(cancelButton, glbc);
        buildConstraints(glbc, 1, row, 1, 1, 80, 50, GridBagConstraints.NONE, GridBagConstraints.CENTER);
        layout.setConstraints(okButton, glbc);

        JPanel panel = new JPanel(layout);
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        panel.add(fieldGroupPanel);
    	panel.add(typeListLabel);
    	panel.add(typeList);
    	panel.add(cancelButton);
    	panel.add(okButton);

    	return panel;
    }

    public void actionPerformed(ActionEvent e) {
    	invalidate();
    	repaint();
    }

    private class FieldNameHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
    		//JTextField f = (JTextField)e.getSource();
    		String name = fieldName.getText();
    		field.setName(name.toLowerCase());
    		field.setLabel(name);
    	}
    }

    private class FieldRequiredHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
    		JCheckBox cb = (JCheckBox)e.getSource();

    		// Determine status
    		boolean isSelected = cb.isSelected();
    		if (isSelected) {
    			field.isRequired(true);
    		} else {
    			field.isRequired(false);
    		}
    	}
    }

    private class FieldButtonHandler extends AbstractAction {
    	private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			JRadioButton cb = (JRadioButton)e.getSource();
            boolean isSel = cb.isSelected();
        	if (cb == stndFieldButton) {
                if (isSel) {
            		fieldList.setEnabled(true);
            		fieldName.setEnabled(false);
            		fieldList.requestFocus();
                }
        	} else {
                if (isSel) {
            		fieldList.setEnabled(false);
            		fieldName.setEnabled(true);
            		fieldName.requestFocus();
                }
        	}
		}
    }

    private class FieldListHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
        	JComboBox jlist = (JComboBox)e.getSource();
        	String fieldLabel = (String) jlist.getSelectedItem();
        	// TODO: if not new, populate with stnd values
//        	if (fieldLabel.equalsIgnoreCase(DefaultBagInfo.FIELD_NEW_COMPONENT)) {
        	if (newFieldButton.isSelected()) {
                typeList.setSelectedItem(TEXTFIELD);
    			field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
                isRequiredCheckbox.setSelected(true);
    			field.isRequired(true);
                fieldName.setText("");
                fieldName.setEnabled(true);
                fieldName.requestFocus();
                field.setLabel("");
        	} else {
        		if (DefaultBagInfo.textAreaSet.contains(fieldLabel)) {
                    typeList.setSelectedItem(TEXTAREA);
        			field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
        		} else {
                    typeList.setSelectedItem(TEXTAREA);
        			field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        		}
        		if (DefaultBagInfo.requiredSet.contains(fieldLabel)) {
                    isRequiredCheckbox.setSelected(true);
        			field.isRequired(true);
        		} else {
                    isRequiredCheckbox.setSelected(false);
        			field.isRequired(false);
        		}
        		field.setName(fieldLabel.toLowerCase());
            	field.setLabel(fieldLabel);
                fieldName.setText(fieldLabel);
                fieldName.setEnabled(false);
        	}
        	addPanel.invalidate();
    	}
    }
    
    private class TypeListHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
        	JComboBox jlist = (JComboBox)e.getSource();
        	String type = (String) jlist.getSelectedItem();
        	if (type.equalsIgnoreCase(TEXTAREA)) {
        		field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
        	} else {
        		field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        	}
    	}
    }

    private class OkAddFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
    		DefaultBag bag = bagView.getBag();
    		DefaultBagInfo bagInfo = bag.getInfo();
    		String name = fieldName.getText();
    		field.setName(name.toLowerCase());
    		field.setLabel(name);
    		prepopulate(field);

    		HashMap<String, BagInfoField> currentMap = bagInfo.getFieldMap();
    		if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();
    		if (currentMap.isEmpty() || !currentMap.containsKey(field.getLabel())) {
    			currentMap.put(field.getLabel(), field);
    			setVisible(false);
    			bagInfo.setFieldMap(currentMap);
                bag.setInfo(bagInfo);
                bagView.setBag(bag);
                bagView.infoInputPane.updateInfoFormsPane(enabled);
    		} else {
    			bagView.showWarningErrorDialog("New Field Dialog", "Field: " + field.getLabel() + " already exists!");
    		}
        }
    }
    
    private void prepopulate(BagInfoField field) {
    	String label = field.getLabel();
    	
    	if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_LC_PROJECT)) {
    		field.setValue(bagView.getBag().getProject().getName());
    		field.isEnabled(false);
    	} else if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_BAGGING_DATE)) {
    		field.setValue(DefaultBagInfo.getTodaysDate());
    	} else if (DefaultBagInfo.readOnlySet.contains(label)) {
    		field.isEnabled(false);
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

    private String getMessage(String property) {
    	return bagView.getPropertyMessage(property);
    }
}