
package gov.loc.repository.bagger.ui;

import java.awt.Dimension;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
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
import gov.loc.repository.bagger.ProjectProfile;
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
	
	private BagView bagView;
	private DefaultBag defaultBag;
	private BaggerProfile baggerProfile;
	//private ProjectBagInfo projectBagInfo;
    public OrganizationInfoForm bagInfoForm = null;
    private OrganizationProfileForm profileForm = null;
    private HierarchicalFormModel infoFormModel = null;
    private HierarchicalFormModel profileFormModel = null;

    private Dimension dimension = new Dimension(400, 370);

    public BagInfoInputPane(BagView bagView, String username, Contact c, boolean b ) {
    	this.bagView = bagView;
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
    	//projectForm.setEnabled(b);
    	profileForm.setEnabled(b);
    	profileForm.getControl().invalidate();
    	bagInfoForm.setEnabled(b);
    	bagInfoForm.invalidate();
    	this.setEnabled(b);
    	this.invalidate();
    }
    
    // Define the information forms
    public void populateForms(DefaultBag bag, boolean enabled) {
    	defaultBag = bag;
    	DefaultBagInfo bagInfo = bag.getInfo();
        BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
        BaggerProfile profile = bagView.bagProject.getBaggerProfile();
        //projectBagInfo = parentView.bagProject.getProjectBagInfo();
        profile.setOrganization(baggerOrganization);
        profile.setToContact(bagView.bagProject.projectContact);
        baggerProfile = profile;

        Contact orgContact = bagInfo.getBagOrganization().getContact();
        if (orgContact == null) {
        	orgContact = new Contact();
        }

        Contact projectContact = bagView.bagProject.projectContact;
        if (projectContact == null) {
        	projectContact = new Contact();
        }

        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null), bagView, bagInfo.getFieldMap(), enabled);

        //projectFormModel = FormModelHelper.createCompoundFormModel(projectBagInfo);
        //projectForm = new ProjectProfileForm(FormModelHelper.createChildPageFormModel(projectFormModel, null), parentView, profile.getProfileMap(), enabled);
        
        profileFormModel = FormModelHelper.createCompoundFormModel(baggerProfile);
        profileForm = new OrganizationProfileForm(FormModelHelper.createChildPageFormModel(profileFormModel, null), bagView);
        profileFormModel.addPropertyChangeListener(profileForm);

        updateProfileForms(bag);
    }

    // Create a tabbed pane for the information forms and checkbox panel
    public void updateProfileForms(DefaultBag bag) {
        removeAll();
        invalidate();
        setName("Profile");
        bagInfoForm.setToolTipText(bagView.getPropertyMessage("infoinputpane.tab.details.help"));
        addTab(bagView.getPropertyMessage("infoInputPane.tab.details"), bagInfoForm);
        profileForm.getControl().setToolTipText("Profile Form");
        addTab(bagView.getPropertyMessage("infoInputPane.tab.profile"), profileForm.getControl());
    }

    public String verifyForms(DefaultBag bag) {
        String messages = "";

        if (!profileForm.hasErrors()) {
        	profileForm.commit();
        }
        BaggerProfile profile = (BaggerProfile) profileForm.getFormObject();
        BaggerOrganization baggerOrg = profile.getOrganization();
        Person userPerson = profile.getToContact().getPerson();
/*
        if (parentView.username == null || parentView.username.length() == 0) {
    		try {
            	parentView.username = URLEncoder.encode(profile.getToContactName(), "utf-8");
    		}
    		catch(Exception ex) {
    			logger.equals("ERROR BagInfoInputPane.verifyForms username: " + profile.getToContactName() + " exception: " + ex );
    		}
        }
*/
        userPerson.parse(profile.getToContact().getContactName());
        profile.getToContact().setPerson(userPerson);

        Contact orgContact = profile.getSourceContact();
        try {
        	Person contactPerson = orgContact.getPerson();
        	contactPerson.parse(orgContact.getContactName());
        	orgContact.setPerson(contactPerson);
        } catch (Exception e) {
        	logger.error("BagInfoInputPane.verifyForms newContact: " + e.getMessage());
        }
        bagView.bagProject.projectContact = profile.getToContact();
        baggerProfile = profile;
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
    		//JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            //int count = sourceTabbedPane.getTabCount();
            //int selected = sourceTabbedPane.getSelectedIndex();
    	}
    }
    
    public void updateProject(BagView bagView) {
    	this.bagInfoForm.setBagView(bagView);
    	DefaultBag bag = bagView.getBag();
    	Project project = bag.getProject();
    	HashMap<String, BagInfoField> currentMap = bag.getInfo().getFieldMap();
		if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();

		if (bag.isEdeposit()) {
			if (currentMap.containsKey(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE)) {
				currentMap.remove(DefaultBagInfo.FIELD_NDNP_AWARDEE_PHASE);
			}
		} else if (bag.isNdnp()) {
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
		} else {
			if (currentMap.containsKey(DefaultBagInfo.FIELD_LC_PROJECT)) {
				currentMap.remove(DefaultBagInfo.FIELD_LC_PROJECT);
			}
		}

		if (project != null) {
			List<ProjectProfile> list = bagView.bagProject.userProjectProfiles.get(project.getName());
			if (list != null) {
				for (int i=0; i < list.size(); i++) {
					ProjectProfile projectProfile = list.get(i);
					if (projectProfile != null) {
						BagInfoField field = new BagInfoField();
						field.setLabel(projectProfile.getFieldName());
						field.setName(field.getLabel().toLowerCase());
						field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
						field.isEnabled(!projectProfile.getIsValueRequired());
						field.isEditable(!projectProfile.getIsValueRequired());
						field.isRequiredvalue(projectProfile.getIsValueRequired());
						field.isRequired(projectProfile.getIsRequired());
						field.setValue(projectProfile.getFieldValue());
						field.buildElements(projectProfile.getElements());
						if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTFIELD_CODE)) {
							field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
						} else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTAREA_CODE)) {
							field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
						} else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.LIST_CODE)) {
							field.setComponentType(BagInfoField.LIST_COMPONENT);
						}
						logger.debug("add projectProfile: " + field);
						currentMap.put(field.getLabel(), field);
					}
				}
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
        	} else if (c instanceof JComboBox) {
        		JComboBox tb = (JComboBox) c;
        		value = (String) tb.getSelectedItem();
        	}
        	map.put(key, value);
        	i++;
        	c = components[i];
        	if (c instanceof JCheckBox) {
        		//JCheckBox cb = (JCheckBox) c;
        	}
        }
        bagInfoForm.dirty = false;
        bag.createBagInfo(map);
        bag.copyFormToBag();
    }
}