
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagit.Bag;

import javax.swing.AbstractAction;
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
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeEvent;
import java.util.List;

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
    private List<BagInfoField> fieldList;
    private JPanel buttonPanel;
    private JComponent  form;
	protected Bag bag;
	public boolean dirty = false;
	private boolean enabled;
	private NewFieldFrame newFieldFrame;
	
    public OrganizationInfoForm(FormModel formModel, BagView bagView, List<BagInfoField> list, boolean enabled) {
//        super(formModel, INFO_FORM_PAGE);
    	this.formModel = formModel;
        this.bagView = bagView;
        this.defaultBag = bagView.getBag();
        this.bag = this.defaultBag.getBag();
		this.fieldList = list;
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

    public void setFieldList(List<BagInfoField> list) {
    	this.fieldList = list;
    }
    
    public List<BagInfoField> getFieldList() {
    	return this.fieldList;
    }

    protected JComponent createFormControl() {
    	int rowCount = 1;
        BagTableFormBuilder formBuilder = new BagTableFormBuilder(getBindingFactory());
        JTextField nameTextField = new JTextField();
        int fieldHeight = nameTextField.getFontMetrics(nameTextField.getFont()).getHeight();
        int index = 1;

        formBuilder.row();
        if (fieldList != null && !fieldList.isEmpty()) {
            for (int i=0; i < fieldList.size(); i++) {
            	BagInfoField field = fieldList.get(i);
                formBuilder.row();
                rowCount++;
                switch (field.getComponentType()) {
                case BagInfoField.TEXTAREA_COMPONENT:
                    JComponent textarea = formBuilder.addTextArea(field.getName(), field.getLabel(), "")[index];
                    textarea.setEnabled(field.isEnabled());
            		textarea.addFocusListener(this);
            		textarea.addKeyListener(this);
            		textarea.addPropertyChangeListener(this);
            		((NoTabTextArea) textarea).setText(field.getValue());
            		((NoTabTextArea) textarea).setBorder(new EmptyBorder(1,1,1,1));
            		((NoTabTextArea) textarea).setColumns(1);
            		((NoTabTextArea) textarea).setRows(3);
            		((NoTabTextArea) textarea).setLineWrap(true);
            		if (i == 0) focusField = textarea;
                    rowCount += 1;
                	break;
                case BagInfoField.TEXTFIELD_COMPONENT:
                    JComponent comp = formBuilder.add(field.getName(), field.getLabel(), "")[index];
                    comp.setEnabled(field.isEnabled());
                    comp.addFocusListener(this);
                    comp.addKeyListener(this);
                    comp.addPropertyChangeListener(this);
                    ((JTextField) comp).setText(field.getValue());
            		if (i == 0) focusField = comp;
                	break;
                default:
                }
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
    	//bagView.showWarningErrorDialog("Bag-Info form was updated to prevent data loss.");
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

    	JButton removeButton = new JButton(bagView.getPropertyMessage("bag.button.field.remove"));
    	removeButton.addActionListener(new RemoveFieldHandler());
    	removeButton.setOpaque(true);
    	removeButton.setToolTipText(bagView.getPropertyMessage("bag.button.field.remove.help"));
    	removeButton.setEnabled(enabled);
    	buttonPanel.add(removeButton);

    	return buttonPanel;
    }
    
    private class AddFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
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
    		bagInfo.createStandardFieldList(true);
    		updateForm();
            bagView.infoInputPane.updateInfoFormsPane(true);
            bag.setInfo(bagInfo);
            bagView.setBag(bag);
       	}
    }

    private class RemoveFieldHandler extends AbstractAction {
       	private static final long serialVersionUID = 1L;

    	public void actionPerformed(ActionEvent e) {
    		String key = "";
            java.awt.Component[] components = form.getComponents();
            for (int i=0; i<components.length; i++) {
            	java.awt.Component c;
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
            			if (field != null) fieldList.remove(field);
            		}
            	}
            }
            DefaultBagInfo info = defaultBag.getInfo();
            info.setFieldList(fieldList);
            defaultBag.setInfo(info);
            bagView.setBag(defaultBag);
            bagView.infoInputPane.updateInfoFormsPane(true);
       	}
    }

    private BagInfoField getField(String key) {
    	BagInfoField field = null;
    	List<BagInfoField> list = fieldList;
    	for (int i=0; i < list.size(); i++) {
    		BagInfoField b = list.get(i);
    		if (b.getLabel().equalsIgnoreCase(key)) {
    			return b;
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
