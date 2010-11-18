
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.net.URLEncoder;
import java.util.HashMap;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.FormModelHelper;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Person;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerProfile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagInfoInputPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(BagInfoInputPane.class);
	
	private BagView parentView;
	private DefaultBag defaultBag;
	private BaggerProfile baggerProfile;
    private OrganizationInfoForm bagInfoForm = null;
    private OrganizationProfileForm profileForm = null;
    private HierarchicalFormModel infoFormModel = null;
    private HierarchicalFormModel profileFormModel = null;

    private Dimension dimension = new Dimension(400, 370);

    public BagInfoInputPane(BagView bagView, String username, Contact c, boolean b ) {
    	this.parentView = bagView;
    	this.defaultBag = bagView.getBag();
    	populateForms(defaultBag, b);
    	setMinimumSize(dimension);
    	setPreferredSize(bagInfoForm.getPreferredSize());
        this.addChangeListener(new BagInfoChangeListener());

        InputMap im = this.getInputMap();
        im.put(KeyStroke.getKeyStroke("F2"), "tabNext");
        ActionMap am = this.getActionMap();
        am.put("tabNext", new AbstractAction("tabNext") {
        	private static final long serialVersionUID = 1L;
        	public void actionPerformed(ActionEvent evt) {
                int selected = getSelectedIndex();
                int count = getComponentCount();
                if (selected >= 0 && selected < count-1) {
                	setSelectedIndex(selected+1);
                } else {
                	setSelectedIndex(0);
                }
            	invalidate();
            	repaint();
        	}
        });
        this.setActionMap(am);
    }

    public Dimension getPreferredSize() {
    	return bagInfoForm.getPreferredSize();
    }
    
    public void enableForms(DefaultBag bag, boolean b) {
    	profileForm.setEnabled(b);
    	bagInfoForm.setEnabled(b);
    }
    
    // Define the information forms
    public void populateForms(DefaultBag bag, boolean enabled) {
    	defaultBag = bag;
    	DefaultBagInfo bagInfo = bag.getInfo();
        BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
        BaggerProfile profile = parentView.getBaggerProfile();
        profile.setOrganization(baggerOrganization);
        profile.setToContact(parentView.projectContact);
        baggerProfile = profile;

        Contact orgContact = bagInfo.getBagOrganization().getContact();
        if (orgContact == null) {
        	orgContact = new Contact();
        }

        Contact projectContact = parentView.projectContact;
        if (projectContact == null) {
        	projectContact = new Contact();
        }

        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null), parentView, bagInfo.getFieldMap(), enabled);
        
        profileFormModel = FormModelHelper.createCompoundFormModel(baggerProfile);
        profileForm = new OrganizationProfileForm(FormModelHelper.createChildPageFormModel(profileFormModel, null), this.parentView);
        profileFormModel.addPropertyChangeListener(profileForm);

        updateProfileForms(bag);
    }

    // Create a tabbed pane for the information forms and checkbox panel
    public void updateProfileForms(DefaultBag bag) {
        removeAll();
        invalidate();
        setName("Profile");
        bagInfoForm.setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.details.help"));
        addTab(parentView.getPropertyMessage("infoInputPane.tab.details"), bagInfoForm);
        profileForm.getControl().setToolTipText("Profile Form");
        addTab(parentView.getPropertyMessage("infoInputPane.tab.profile"), profileForm.getControl());
    }

    public String verifyForms(DefaultBag bag) {
        String messages = "";

        if (!profileForm.hasErrors()) {
        	profileForm.commit();
        }
        BaggerProfile baggerProfile = (BaggerProfile) profileForm.getFormObject();
        BaggerOrganization baggerOrg = baggerProfile.getOrganization();
        Person userPerson = baggerProfile.getToContact().getPerson();
        if (parentView.username == null || parentView.username.length() == 0) {
    		try {
            	parentView.username = URLEncoder.encode(baggerProfile.getToContactName(), "utf-8");
    		}
    		catch(Exception ex) {
    			logger.equals("ERROR BagInfoInputPane.verifyForms username: " + baggerProfile.getToContactName() + " exception: " + ex );
    		}
        }
        userPerson.parse(baggerProfile.getToContact().getContactName());
        baggerProfile.getToContact().setPerson(userPerson);

        Contact orgContact = baggerProfile.getSourceContact();
        try {
        	Person contactPerson = orgContact.getPerson();
        	contactPerson.parse(orgContact.getContactName());
        	orgContact.setPerson(contactPerson);
        } catch (Exception e) {
        	logger.error("BagInfoInputPane.verifyForms newContact: " + e.getMessage());
        }        
        bag.getInfo().setBagOrganization(baggerOrg);
        createBagInfo(bag);

        return messages;
    }
    
    public String updateForms(DefaultBag bag) {
        String messages = "";

        messages = verifyForms(bag);
        updateProfileForms(bag);
        update(bag);
        
        return messages;
    }
    
    public boolean hasFormErrors(DefaultBag bag) {
    	return false;
    }
    
    public void update(DefaultBag bag) {
        java.awt.Component[] components = bagInfoForm.getComponents();
        for (int i=0; i<components.length; i++) {
        	java.awt.Component c = components[i];
        	c.invalidate();
        	c.repaint();
        }
        bagInfoForm.invalidate();
        profileForm.getControl().invalidate();
    	invalidate();
    	repaint();
    }

    private class BagInfoChangeListener implements ChangeListener {
    	public void stateChanged(ChangeEvent changeEvent) {
    		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int count = sourceTabbedPane.getTabCount();
            int selected = sourceTabbedPane.getSelectedIndex();
    	}
    }
    
    public void updateProject(BagView bagView) {
    	// TODO: add project field to bag-info form
    	this.bagInfoForm.setBagView(bagView);
    	DefaultBag bag = bagView.getBag();
    	Project project = bag.getProject();
    	HashMap<String, BagInfoField> currentMap = bag.getInfo().getFieldMap();
		if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();
		if (bag.isEdeposit()) {
			//if (currentMap.isEmpty() || !currentMap.containsKey(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER)) {
				BagInfoField field = new BagInfoField();
				field.setLabel(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER);
				field.setName(field.getLabel().toLowerCase());
				field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
				field.isEnabled(true);
				field.isRequired(true);
				currentMap.put(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER, field);
			//}
			if (currentMap.containsKey(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE)) {
				currentMap.remove(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE);
			}
		} else if (bag.isNdnp()) {
			//if (currentMap.isEmpty() || !currentMap.containsKey(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE)) {
				BagInfoField field = new BagInfoField();
				field.setLabel(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE);
				field.setName(field.getLabel().toLowerCase());
				field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
				field.isEnabled(true);
				field.isRequired(true);
				currentMap.put(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE, field);
			//}
			if (currentMap.containsKey(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER)) {
				currentMap.remove(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER);
			}
		} else {
			if (currentMap.containsKey(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER)) {
				currentMap.remove(DefaultBagInfo.FIELD_EDEPOSIT_PUBLISHER);
			}
			if (currentMap.containsKey(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE)) {
				currentMap.remove(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE);
			}
		}
		if (!bag.isNoProject()) {
			//if (currentMap.isEmpty() || !currentMap.containsKey(DefaultBagInfo.FIELD_LC_PROJECT)) {
				BagInfoField field = new BagInfoField();
				field.setLabel(DefaultBagInfo.FIELD_LC_PROJECT);
				field.setName(field.getLabel().toLowerCase());
				field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
				field.isEnabled(false);
				field.isRequired(true);
				field.setValue(bagView.getBag().getProject().getName());
				currentMap.put(DefaultBagInfo.FIELD_LC_PROJECT, field);
			//}
		} else {
			if (currentMap.containsKey(DefaultBagInfo.FIELD_LC_PROJECT)) {
				currentMap.remove(DefaultBagInfo.FIELD_LC_PROJECT);
			}
		}
		bag.getInfo().setFieldMap(currentMap);
        bagView.setBag(bag);
        bagView.infoInputPane.updateInfoFormsPane(true);
    }

    private void createBagInfo(DefaultBag bag) {
        HashMap<String,String> map = new HashMap<String,String>();
        String key = "";
        String value = "";
        JComponent infoForm = this.bagInfoForm.getForm();
        java.awt.Component[] components = infoForm.getComponents();
        for (int i=0; i<components.length; i++) {
        	java.awt.Component c;
        	c = components[i];
        	if (c instanceof JLabel) {
            	JLabel label = (JLabel) c;
            	key = label.getText();
        	}
        	i++;
        	// Is required component
        	c = components[i];
        	i++;
        	c = components[i];
        	if (c instanceof JTextField) {
        		JTextField tf = (JTextField) c;
            	value = tf.getText();
        	} else if (c instanceof JTextArea) {
        		JTextArea ta = (JTextArea) c;
            	value = ta.getText();
        	}
        	map.put(key, value);
        	i++;
        	c = components[i];
        	if (c instanceof JCheckBox) {
        		JCheckBox cb = (JCheckBox) c;
        	}
        }
        bagInfoForm.dirty = false;
        bag.createBagInfo(map);
        bag.copyFormToBag();
    }
}