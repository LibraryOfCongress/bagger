
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.ProjectProfile;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagit.Bag;

import javax.swing.AbstractAction;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.FormModel;
import org.springframework.richclient.application.ApplicationServicesLocator;
import org.springframework.richclient.form.AbstractForm;
import org.springframework.richclient.form.binding.BindingFactory;
import org.springframework.richclient.form.binding.BindingFactoryProvider;

public class OrganizationInfoForm extends JPanel implements PropertyChangeListener, FocusListener, KeyListener, MouseListener {
	private static final long serialVersionUID = -3231249644435262577L;
	private static final Log logger = LogFactory.getLog(OrganizationInfoForm.class);

	public static final String INFO_FORM_PAGE = "infoPage";
	private static final int MIN_ROWS = 12;

	private BindingFactory bindingFactory = null;
	private FormModel formModel;
    private JComponent infoForm;
    public JComponent focusField;
    private Dimension dimension = new Dimension(400, 300);
    private BagView bagView;
    private DefaultBag defaultBag;
    private HashMap<String, BagInfoField> fieldMap;
    private JPanel buttonPanel;
    private JComponent  form;
	protected Bag bag;
	public boolean dirty = false;
	private boolean enabled;
	private NewFieldFrame newFieldFrame;
	
    public OrganizationInfoForm(FormModel formModel, BagView bagView, HashMap<String, BagInfoField> map, boolean enabled) {
//        super(formModel, INFO_FORM_PAGE);
    	this.formModel = formModel;
        this.bagView = bagView;
        this.defaultBag = bagView.getBag();
        this.bag = this.defaultBag.getBag();
		this.fieldMap = map;
		this.enabled = enabled;
		
		this.setLayout(new BorderLayout());
		buttonPanel = createButtonPanel(enabled);
		this.add(buttonPanel, BorderLayout.NORTH);
		form = createFormControl();
        this.add(form, BorderLayout.CENTER);
    }
    
    public JComponent getForm() {
    	return this.form;
    }
    
    public void setBagView(BagView bagView) {
    	this.bagView = bagView;
    }

    public void setFieldMap(HashMap<String, BagInfoField> map) {
    	this.fieldMap = map;
    }
    
    public HashMap<String, BagInfoField> getFieldMap() {
    	return this.fieldMap;
    }

    protected JComponent createFormControl() {
    	int rowCount = 1;
        ImageIcon requiredIcon = bagView.getPropertyImage("bag.required.image");
        BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory(), requiredIcon);
        JTextField nameTextField = new JTextField();
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight() - 1;
        int index = 2;
        int count = 0;

        formBuilder.row();
        if (fieldMap != null && !fieldMap.isEmpty()) {
			Set<String> keys = fieldMap.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
            	BagInfoField field = fieldMap.get(key);
                formBuilder.row();
                rowCount++;
                ImageIcon imageIcon = bagView.getPropertyImage("bag.delete.image");
            	JButton removeButton = new JButton(imageIcon);
            	Dimension dimension = removeButton.getPreferredSize();
            	dimension.width = imageIcon.getIconWidth();
            	removeButton.setMaximumSize(dimension);
            	removeButton.setOpaque(false);
            	removeButton.setBorderPainted(false);
            	removeButton.setContentAreaFilled(false);
            	removeButton.addActionListener(new RemoveFieldHandler());
				logger.debug("OrganizationInfoForm add: " + field);
				if (field.getValue().length() > 30) {
					field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
				}
            	if (field.isRequired()) {
            		removeButton = new JButton();
            		removeButton.setOpaque(false);
            		removeButton.setBorderPainted(false);
            		removeButton.setContentAreaFilled(false);
            	}
                switch (field.getComponentType()) {
                case BagInfoField.TEXTAREA_COMPONENT:
                    JComponent[] tlist = formBuilder.addTextArea(field.getName(), field.isRequired(), field.getLabel(), removeButton, ""); 
                    JComponent tcomp = tlist[0];
                    tcomp.addMouseListener(this);
                    JComponent textarea = tlist[index];
                    textarea.setEnabled(field.isEnabled());
            		textarea.addFocusListener(this);
            		textarea.addKeyListener(this);
            		textarea.addPropertyChangeListener(this);
            		((NoTabTextArea) textarea).setText(field.getValue());
            		((NoTabTextArea) textarea).setBorder(new EmptyBorder(1,1,1,1));
            		((NoTabTextArea) textarea).setColumns(1);
            		((NoTabTextArea) textarea).setRows(3);
            		((NoTabTextArea) textarea).setLineWrap(true);
            		if (count == 0) focusField = textarea;
                    rowCount += 1;
                	break;
                case BagInfoField.TEXTFIELD_COMPONENT:
                    JComponent[] flist = formBuilder.add(field.getName(), field.isRequired(), field.getLabel(), removeButton, "");
                    JComponent comp = flist[index];
                    comp.setEnabled(field.isEnabled());
                    JComponent mcomp = flist[0];
                    mcomp.addMouseListener(this);
                    comp.addFocusListener(this);
                    comp.addKeyListener(this);
                    comp.addPropertyChangeListener(this);
                    ((JTextField) comp).setText(field.getValue());
            		if (count == 0) focusField = comp;
                	break;
                default:
                }
                count++;
            }
            focusField.requestFocus();
        }
        infoForm = formBuilder.getForm();
        if (rowCount < MIN_ROWS) rowCount = MIN_ROWS;
        int height = 2 * fieldHeight * rowCount;
        dimension = new Dimension(400, height);
        infoForm.setPreferredSize(dimension);
        
        return infoForm;
    }

    public void propertyChange(PropertyChangeEvent evt) {
    	if (bagView != null) bagView.updatePropButton.setEnabled(true);
    	dirty = true;
    }

    public boolean requestFocusInWindow() {
    	if (focusField != null) {
    		return focusField.requestFocusInWindow();
    	} else {
    		return false;
    	}
    }

    public void focusGained(FocusEvent evt) {
    }
    
    public void focusLost(FocusEvent evt) {
    	bagView.infoInputPane.updateBagHandler.updateBag(this.defaultBag);
		bagView.bagInfoInputPane.setSelectedIndex(0);
    }

	public BindingFactory getBindingFactory() {
 		if (bindingFactory == null) {
 			BindingFactoryProvider bfp = (BindingFactoryProvider) ApplicationServicesLocator.services().getService(BindingFactoryProvider.class);
			bindingFactory = bfp.getBindingFactory(formModel);
		} 
		return bindingFactory;
	} 

    private JPanel createButtonPanel(boolean enabled) {
    	JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));

    	JButton addButton = new JButton(bagView.getPropertyMessage("bag.button.field.add"));
    	addButton.addActionListener(new AddFieldHandler());
    	addButton.setOpaque(true);
    	addButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.add.help"));
    	addButton.setEnabled(enabled);
    	buttonPanel.add(addButton);
    	
    	JButton addDefaultsButton = new JButton(bagView.getPropertyMessage("bag.button.defaults.add"));
    	addDefaultsButton.addActionListener(new AddFieldDefaultsHandler());
    	addDefaultsButton.setOpaque(true);
    	addDefaultsButton.setToolTipText(bagView.getPropertyMessage("bag.button.defaults.add.help"));
    	addDefaultsButton.setEnabled(enabled);
    	buttonPanel.add(addDefaultsButton);

    	return buttonPanel;
    }
    
    private class AddFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
//        	if (dirty) bagView.infoInputPane.updateBagHandler.updateBag(defaultBag);
	        newFieldFrame = new NewFieldFrame(bagView, bagView.getPropertyMessage("bag.frame.addfield"));
	        newFieldFrame.setVisible(true);
       	}
    }

    public void updateForm() {
    	this.removeAll();
    	this.invalidate();
    	this.add(buttonPanel);
    	form = createFormControl();
    	this.add(form);
    }
    
    private class AddFieldDefaultsHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		DefaultBag bag = bagView.getBag();
    		DefaultBagInfo bagInfo = bag.getInfo();
    		bagInfo.setBag(bag);
    		bagInfo.createStandardFieldMap(true);
    		bagInfo.createProfileFieldList(true);
    		updateForm();
            bagView.infoInputPane.updateInfoFormsPane(true);
            bag.setInfo(bagInfo);
            bag.copyBagToForm();
            bagView.setBag(bag);
       	}
    }

    private class RemoveFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		Component selected = (Component) e.getSource();
    		String key = "";
            Component[] components = form.getComponents();
            for (int i=0; i<components.length; i++) {
            	Component c;
            	// See BagTableFormBuilder.addBinding for component info
            	// Field label
            	c = components[i];
            	if (c instanceof JLabel) {
                	JLabel label = (JLabel) c;
                	key = label.getText();
            	}
            	i++;
            	// Required button
            	c = components[i];
            	i++;
            	// Input text field
            	c = components[i];
            	i++;
            	// Remove button
            	c = components[i];
            	if (c instanceof JButton) {
            		if (c == selected) {
            			BagInfoField field = getField(key);
            			if (field != null) fieldMap.remove(key);
            		}
            	}
            }
            DefaultBagInfo info = defaultBag.getInfo();
            info.setFieldMap(fieldMap);
            defaultBag.setInfo(info);
            bagView.setBag(defaultBag);
            bagView.infoInputPane.updateInfoFormsPane(true);
       	}
    }

    private BagInfoField getField(String key) {
    	BagInfoField field = null;
		Set<String> keys = fieldMap.keySet();
		for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
			String keySet = (String) iter.next();
			if (keySet.equalsIgnoreCase(key)) {
				field = fieldMap.get(key);
				return field;
			}
		}
    	return field;
    }

    public void keyTyped(KeyEvent event) {
    	if (bagView != null) bagView.updatePropButton.setEnabled(true);
    }

    public void keyPressed(KeyEvent event) {
    	if (bagView != null) bagView.updatePropButton.setEnabled(true);
    }

    public void keyReleased(KeyEvent event) {
    	if (bagView != null) bagView.updatePropButton.setEnabled(true);
    }

    public void mouseReleased(MouseEvent event) {
    }

    public void mousePressed(MouseEvent event) {
    }

    public void mouseEntered(MouseEvent event) {
    }

    public void mouseExited(MouseEvent event) {
    }

    public void mouseClicked(MouseEvent event) {
    	if (event.getClickCount() == 2) {
    		// TODO: edit selected field
	        newFieldFrame = new NewFieldFrame(bagView, bagView.getPropertyMessage("bag.frame.addfield")); 
	        Collection<ProjectProfile> fieldList = bagView.userProjectProfiles;
	        JComponent component = (JComponent) event.getComponent();
	        if (component instanceof JLabel) {
		        BagInfoField field = new BagInfoField();
		        String txt = ((JLabel)component).getText();
		        field.setLabel(txt.trim());
		    	for (Iterator<ProjectProfile> iter = fieldList.iterator(); iter.hasNext();) {
		    		Project project = bagView.getBag().getProject();
					ProjectProfile projectProfile = (ProjectProfile) iter.next();
					if (txt.equalsIgnoreCase(projectProfile.getFieldName()) && project.getId() == projectProfile.getProjectId()) {
						field.setLabel(projectProfile.getFieldName());
						field.setName(field.getLabel().toLowerCase());
						field.isEnabled(!projectProfile.getIsValueRequired());
						field.isEditable(!projectProfile.getIsValueRequired());
						field.isRequiredvalue(projectProfile.getIsValueRequired());
						field.isRequired(projectProfile.getIsRequired());
						field.setValue(projectProfile.getFieldValue());
					}
				}
		        newFieldFrame.setField(field);
		        newFieldFrame.setVisible(true);
	        }
    	}
    }

}
