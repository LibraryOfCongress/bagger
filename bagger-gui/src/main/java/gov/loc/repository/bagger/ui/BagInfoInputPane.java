
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerProfile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagit.Bag;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.FormModelHelper;

public class BagInfoInputPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(BagInfoInputPane.class);
	
	private BagView bagView;
	private DefaultBag defaultBag;
	private BaggerProfile bagProfile;
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
        		try {
                    int selected = getSelectedIndex();
                    int count = getComponentCount();
                    if (selected >= 0 && selected < count-1) {
                    	setSelectedIndex(selected+1);
                    } else {
                    	setSelectedIndex(0);
                    }
                	invalidate();
                	repaint();
        		} catch (Exception e) {
        		}
        	}
        });
        this.setActionMap(am);
    }

    public Dimension getPreferredSize() {
    	return bagInfoForm.getPreferredSize();
    }
    
    public void enableForms(DefaultBag bag, boolean b) {
    	profileForm.setEnabled(b);
    	profileForm.getControl().invalidate();
    	bagInfoForm.setEnabled(b);
    	bagInfoForm.invalidate();
    	this.setEnabled(b);
    	this.invalidate();
    }
    
    public void populateForms(DefaultBag bag, boolean enabled) {
    	defaultBag = bag;
    	DefaultBagInfo bagInfo = bag.getInfo();
        BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
        Profile profile = bagView.getBag().getProfile();
       
        if (bagProfile == null) {
        	bagProfile = new BaggerProfile();
        }
        
        
    	bagProfile.setOrganization(baggerOrganization);
    	bagProfile.setToContact(profile.getSendToContact());
    	updateBagFields();
        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null), bagView, bagInfo.getFieldMap(), enabled);
     
        profileFormModel = FormModelHelper.createCompoundFormModel(bagProfile);
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

        Profile project = bagView.getBag().getProfile();
        if (!profileForm.hasErrors()) {
        	profileForm.commit();
        }
        BaggerProfile bprofile = (BaggerProfile) profileForm.getFormObject();
        BaggerOrganization baggerOrg = bprofile.getOrganization();
        Contact userPerson = bprofile.getToContact();
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
      

        Contact orgContact = bprofile.getSourceContact();
        try {
        

        	Profile profile = bagView.bagProject.userProfiles.get(project.getName());
        	Organization organization = new Organization();
        	organization.setName(ProfileField.createProfileField(
        			   Organization.FIELD_SOURCE_ORGANIZATION, bprofile.getSourceOrganization()));
        	organization.setName(ProfileField.createProfileField(
     			   Organization.FIELD_ORGANIZATION_ADDRESS, bprofile.getOrganizationAddress()));
        	profile.setOrganization(organization);
            profile.setSendFromContact(orgContact);
            //TODO
            Contact toContact = bprofile.getToContact();
            profile.setSendToContact(toContact);
            
            bagView.bagProject.userProfiles.put(project.getName(), profile);
        } catch (Exception e) {
        	logger.error("BagInfoInputPane.verifyForms newContact: " + e.getMessage());
        }
        bagView.bagProject.projectContact = bprofile.getToContact();
       
        bag.getInfo().setBagOrganization(baggerOrg);
        //TODO
        bag.getInfo().setToContactName(bprofile.getToContactName());
        bag.getInfo().setToContactPhone(bprofile.getToContactPhone());
        bag.getInfo().setToContactEmail(bprofile.getToContactEmail());
        
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
    	try {
    		
        	this.bagInfoForm.setBagView(bagView);
        	DefaultBag bag = updateBagFields();
            bagView.setBag(bag);
            bagView.infoInputPane.updateInfoFormsPane(true);
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    }
    
    public DefaultBag updateBagFields()
    {
    	DefaultBag bag = bagView.getBag();
    	Profile profile = bag.getProfile();
    	HashMap<String, BagInfoField> currentMap = bag.getInfo().getFieldMap();
		if (currentMap == null) currentMap = new HashMap<String, BagInfoField>();
		
		//HashMap<String,ProfileField> standardFields = profile.getStandardFields();
		//currentMap.clear();
		
		if (bag.isNoProject()) {
			if (currentMap.containsKey(DefaultBagInfo.FIELD_LC_PROJECT)) {
				currentMap.remove(DefaultBagInfo.FIELD_LC_PROJECT);
			}
			if (currentMap.containsKey(DefaultBagInfo.FIELD_EXTERNAL_IDENTIFIER)) {
				currentMap.remove(DefaultBagInfo.FIELD_EXTERNAL_IDENTIFIER);
			}
		}

		if (profile != null) {
			
			
			if(!profile.getName().equals(bagView.getPropertyMessage("bag.project.noproject")))
			{
				BagInfoField field = new BagInfoField();
				field.setLabel(DefaultBagInfo.FIELD_LC_PROJECT);
				field.setName(DefaultBagInfo.FIELD_LC_PROJECT);
				field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
				field.isEnabled(false);
				field.isEditable(false);
				field.isRequiredvalue(true);
				field.isRequired(true);
				field.setValue(profile.getName());
				field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
				currentMap.put(field.getLabel(), field);
			}
			
			List<ProfileField> list = bagView.bagProject.userProjectProfiles.get(profile.getName());
			HashMap<String, ProfileField> profileFields = convertToMap(list);
			
			if(currentMap.size()>0)
			{
				for(BagInfoField field: currentMap.values())
				{
					ProfileField projectProfile = profileFields.get(field.getLabel());
					if(projectProfile == null)
					  continue;
					
					field.isEnabled(!projectProfile.isReadOnly());
					field.isEditable(!projectProfile.isReadOnly());
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
				}
			}
			
			
			
			HashMap<String, ProfileField> exclusiveProfileFields = new HashMap<String, ProfileField>();
			exclusiveProfileFields.putAll(profileFields);
			exclusiveProfileFields.keySet().removeAll(currentMap.keySet());
			
			
			if (exclusiveProfileFields.size()>0) {
				for (ProfileField profileField : exclusiveProfileFields.values()) {
					ProfileField projectProfile = profileField;
					if (projectProfile != null) {
						BagInfoField field = new BagInfoField();
						field.setLabel(projectProfile.getFieldName());
						field.setName(field.getLabel());
						field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
						field.isEnabled(!projectProfile.isReadOnly());
						field.isEditable(!projectProfile.isReadOnly());
						field.isRequiredvalue(projectProfile.getIsValueRequired());
						field.isRequired(projectProfile.getIsRequired());
						field.setValue(projectProfile.getFieldValue());
						//field.setValue("");
						if(projectProfile.isReadOnly())
							field.isEnabled(false);
						field.buildElements(projectProfile.getElements());
						if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTFIELD_CODE)) {
							field.setComponentType(BagInfoField.TEXTFIELD_COMPONENT);
						} else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.TEXTAREA_CODE)) {
							field.setComponentType(BagInfoField.TEXTAREA_COMPONENT);
						} else if (projectProfile.getFieldType().equalsIgnoreCase(BagInfoField.LIST_CODE)) {
							field.setComponentType(BagInfoField.LIST_COMPONENT);
						}
						currentMap.put(field.getLabel(), field);
					}
				}
			}
			
			
		}
		bag.getInfo().setFieldMap(currentMap);
		bagView.setBag(bag);
		return bag;
    }
    
    public HashMap<String, ProfileField> convertToMap(List<ProfileField> profileFields)
    {
    	HashMap<String, ProfileField> filedsToReturn = new HashMap<String, ProfileField>();
    	if(profileFields == null)
    		return filedsToReturn;
    	for(ProfileField profileFiled: profileFields)
    	{
    		filedsToReturn.put(profileFiled.getFieldName(),profileFiled);
    	}
    	return filedsToReturn;
    }

    private void createBagInfo(DefaultBag bag) {
    	try {
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
            	Profile profile = bagView.bagProject.userProfiles.get(bag.getProfile().getName());
            	if(profile != null)
            	{
            	  if(profile.getCustomFields().containsKey(key))
            	  {
            		  ProfileField profileField = profile.getCustomFields().get(key);
            		  profileField.setFieldValue(value);
            	  }
            	  else if(profile.getStandardFields().containsKey(key))
            	  {
            		  ProfileField profileField = profile.getStandardFields().get(key);
            		  profileField.setFieldValue(value);
            	  }
            	}
            	
            	i++;
            	c = components[i];
            	if (c instanceof JCheckBox) {
            		//JCheckBox cb = (JCheckBox) c;
            	}
            }
            bagInfoForm.dirty = false;
            bag.createBagInfo(map);
            bag.copyFormToBag();
    	} catch (Exception e) {
    	}
    }
}