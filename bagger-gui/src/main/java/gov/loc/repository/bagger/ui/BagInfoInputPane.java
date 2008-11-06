
package gov.loc.repository.bagger.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.net.URLEncoder;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
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
    private OrganizationInfoForm bagInfoForm = null;
    private OrganizationGeneralForm organizationGeneralForm = null;
    private OrganizationContactForm organizationContactForm = null;
    private OrganizationContactForm userContactForm = null;
	private HierarchicalFormModel organizationFormModel = null;
    private HierarchicalFormModel contactFormModel = null;
    private HierarchicalFormModel userFormModel = null;
    private HierarchicalFormModel infoFormModel = null;

    private String username;
	private Contact user;
	private Color selectedColor = new Color(180, 180, 200);
	private Color unselectedColor = Color.black; //new Color(140, 140, 160);

    public BagInfoInputPane(BaggerBag b, String username, Contact c ) {
    	this.baggerBag = b;
    	this.user = c;
    	this.username = username;
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
                //System.out.println("tabbedPane keyboard: " + selected + " of: " + getComponentCount());
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
    
    // Define the information forms
    public void populateForms(BaggerBag baggerBag) {
    	BagInfo bagInfo = baggerBag.getInfo();
        BagOrganization bagOrganization = bagInfo.getBagOrganization();
        organizationFormModel = FormModelHelper.createCompoundFormModel(bagOrganization);
        organizationGeneralForm = new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(organizationFormModel, null));

        Contact contact = bagInfo.getBagOrganization().getContact();
        if (contact == null) {
        	contact = new Contact();
        }
        contactFormModel = FormModelHelper.createCompoundFormModel(contact);
        organizationContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null));
        
        Contact person = this.user;
        if (person == null) {
        	person = new Contact();
        }
        userFormModel = FormModelHelper.createCompoundFormModel(person);
        userContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(userFormModel, null));

        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null));

        updateProfileForms();
    }

    // Create a tabbed pane for the information forms and checkbox panel
    private void updateProfileForms() {
        this.removeAll();
        this.invalidate();
        this.setName("Profile");
        this.addTab("Information", bagInfoForm.getControl());
        this.addTab("User", userContactForm.getControl());
        this.addTab("Organization", organizationGeneralForm.getControl());
        this.addTab("Contact", organizationContactForm.getControl());

//        HierarchicalFormModel model = bagInfoForm.getFormModel();
//        System.out.println("bagInfoForm isDirty: " + bagInfoForm.isDirty());
//        System.out.println("bagInfoForm model: " + model.toString());
        bagInfoForm.getControl().setForeground(unselectedColor);
        userContactForm.getControl().setForeground(selectedColor);
        organizationGeneralForm.getControl().setForeground(selectedColor);
        organizationContactForm.getControl().setForeground(selectedColor);
    }

    public String verifyForms(BaggerBag baggerBag) {
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
        Person userPerson = user.getPerson();
        userPerson.parse(user.getContactName());
        user.setPerson(userPerson);

        if (!organizationContactForm.hasErrors()) {
            organizationContactForm.commit();            	
        }
        Contact newContact = (Contact)organizationContactForm.getFormObject();
        Person contactPerson = newContact.getPerson();
        contactPerson.parse(newContact.getContactName());
        newContact.setPerson(contactPerson);

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
        org.setAddress(newOrganization.getOrgAddress());
        user.setOrganization(org);
        newContact.setOrganization(org);

        newOrganization.setContact(newContact);
        newInfo.setBagOrganization(newOrganization);
        baggerBag.setInfo(newInfo);
        baggerBag.setName(newInfo.getName());
        if (newInfo.getName() == null || newInfo.getName().isEmpty() || newInfo.getName().equalsIgnoreCase("null")) {
        	baggerBag.setName(baggerBag.getInfo().getName());
        }
        //System.out.println("BagInfoInputPane.baggerBag.getName: " + baggerBag.getName());

        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || userContactForm.hasErrors()) {
        	messages = "Bag Information form errors exist.\n";
        }
        update();
        
        return messages;
    }
    
    public String updateForms(BaggerBag baggerBag) {
        String messages = "";

        messages = verifyForms(baggerBag);
        updateProfileForms();
        update();
        
        return messages;
    }
    
    public void updateSelected() {
        // TODO: Figure out why required field marker still appears after openBag
    	if (bagInfoForm.hasErrors()) {
    		this.setSelectedIndex(0);
    	} else if (organizationGeneralForm.hasErrors()) {
    		this.setSelectedIndex(2);
    	} else if (organizationContactForm.hasErrors()) {
    		this.setSelectedIndex(3);
//    	} else if (userContactForm.hasErrors()) {
//    		this.setSelectedIndex(1);
    	}
    	update();
    }
    
    public boolean hasFormErrors() {
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || userContactForm.hasErrors()) {
        	return true;
        } else {
        	return false;
        }
    }
    
    public void update() {
        java.awt.Component[] components = bagInfoForm.getControl().getComponents();
        for (int i=0; i<components.length; i++) {
        	java.awt.Component c = components[i];
        	c.invalidate();
        	c.repaint();
        }
        bagInfoForm.getControl().invalidate();
        bagInfoForm.getControl().repaint();
        userContactForm.getControl().invalidate();
        organizationGeneralForm.getControl().invalidate();
        organizationContactForm.getControl().invalidate();
    	invalidate();
    	repaint();
    }

}