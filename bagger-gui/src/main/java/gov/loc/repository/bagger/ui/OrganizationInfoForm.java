
package gov.loc.repository.bagger.ui;

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
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
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

public class OrganizationInfoForm extends JPanel implements PropertyChangeListener, FocusListener, KeyListener {
	private static final long serialVersionUID = -3231249644435262577L;
	private static final Log logger = LogFactory.getLog(OrganizationInfoForm.class);

	public static final String INFO_FORM_PAGE = "infoPage";
	private static final int MIN_ROWS = 10;

	private BindingFactory bindingFactory = null;
	private FormModel formModel;
    private JComponent infoForm;
    private JComponent focusField;
    private Dimension dimension = new Dimension(400, 370);
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
        BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory());
        JTextField nameTextField = new JTextField();
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight();
        int index = 1;
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
                switch (field.getComponentType()) {
                case BagInfoField.TEXTAREA_COMPONENT:
                    JComponent textarea = formBuilder.addTextArea(field.getName(), field.getLabel(), removeButton, "")[index];
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
                    JComponent comp = formBuilder.add(field.getName(), field.getLabel(), removeButton, "")[index];
                    comp.setEnabled(field.isEnabled());
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
    	if (dirty) bagView.infoInputPane.updateBagHandler.updateBag(this.defaultBag);
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
    		updateForm();
            bagView.infoInputPane.updateInfoFormsPane(true);
            bag.setInfo(bagInfo);
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
            	c = components[i];
            	if (c instanceof JLabel) {
                	JLabel label = (JLabel) c;
                	key = label.getText();
            	}
            	i++;
            	c = components[i];
            	i++;
            	c = components[i];
            	if (c instanceof JCheckBox) {
            		JCheckBox cb = (JCheckBox) c;
            		if (cb.isSelected()) {
            			BagInfoField field = getField(key);
            			if (field != null) fieldMap.remove(key);
            		}
            	} else if (c == selected) {
        			BagInfoField field = getField(key);
        			if (field != null) fieldMap.remove(key);
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
}
