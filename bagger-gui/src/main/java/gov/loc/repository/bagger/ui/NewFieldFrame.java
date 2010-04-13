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

import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import javax.swing.AbstractAction;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FieldMetadata;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.application.ApplicationPage;
import org.springframework.richclient.application.PageComponent;
import org.springframework.richclient.core.DefaultMessage;
import org.springframework.richclient.dialog.TitlePane;

public class NewFieldFrame extends JDialog implements ActionListener {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(NewFieldFrame.class);
	// TODO: Any new field type needs to be handled in BagInfoInputPane.createBagInfo
	private static final String TEXTFIELD = "Brief Text";
	private static final String TEXTAREA = "Extended Text";
	private static final String LISTFIELD = "List";
	
	BagView bagView;
	DefaultBag bag = null;
	private Dimension preferredDimension = new Dimension(550, 380);
	BagInfoField field;
	JPanel addPanel;
	JButton okButton;
	JButton cancelButton;
    JComboBox fieldList;
    JTextField fieldName;
    JComboBox typeList;
	JCheckBox isRequiredCheckbox;
	JCheckBox isRequiredValue;
	JRadioButton stndFieldButton;
	JRadioButton newFieldButton;
	JPanel fieldGroupPanel;
    JLabel valueLabel;
	JTextField valueField;
	JComboBox listField;
	JButton listAddButton;
	JButton listRemoveButton;
	boolean edit = false;
	private Queue<ProfileField> profileFieldsQueue;
	private JButton btnSaveAndAdd;
	
	public NewFieldFrame(BagView bagView, BagInfoField field, String title) {
        //super(title);
		setTitle(title);
		Application app = Application.instance();
		ApplicationPage page = app.getActiveWindow().getPage();
		PageComponent component = page.getActiveComponent();
		if (component != null) this.bagView = BagView.instance;
		else this.bagView = bagView;
		if (bagView != null) {
			bag = bagView.getBag();
	        getContentPane().removeAll();
	        addPanel = createComponents(field);
		} else {
			addPanel = new JPanel();
		}
		
		if (field != null) {
			this.field = field;
		} else {
			this.field = new BagInfoField();
		}
		this.field.isEnabled(true);
        addPanel.setPreferredSize(preferredDimension);
        getContentPane().add(addPanel, BorderLayout.CENTER);
        setLocation(300, 200);
        pack();
    }
	
	public NewFieldFrame(BagView bagView, String title) {
       // super(title);
		setTitle(title);
        this.bagView = bagView;
        List<ProfileField> profileFields = bagView.bagProject.userProjectProfiles.get(bagView.getBag().getProfile().getName());
        Collections.sort(profileFields,new Comparator<ProfileField>() {

			public int compare(ProfileField o1, ProfileField o2) {
				return o1.getFieldName().compareTo(o2.getFieldName());}
		});
        
        if(profileFields == null || profileFields.size()==0)
        {
        	logger.info("There are no field in the profile " + bagView.getBag().getProfile().getName());
        	this.setVisible(false);
        	return;
        }
        profileFieldsQueue = new LinkedList<ProfileField>();
        profileFieldsQueue.addAll(profileFields);
        BagInfoField inofField = new BagInfoField(profileFieldsQueue.poll());
        updateField(inofField);
        setField(inofField);
        setLocation(300, 200);
        pack();
        
    }
	

	
	public void setField(BagInfoField field) {
		String name = field.getLabel();
		boolean b = false;
		for (int j=0; j < fieldList.getModel().getSize(); j++) {
			String proj = (String) fieldList.getModel().getElementAt(j);
    		if (name.trim().equalsIgnoreCase(proj.trim())) {
    			b = true;
    			break;
    		}
		}
		if (b) {
			stndFieldButton.setSelected(b);
	    	fieldList.setEnabled(b);
	    	fieldList.setSelectedItem(name);
		} else {
			stndFieldButton.setSelected(b);
	    	fieldList.setEnabled(b);
	    	newFieldButton.setSelected(!b);
	    	fieldList.setSelectedItem("");
	    	fieldName.setText(name);
		}
	}
	
	public void updateField(BagInfoField field)
	{
		if (bagView != null) {
			bag = bagView.getBag();
			getContentPane().removeAll();
			addPanel = createComponents(field);
		} else {
			addPanel = new JPanel();
		}
		if (field != null) {
			this.field = field;
		} else {
			this.field = new BagInfoField();
		}
		this.field.isEnabled(true);
		addPanel.setPreferredSize(preferredDimension);
		getContentPane().add(addPanel, BorderLayout.CENTER);
		pack();
	}
	
	
    private JPanel createComponents(BagInfoField field) {
        Border border = new EmptyBorder(5, 5, 5, 5);

        logger.debug("NewFieldFrame createComponents: " + field);
        if (field != null) {
    		this.edit = true;
        } else {
        	this.edit = false;
        }
        
        JPanel framePanel = new JPanel();
        framePanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        framePanel.setLayout(new BorderLayout(0, 0));
       
        
        JPanel titlePaneContainer = new JPanel(new BorderLayout());
        TitlePane titlePane = new TitlePane();
        
        if(!this.edit)
        {
        	titlePane.setTitle(bagView.getPropertyMessage("NewFieldFrame.title"));
        	titlePane.setMessage( new DefaultMessage(bagView.getPropertyMessage("NewFieldFrame.description")));
        }
        else
        {
        	titlePane.setTitle(bagView.getPropertyMessage("EditFieldFrame.title"));
        	titlePane.setMessage( new DefaultMessage(bagView.getPropertyMessage("EditFieldFrame.description")));
        }
		titlePaneContainer.add(titlePane.getControl());
		titlePaneContainer.add(new JSeparator(), BorderLayout.SOUTH);
		framePanel.add(titlePaneContainer,BorderLayout.NORTH);
        
    	JPanel contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		 framePanel.add(contentPane,BorderLayout.CENTER);
	
		JPanel panel_2 = new JPanel();
		contentPane.add(panel_2, BorderLayout.NORTH);
		panel_2.setLayout(new GridLayout(0, 1, 0, 0));
		
		JPanel panel = new JPanel();
		panel_2.add(panel);
		panel.setLayout(new FlowLayout(FlowLayout.LEFT, 5, 5));
		
		JLabel lblProfile = new JLabel(" Profile Name : ");
		panel.add(lblProfile);
		
		JLabel label = new JLabel("");
		panel.add(label);
		
		JLabel lblValue = new JLabel( this.bag.getProfile().getName());
		panel.add(lblValue);
		
		JPanel fieldContentPanel = new JPanel();
		contentPane.add(fieldContentPanel, BorderLayout.CENTER);
		GridBagLayout gbl_fieldContentPanel = new GridBagLayout();
		gbl_fieldContentPanel.columnWidths = new int[]{ 0, 0, 0, 0};
		gbl_fieldContentPanel.rowHeights = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};
		gbl_fieldContentPanel.columnWeights = new double[]{ 0.0, 0.0, 1.0, Double.MIN_VALUE};
		gbl_fieldContentPanel.rowWeights = new double[]{0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE};
		fieldContentPanel.setLayout(gbl_fieldContentPanel);
		
		JButton btnAddAllDefaultFeids = new JButton("Add All Default BagIt Fields");
		GridBagConstraints gbc_AddAllDefaultFeids = new GridBagConstraints();
		gbc_AddAllDefaultFeids.anchor = GridBagConstraints.EAST;
		gbc_AddAllDefaultFeids.insets = new Insets(0, 0, 5, 5);
		gbc_AddAllDefaultFeids.gridx = 2;
		gbc_AddAllDefaultFeids.gridy = 0;
		btnAddAllDefaultFeids.addActionListener(
				bagView.infoInputPane.bagInfoInputPane.bagInfoForm.new  AddFieldDefaultsHandler());
		
		fieldContentPanel.add(btnAddAllDefaultFeids, gbc_AddAllDefaultFeids);
		
		if(this.edit)
		{
			btnAddAllDefaultFeids.setVisible(false);
		}
		
		JLabel spacerLabel = new JLabel("                ");
		GridBagConstraints gbc_spacerLabel = new GridBagConstraints();
		gbc_spacerLabel.insets = new Insets(0, 0, 5, 0);
		gbc_spacerLabel.gridx = 3;
		gbc_spacerLabel.gridy = 0;
		fieldContentPanel.add(spacerLabel, gbc_spacerLabel);
		
		JLabel spacerLabelLeft = new JLabel("              ");
		GridBagConstraints gbc_spacerLabelLeft = new GridBagConstraints();
		gbc_spacerLabelLeft.insets = new Insets(0, 0, 5, 0);
		gbc_spacerLabelLeft.gridx = 0;
		gbc_spacerLabelLeft.gridy = 0;
		fieldContentPanel.add(spacerLabelLeft, gbc_spacerLabelLeft);
		
		JPanel addFieldPanel = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) addFieldPanel.getLayout();
		flowLayout_1.setVgap(0);
		flowLayout_1.setHgap(0);
		flowLayout_1.setAlignment(FlowLayout.RIGHT);
		GridBagConstraints gbc_addFieldPanel = new GridBagConstraints();
		gbc_addFieldPanel.anchor = GridBagConstraints.EAST;
		gbc_addFieldPanel.insets = new Insets(0, 0, 5, 5);
		gbc_addFieldPanel.gridx = 1;
		gbc_addFieldPanel.gridy = 1;
		fieldContentPanel.add(addFieldPanel, gbc_addFieldPanel);
		
		FieldButtonHandler fieldButtonHandler = new FieldButtonHandler();
        ButtonGroup fieldGroup = new ButtonGroup();
		
        newFieldButton = new JRadioButton("");
		addFieldPanel.add(newFieldButton);
		newFieldButton.addActionListener(fieldButtonHandler);
		fieldGroup.add(newFieldButton);
		 JLabel lblAddFeild = null;
		if(!this.edit)
			lblAddFeild = new JLabel("Add Feild");
		else
			lblAddFeild = new JLabel("Edit Feild");
		addFieldPanel.add(lblAddFeild);
		
		fieldName = new JTextField();
		GridBagConstraints gbc_textField = new GridBagConstraints();
		gbc_textField.insets = new Insets(0, 0, 5, 5);
		gbc_textField.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField.gridx = 2;
		gbc_textField.gridy = 1;
		fieldContentPanel.add(fieldName, gbc_textField);
		fieldName.setColumns(10);
		fieldName.addActionListener(new FieldNameHandler());
		fieldName.setToolTipText(getMessage("baginfo.field.name.help"));
		fieldName.setEnabled(false);
		
		JPanel addExistingFieldPanel = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) addExistingFieldPanel.getLayout();
		flowLayout_2.setVgap(0);
		flowLayout_2.setAlignment(FlowLayout.RIGHT);
		flowLayout_2.setHgap(0);
		GridBagConstraints gbc_addExistingFieldPanel = new GridBagConstraints();
		gbc_addExistingFieldPanel.anchor = GridBagConstraints.EAST;
		gbc_addExistingFieldPanel.insets = new Insets(0, 0, 5, 5);
		gbc_addExistingFieldPanel.fill = GridBagConstraints.VERTICAL;
		gbc_addExistingFieldPanel.gridx = 1;
		gbc_addExistingFieldPanel.gridy = 2;
		fieldContentPanel.add(addExistingFieldPanel, gbc_addExistingFieldPanel);
		
		stndFieldButton = new JRadioButton("");
		stndFieldButton.setSelected(true);
		addExistingFieldPanel.add(stndFieldButton);
		stndFieldButton.addActionListener(fieldButtonHandler);
		fieldGroup.add(stndFieldButton);
		JLabel lblAdd = null;
		if(!this.edit)
			lblAdd = new JLabel("Add Existing Field");
		else
			lblAdd = new JLabel("Edit Default Field");
		
		addExistingFieldPanel.add(lblAdd);
		
		FieldListHandler fieldListHandler = new FieldListHandler();
        List<String> listModel = bagView.getBag().getInfo().getStandardBagFields();

        fieldList = new JComboBox();

        fieldList = new JComboBox(listModel.toArray());
        fieldList.setName(getMessage("baginfo.field.fieldlist"));
        fieldList.setSelectedItem("");
        fieldList.addActionListener(fieldListHandler);
        fieldList.setToolTipText(getMessage("baginfo.field.fieldlist.help"));
		
		GridBagConstraints gbc_comboBox = new GridBagConstraints();
		gbc_comboBox.insets = new Insets(0, 0, 5, 5);
		gbc_comboBox.fill = GridBagConstraints.HORIZONTAL;
		gbc_comboBox.gridx = 2;
		gbc_comboBox.gridy = 2;
		fieldContentPanel.add(fieldList, gbc_comboBox);
		
		JLabel lblFieldType = new JLabel("Field Type");
		GridBagConstraints gbc_FieldType = new GridBagConstraints();
		gbc_FieldType.anchor = GridBagConstraints.EAST;
		gbc_FieldType.insets = new Insets(0, 0, 5, 5);
		gbc_FieldType.gridx = 1;
		gbc_FieldType.gridy = 3;
		fieldContentPanel.add(lblFieldType, gbc_FieldType);
		
		 ArrayList<String> typeModel = new ArrayList<String>();
	        typeModel.add(TEXTFIELD);
	        typeModel.add(TEXTAREA);
	        typeModel.add(LISTFIELD);
		typeList = new JComboBox(typeModel.toArray());
		GridBagConstraints gbc_fieldTypeCombo = new GridBagConstraints();
		gbc_fieldTypeCombo.insets = new Insets(0, 0, 5, 5);
		gbc_fieldTypeCombo.fill = GridBagConstraints.HORIZONTAL;
		gbc_fieldTypeCombo.gridx = 2;
		gbc_fieldTypeCombo.gridy = 3;
		fieldContentPanel.add(typeList, gbc_fieldTypeCombo);
		
		if (field == null) {
			typeList.setSelectedItem(TEXTFIELD);
		} else if (field.getComponentType() == BagInfoField.TEXTFIELD_COMPONENT) {
			typeList.setSelectedItem(TEXTFIELD);
		} else if (field.getComponentType() == BagInfoField.TEXTAREA_COMPONENT) {
			typeList.setSelectedItem(TEXTAREA);
		} else if (field.getComponentType() == BagInfoField.LIST_COMPONENT) {
			typeList.setSelectedItem(LISTFIELD);
		} else {
			typeList.setSelectedItem(TEXTFIELD);
		}
		typeList.addActionListener(new TypeListHandler());
		typeList.setToolTipText(getMessage("baginfo.field.typelist.help"));
		
		JLabel lblIsFieldRequired = new JLabel("Is Field Required");
		GridBagConstraints gbc_IsFieldRequired = new GridBagConstraints();
		gbc_IsFieldRequired.anchor = GridBagConstraints.EAST;
		gbc_IsFieldRequired.insets = new Insets(0, 0, 5, 5);
		gbc_IsFieldRequired.gridx = 1;
		gbc_IsFieldRequired.gridy = 4;
		fieldContentPanel.add(lblIsFieldRequired, gbc_IsFieldRequired);
		
		isRequiredCheckbox = new JCheckBox("");
		
		isRequiredCheckbox = new JCheckBox();
        isRequiredCheckbox.setBorder(border);
        if (field != null) {
    		isRequiredCheckbox.setSelected(field.isRequired());
        } else {
            isRequiredCheckbox.setSelected(false);
        }
        isRequiredCheckbox.addActionListener(new FieldRequiredHandler());
        isRequiredCheckbox.setToolTipText(getMessage("bag.label.isreq.help"));
        
		GridBagConstraints gbc_checkBoxIsFieldRequired = new GridBagConstraints();
		gbc_checkBoxIsFieldRequired.insets = new Insets(0, 0, 5, 5);
		gbc_checkBoxIsFieldRequired.anchor = GridBagConstraints.WEST;
		gbc_checkBoxIsFieldRequired.gridx = 2;
		gbc_checkBoxIsFieldRequired.gridy = 4;
		fieldContentPanel.add(isRequiredCheckbox, gbc_checkBoxIsFieldRequired);
		
		JLabel lblDefaultFeildValue = new JLabel("Default Field Value");
		GridBagConstraints gbc_DefaultFeildValue = new GridBagConstraints();
		gbc_DefaultFeildValue.anchor = GridBagConstraints.EAST;
		gbc_DefaultFeildValue.insets = new Insets(0, 0, 5, 5);
		gbc_DefaultFeildValue.gridx = 1;
		gbc_DefaultFeildValue.gridy = 5;
		fieldContentPanel.add(lblDefaultFeildValue, gbc_DefaultFeildValue);
		
		valueField = new JTextField();
		GridBagConstraints gbc_textField_1 = new GridBagConstraints();
		gbc_textField_1.insets = new Insets(0, 0, 5, 5);
		gbc_textField_1.fill = GridBagConstraints.HORIZONTAL;
		gbc_textField_1.gridx = 2;
		gbc_textField_1.gridy = 5;
		fieldContentPanel.add(valueField, gbc_textField_1);
		valueField.setColumns(10);
		
		JPanel buttonPannel = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) buttonPannel.getLayout();
		flowLayout_3.setAlignment(FlowLayout.RIGHT);
		flowLayout_3.setVgap(0);
		flowLayout_3.setHgap(5);
		GridBagConstraints gbc_buttonPannel = new GridBagConstraints();
		gbc_buttonPannel.insets = new Insets(0, 0, 5, 5);
		gbc_buttonPannel.fill = GridBagConstraints.BOTH;
		gbc_buttonPannel.gridx = 1;
		gbc_buttonPannel.gridy = 6;
		fieldContentPanel.add(buttonPannel, gbc_buttonPannel);
		
		listAddButton = new JButton("Add Item");
		listAddButton.addActionListener(new ListAddHandler());
		buttonPannel.add(listAddButton);
		
		listRemoveButton = new JButton("Remove Item");
		listRemoveButton.addActionListener(new ListRemoveHandler());
		if (field != null && field.getComponentType() == BagInfoField.LIST_COMPONENT) {
			List<String> model = field.getElements();
			listField = new JComboBox(model.toArray());
			listField.invalidate();
			listField.setSelectedItem(field.getValue());
			listField.addActionListener(new ListFieldHandler());
			listField.setEnabled(true);
			valueField.setEnabled(false);
			listAddButton.setEnabled(true);
			listRemoveButton.setEnabled(true);
		} else {
			listField = new JComboBox();
			listField.addActionListener(new ListFieldHandler());
			listField.setEnabled(false);
			valueField.setEnabled(true);
			listAddButton.setEnabled(false);
			listRemoveButton.setEnabled(false);
		}
		
		buttonPannel.add(listRemoveButton);
		
		GridBagConstraints gbc_listField = new GridBagConstraints();
		gbc_listField.insets = new Insets(0, 0, 5, 5);
		gbc_listField.fill = GridBagConstraints.HORIZONTAL;
		gbc_listField.gridx = 2;
		gbc_listField.gridy = 6;
		fieldContentPanel.add(listField, gbc_listField);
		
		JLabel lblIsFieldValueRequired = new JLabel("Is Field Value Required");
		GridBagConstraints gbc_IsFieldValueRequired = new GridBagConstraints();
		gbc_IsFieldValueRequired.anchor = GridBagConstraints.EAST;
		gbc_IsFieldValueRequired.insets = new Insets(0, 0, 0, 5);
		gbc_IsFieldValueRequired.gridx = 1;
		gbc_IsFieldValueRequired.gridy = 7;
		fieldContentPanel.add(lblIsFieldValueRequired, gbc_IsFieldValueRequired);
		
		isRequiredValue = new JCheckBox();
        isRequiredValue.setBorder(border);
        if (field != null) {
    		isRequiredValue.setSelected(field.isRequiredvalue());
        } else {
            isRequiredValue.setSelected(false);
        }
        isRequiredValue.addActionListener(new RequiredValueHandler());
        isRequiredValue.setToolTipText(getMessage("bag.label.isreqvalue.help"));
		GridBagConstraints gbc_checkBoxIsFieldValueRequired = new GridBagConstraints();
		gbc_checkBoxIsFieldValueRequired.insets = new Insets(0, 0, 0, 5);
		gbc_checkBoxIsFieldValueRequired.anchor = GridBagConstraints.WEST;
		gbc_checkBoxIsFieldValueRequired.gridx = 2;
		gbc_checkBoxIsFieldValueRequired.gridy = 7;
		fieldContentPanel.add(isRequiredValue, gbc_checkBoxIsFieldValueRequired);
		
		JPanel dialogButtonpanel = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) dialogButtonpanel.getLayout();
		flowLayout_4.setVgap(15);
		flowLayout_4.setHgap(4);
		contentPane.add(dialogButtonpanel, BorderLayout.SOUTH);
		
		JButton btnCancel = new JButton("Cancel");
		dialogButtonpanel.add(btnCancel);
		btnCancel.addActionListener(new CancelAddFieldHandler());
		if(this.edit)
		{
			JButton removeFieldHandler= new JButton("Delete Feild");
			removeFieldHandler.addActionListener(new RemoveFieldHandler());
			dialogButtonpanel.add(removeFieldHandler);
		}
		
		if(this.edit)
			btnSaveAndAdd = new JButton("Save and Edit Another");
		else
		    btnSaveAndAdd = new JButton("Save and Add Another");
		
		dialogButtonpanel.add(btnSaveAndAdd);
		if(profileFieldsQueue != null && profileFieldsQueue.size()==0)
			btnSaveAndAdd.setEnabled(false);
		btnSaveAndAdd.addActionListener(new AddAnotherFieldHandler());
		
		JButton btnSaveAndClose = new JButton("Save And Close");
		dialogButtonpanel.add(btnSaveAndClose);
		btnSaveAndClose.addActionListener(new OkAddFieldHandler());

    	return framePanel;
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

    private class RequiredValueHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
    		JCheckBox cb = (JCheckBox)e.getSource();
                
    		// Determine status
    		boolean isSelected = cb.isSelected();
    		if (isSelected) {
    			field.isRequiredvalue(true);
    			field.isEditable(false);
    			field.isEnabled(false);
    		} else {
    			field.isRequiredvalue(false);
    			field.isEditable(true);
    			field.isEnabled(true);
    		}
    	}
    }

    private class FieldButtonHandler extends AbstractAction {
    	private static final long serialVersionUID = 1L;
		public void actionPerformed(ActionEvent e) {
			JRadioButton cb = (JRadioButton)e.getSource();
            boolean isSel = cb.isSelected();
        	if (cb.equals(stndFieldButton)) {
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
        	// if not new, populate with stnd values
        	if (newFieldButton.isSelected()) {
//                typeList.setSelectedItem(TEXTFIELD);
        		if (field.getComponentType() == BagInfoField.LIST_COMPONENT) {
        			typeList.setSelectedItem(LISTFIELD);
        		} else if (DefaultBagInfo.textAreaSet.contains(fieldLabel.trim())) {
                    typeList.setSelectedItem(TEXTAREA);
        			field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
        		} else {
                    typeList.setSelectedItem(TEXTFIELD);
        			field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        		}
    			field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
                fieldName.setText("");
                fieldName.setEnabled(true);
                fieldName.requestFocus();
                field.setLabel("");
        	} else {
        		if (field.getComponentType() == BagInfoField.LIST_COMPONENT) {
        			typeList.setSelectedItem(LISTFIELD);
        		} else if (DefaultBagInfo.textAreaSet.contains(fieldLabel.trim())) {
                    typeList.setSelectedItem(TEXTAREA);
        			field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
        		} else {
                    typeList.setSelectedItem(TEXTFIELD);
        			field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        		}
        		if (bagView.getBag().getInfo().getRequiredSet().contains(fieldLabel)) {
        			field.isRequired(true);
        		} else {
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
        	if (type.trim().equalsIgnoreCase(TEXTAREA)) {
        		valueField.setEnabled(true);
        		listField.setEnabled(false);
        		listAddButton.setEnabled(false);
        		listRemoveButton.setEnabled(false);
        		field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
        	} else if (type.trim().equalsIgnoreCase(LISTFIELD)) {
        		valueField.setEnabled(false);
        		listField.setEnabled(true);
        		listAddButton.setEnabled(true);
        		listRemoveButton.setEnabled(true);
        		field.setComponentType(BagInfoField.LIST_COMPONENT);
        	} else {
        		valueField.setEnabled(true);
        		listField.setEnabled(false);
        		listAddButton.setEnabled(false);
        		listRemoveButton.setEnabled(false);
        		field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
        	}
    	}
    }

    private class ListFieldHandler extends AbstractAction {
    	private static final long serialVersionUID = 75893358194076314L;
    	public void actionPerformed(ActionEvent e) {
        	JComboBox jlist = (JComboBox)e.getSource();
        	String item = (String) jlist.getSelectedItem();
        	valueField.setText(item);
        	field.setValue(item);
    	}
    }

    private class ListAddHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			listField.invalidate();
			NewItemFrame newItemFrame = new NewItemFrame(bagView, listField, "Add Field Item");
			newItemFrame.setVisible(true);
		}
    }

    private class ListRemoveHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
        	String item = (String) listField.getSelectedItem();
        	if (item != null) {
        		listField.removeItem(item);
        		listField.invalidate();
        	}
		}
    }
    
    private class RemoveFieldHandler extends AbstractAction{

		public void actionPerformed(ActionEvent e) {
			
			DefaultBag bag = bagView.getBag();
			DefaultBagInfo bagInfo = bag.getInfo();
			String fieldName = field.getLabel().equals("")?field.getName():field.getLabel();
			bagView.bagProject.removeProjectProfile(bagView.getBag().getProfile(), fieldName);
			HashMap<String, BagInfoField> currentMap = bagInfo.getFieldMap();
			if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();
			currentMap.remove(fieldName);

			bagInfo.setFieldMap(currentMap);
			bag.setInfo(bagInfo);
			bagView.setBag(bag);
			bagView.infoInputPane.updateInfoFormsPane(enabled);

			if(profileFieldsQueue != null)
			{
				ProfileField profileField = profileFieldsQueue.poll();
				if(profileFieldsQueue.size() == 0)
					btnSaveAndAdd.setEnabled(false);
				if(profileField == null)
				{
					setVisible(false);
					return;
				}

				BagInfoField bagInfoField = new BagInfoField(profileField);
				updateField(bagInfoField);
				setField(bagInfoField);
			}
		}
    }

    private class OkAddFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
    		DefaultBag bag = bagView.getBag();
    		DefaultBagInfo bagInfo = bag.getInfo();
    		String oldfieldName = field.getLabel().equals("")?field.getName():field.getLabel();
    		prepopulate(field);
    		String name = fieldName.getText();
    		
    		field.setValue(valueField.getText().trim());
			List<String> elements = new ArrayList<String>();
    		if (field.getComponentType() == BagInfoField.LIST_COMPONENT) {
    			listField.invalidate();
    			if (listField.getSelectedItem() == null) {
        			field.setValue("");
    			} else {
        			for (int i=0; i < listField.getItemCount(); i++) {
        				elements.add(listField.getItemAt(i).toString());
        			}
        			field.setValue(listField.getSelectedItem().toString());
    			}
    		}
			field.setElements(elements);

    		if (field.getComponentType() == BagInfoField.LIST_COMPONENT && field.getValue().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "List field: " + field.getLabel() + " must have a value!");
    			return;
    		}
    		if (field.isRequired() && field.getValue().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "Required field: " + field.getLabel() + " must have a value!");
    			return;
    		}
    		if (field.isRequiredvalue() && field.getValue().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "Field: " + field.getLabel() + " must have a default value!");
    			return;
    		}
    		if (name.trim().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "Field name must be selected!");
    			return;
    		}
    		field.setName(name);
    		field.setLabel(name);
    		HashMap<String, BagInfoField> currentMap = bagInfo.getFieldMap();
    		if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();
//    		if (currentMap.isEmpty() || !currentMap.containsKey(field.getLabel())) {
    			setVisible(false);
    			bagView.bagProject.removeProjectProfile(bagView.getBag().getProfile(), oldfieldName);
    			bagView.bagProject.addProjectField(field);
    			currentMap.remove(oldfieldName);
    			currentMap.put(field.getLabel(), field);
    			bagInfo.setFieldMap(currentMap);
                bag.setInfo(bagInfo);
                bagView.setBag(bag);
                bagView.infoInputPane.updateInfoFormsPane(enabled);
//    		} else {
//    			bagView.showWarningErrorDialog("New Field Dialog", "Field: " + field.getLabel() + " already exists!");
//    		}
            bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
    		bagView.infoInputPane.bagInfoInputPane.bagInfoForm.requestFocus();
    		
        }
    }
    
    
    private class AddAnotherFieldHandler extends AbstractAction {
		private static final long serialVersionUID = 1L;

		public void actionPerformed(ActionEvent e) {
			DefaultBag bag = bagView.getBag();
    		DefaultBagInfo bagInfo = bag.getInfo();
    		String oldfieldName = field.getLabel().equals("")?field.getName():field.getLabel();
    		if(!NewFieldFrame.this.edit)
    		{
    			oldfieldName = "";
    		}
    		prepopulate(field);
    		String name = fieldName.getText();
    		
    		field.setValue(valueField.getText().trim());
			List<String> elements = new ArrayList<String>();
    		if (field.getComponentType() == BagInfoField.LIST_COMPONENT) {
    			listField.invalidate();
    			if (listField.getSelectedItem() == null) {
        			field.setValue("");
    			} else {
        			for (int i=0; i < listField.getItemCount(); i++) {
        				elements.add(listField.getItemAt(i).toString());
        			}
        			field.setValue(listField.getSelectedItem().toString());
    			}
    		}
			field.setElements(elements);

    		if (field.getComponentType() == BagInfoField.LIST_COMPONENT && field.getValue().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "List field: " + field.getLabel() + " must have a value!");
    			return;
    		}
    		if (field.isRequired() && field.getValue().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "Required field: " + field.getLabel() + " must have a value!");
    			return;
    		}
    		if (field.isRequiredvalue() && field.getValue().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "Field: " + field.getLabel() + " must have a default value!");
    			return;
    		}
    		if (name.trim().isEmpty()) {
    			bagView.showWarningErrorDialog("New Field Dialog", "Field name must be selected!");
    			return;
    		}
    		field.setName(name);
    		field.setLabel(name);
    		HashMap<String, BagInfoField> currentMap = bagInfo.getFieldMap();
    		if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();
//    		if (currentMap.isEmpty() || !currentMap.containsKey(field.getLabel())) {
    			//setVisible(false);
    			bagView.bagProject.removeProjectProfile(bagView.getBag().getProfile(), oldfieldName);
    			bagView.bagProject.addProjectField(field);
    			currentMap.remove(oldfieldName);
    			currentMap.put(field.getLabel(), field);
    			bagInfo.setFieldMap(currentMap);
                bag.setInfo(bagInfo);
                bagView.setBag(bag);
                bagView.infoInputPane.updateInfoFormsPane(enabled);
//    		} else {
//    			bagView.showWarningErrorDialog("New Field Dialog", "Field: " + field.getLabel() + " already exists!");
//    		}
    		//bagView.infoInputPane.bagInfoInputPane.bagInfoForm.requestFocus();
            if(profileFieldsQueue != null)
            {
            	ProfileField profileField = profileFieldsQueue.poll();
            	if(profileFieldsQueue.size() == 0)
            		btnSaveAndAdd.setEnabled(false);
            	
            	BagInfoField bagInfoField = new BagInfoField(profileField);
			    updateField(bagInfoField);
			    setField(bagInfoField);
            }
            if(!NewFieldFrame.this.edit)
            {
            	 updateField(null);
            }
        }
    }
    
    private void prepopulate(BagInfoField field) {
    	String label = field.getLabel();
    	
    	if (label.equalsIgnoreCase(DefaultBagInfo.FIELD_LC_PROJECT)) {
    		field.setValue(bagView.getBag().getProfile().getName());
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

    private String getMessage(String property) {
    	return bagView.getPropertyMessage(property);
    }
}