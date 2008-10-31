
package gov.loc.repository.bagger.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URLEncoder;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.FormModelHelper;

import gov.loc.repository.bagger.*;
import gov.loc.repository.bagger.bag.*;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagInfoInputPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(BagInfoInputPane.class);
    private BaggerBag baggerBag;
    private JPanel bagSettingsPanel;
    private OrganizationInfoForm bagInfoForm = null;
    private OrganizationGeneralForm organizationGeneralForm = null;
    private OrganizationContactForm organizationContactForm = null;
    private OrganizationContactForm userContactForm = null;
	private String username;
	private Contact user;
	private Color selectedColor = new Color(200, 200, 220);
	private Color unselectedColor = Color.black; //new Color(140, 140, 160);

    public BagInfoInputPane(BaggerBag b, String username, Contact c, JPanel bagSettingsPanel ) {
    	this.baggerBag = b;
    	this.user = c;
    	this.username = username;
    	this.bagSettingsPanel = bagSettingsPanel;
    	populateForms(b);
        setPreferredSize(bagInfoForm.getControl().getPreferredSize());
        ChangeListener changeListener = new ChangeListener() {
        	public void stateChanged(ChangeEvent changeEvent) {
        		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int count = sourceTabbedPane.getTabCount();
                int selected = sourceTabbedPane.getSelectedIndex();
                for (int i = 0; i < count; ++i) {
                    Color c = (i == selected) ? unselectedColor : selectedColor;
                    sourceTabbedPane.setBackgroundAt(i, c);
                    sourceTabbedPane.setForegroundAt(i, c);
                }
        	}
        };
        this.addChangeListener(changeListener);
/* */
        InputMap im = this.getInputMap();
        im.put(KeyStroke.getKeyStroke("F2"), "tabNext");
        ActionMap am = this.getActionMap();
        am.put("tabNext", new AbstractAction("tabNext") {
        	private static final long serialVersionUID = 1L;
        	public void actionPerformed(ActionEvent evt) {
                int selected = getSelectedIndex();
                System.out.println("tabbedPane keyboard: " + selected + " of: " + getComponentCount());
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
/* */
    }

    public Dimension getPreferredSize() {
    	return bagInfoForm.getControl().getPreferredSize();
    }
    
    public void setUser(Contact user) {
    	this.user = user;
    }
    
    public Contact getUser() {
    	return this.user;
    }
    
    public void populateForms(BaggerBag baggerBag) {
        // Define the information forms
    	HierarchicalFormModel organizationFormModel;
    	BagInfo bagInfo = baggerBag.getInfo();
        BagOrganization bagOrganization = bagInfo.getBagOrganization();
        organizationFormModel = FormModelHelper.createCompoundFormModel(bagOrganization);
        organizationGeneralForm = new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(organizationFormModel, null));

        HierarchicalFormModel contactFormModel;
        Contact contact = bagInfo.getBagOrganization().getContact();
        if (contact == null) {
        	contact = new Contact();
        	contact.setContactType(new ContactType());
        }
        contactFormModel = FormModelHelper.createCompoundFormModel(contact);
        organizationContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null));
        
        HierarchicalFormModel userFormModel;
        Contact person = this.user;
        if (person == null) {
        	person = new Contact();
        	person.setContactType(new ContactType());
        }
        userFormModel = FormModelHelper.createCompoundFormModel(person);
        userContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(userFormModel, null));

        HierarchicalFormModel infoFormModel;
        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null));

        updateProfileForms();
    }

    private void updateProfileForms() {
        // Create a tabbed pane for the information forms and checkbox panel
        this.removeAll();
        this.setName("Profile");
        this.addTab("Information", bagInfoForm.getControl());
        bagInfoForm.getControl().setForeground(unselectedColor);
        this.addTab("User", userContactForm.getControl());
        userContactForm.getControl().setForeground(selectedColor);
        this.addTab("Organization", organizationGeneralForm.getControl());
        organizationGeneralForm.getControl().setForeground(selectedColor);
        this.addTab("Contact", organizationContactForm.getControl());
        organizationContactForm.getControl().setForeground(selectedColor);
        this.addTab("Settings", bagSettingsPanel);
        bagSettingsPanel.setForeground(selectedColor);
    }

    public String updateForms(BaggerBag baggerBag) {
        String messages = "";

        if (!userContactForm.hasErrors()) {
            userContactForm.commit();
        }
        user = (Contact)userContactForm.getFormObject();
        if (username == null || username.isEmpty()) {
    		try {
            	username = URLEncoder.encode(user.getContactName(), "utf-8");
    		}
    		catch(Exception ex) {
    			logger.equals("ERROR BagView.updateForms username: " + user.getContactName() + " exception: " + ex );
    		}
        }

        if (!organizationContactForm.hasErrors()) {
            organizationContactForm.commit();            	
        }
        Contact newContact = (Contact)organizationContactForm.getFormObject();

        if (!bagInfoForm.hasErrors()) {
            bagInfoForm.commit();            	
        }
        BagInfo newInfo = (BagInfo)bagInfoForm.getFormObject();

        if (!organizationGeneralForm.hasErrors()) {
            organizationGeneralForm.commit();
        }
        BagOrganization newOrganization = (BagOrganization)organizationGeneralForm.getFormObject();
        Organization org = user.getOrganization();
        org.setName(newOrganization.getOrgName());
        Address address = new Address();
        address.setAddress(newOrganization.getOrgAddress());
        org.setAddress(address);
        user.setOrganization(org);

//        bag = getBag();
        newOrganization.setContact(newContact);
        newInfo.setBagOrganization(newOrganization);
        baggerBag.setInfo(newInfo);
//        setBag(bag);

        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || userContactForm.hasErrors()) {
        	messages = "Bag Information form errors exist.\n";
        }
        updateProfileForms();
        update();
        // TODO:
        if (bagInfoForm.hasErrors()) {
        	this.setSelectedIndex(0);
        } else if (organizationGeneralForm.hasErrors()) {
        	this.setSelectedIndex(2);
        } else if (organizationContactForm.hasErrors()) {
        	this.setSelectedIndex(3);
        } else if (userContactForm.hasErrors()) {
        	this.setSelectedIndex(1);
        }
//        messages += updateProfile();
        
        return messages;
    }
    
    public boolean hasFormErrors() {
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || userContactForm.hasErrors()) {
        	return true;
        } else {
        	return false;
        }
    }
    
    public void update() {
        bagInfoForm.getControl().invalidate();
        bagInfoForm.getControl().repaint();
        userContactForm.getControl().invalidate();
        userContactForm.getControl().repaint();
        organizationGeneralForm.getControl().invalidate();
        organizationGeneralForm.getControl().repaint();
        organizationContactForm.getControl().invalidate();
        organizationContactForm.getControl().repaint();
        bagSettingsPanel.invalidate();
        bagSettingsPanel.repaint();
    	invalidate();
    	repaint();
    }
}