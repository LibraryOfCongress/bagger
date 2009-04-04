
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

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Person;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerFetch;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagInfoInputPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(BagInfoInputPane.class);
	private BagView parentView;
    private OrganizationInfoForm bagInfoForm = null;
    private OrganizationGeneralForm organizationGeneralForm = null;
    private OrganizationContactForm organizationContactForm = null;
    private OrganizationContactForm userContactForm = null;
    private OrganizationFetchForm fetchForm = null;
	private HierarchicalFormModel organizationFormModel = null;
    private HierarchicalFormModel contactFormModel = null;
    private HierarchicalFormModel userFormModel = null;
    private HierarchicalFormModel infoFormModel = null;
    private HierarchicalFormModel fetchFormModel = null;

    private String username;
	private Contact user;
	private Color errorColor = new Color(200, 100, 100);
	private Color selectedColor = new Color(100, 100, 120);
	private Color unselectedColor = Color.black; //new Color(140, 140, 160);

    public BagInfoInputPane(BagView bagView, String username, Contact c ) {
    	this.parentView = bagView;
    	this.user = c;
    	this.username = username;
    	populateForms(bagView.getBag());
        setPreferredSize(bagInfoForm.getControl().getPreferredSize());
        ChangeListener changeListener = new ChangeListener() {
        	public void stateChanged(ChangeEvent changeEvent) {
        		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
                int count = sourceTabbedPane.getTabCount();
                int selected = sourceTabbedPane.getSelectedIndex();
                for (int i = 0; i < count; ++i) {
                    Color c = (i == selected) ? unselectedColor : selectedColor;
                    switch(i) {
                    case 0:
                    	if (bagInfoForm.hasErrors()) {
                    		c = errorColor;
                    		bagInfoForm.getControl().grabFocus();
                    	}
                    	break;
                    case 1:
                    	if (userContactForm.hasErrors()) {
                    		c = errorColor;
                    		userContactForm.getControl().grabFocus();
                    	}
                    	break;
                    case 2:
                    	if (organizationGeneralForm.hasErrors()) {
                    		c = errorColor;
                    		organizationGeneralForm.getControl().grabFocus();
                    	}
                    	break;
                    case 3:
                    	if (organizationContactForm.hasErrors()) {
                    		c = errorColor;
                    		organizationContactForm.getControl().grabFocus();
                    	}
                    	break;
                    case 4:
                    	if (parentView.getBag().getIsHoley() && fetchForm.hasErrors()) {
                    		c = errorColor;
                    		fetchForm.getControl().grabFocus();
                    	}
                    	break;
                    default:
                    }
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
    
    public void enableForms(DefaultBag bag, boolean b) {
    	bagInfoForm.setEnabled(b);
        organizationGeneralForm.setEnabled(b);
        organizationContactForm.setEnabled(b);
        userContactForm.setEnabled(b);
        if (bag.getIsHoley()) {
            fetchForm.setEnabled(b);
        }
    }
    
    // Define the information forms
    public void populateForms(DefaultBag bag) {
    	DefaultBagInfo bagInfo = bag.getInfo();
        BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
        organizationFormModel = FormModelHelper.createCompoundFormModel(baggerOrganization);
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
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null), this.parentView);

        if (bag.getIsHoley()) {
        	BaggerFetch fetch = bag.getFetch();
            fetchFormModel = FormModelHelper.createCompoundFormModel(fetch);
            fetchForm = new OrganizationFetchForm(FormModelHelper.createChildPageFormModel(fetchFormModel, null));
        }

        updateProfileForms(bag);
    }

    // Create a tabbed pane for the information forms and checkbox panel
    public void updateProfileForms(DefaultBag bag) {
        this.removeAll();
        this.invalidate();
        this.setName("Profile");
        bagInfoForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.details.help"));
        this.addTab(parentView.getPropertyMessage("infoInputPane.tab.details"), bagInfoForm.getControl());
        userContactForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.user.help"));
        this.addTab(parentView.getPropertyMessage("infoInputPane.tab.user"), userContactForm.getControl());
        organizationGeneralForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.organization.help"));
        this.addTab(parentView.getPropertyMessage("infoInputPane.tab.organization"), organizationGeneralForm.getControl());
        organizationContactForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.contact.help"));
        this.addTab(parentView.getPropertyMessage("infoInputPane.tab.contact"), organizationContactForm.getControl()); 
        if (bag.getIsHoley()) {
        	if (fetchForm != null) {
            	fetchForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.fetch.help"));
                this.addTab(parentView.getPropertyMessage("infoInputPane.tab.fetch"), fetchForm.getControl());        		
        	}
        }

    	if (bagInfoForm.hasErrors()) {
            bagInfoForm.getControl().setForeground(errorColor);
    	} else {
            bagInfoForm.getControl().setForeground(unselectedColor);    		
    	}
    	if (userContactForm.hasErrors()) {
            userContactForm.getControl().setForeground(errorColor);    		
    	} else {
            userContactForm.getControl().setForeground(selectedColor);    		
    	}
    	if (organizationGeneralForm.hasErrors()) {
            organizationGeneralForm.getControl().setForeground(errorColor);
    	} else {
            organizationGeneralForm.getControl().setForeground(selectedColor);    		
    	}
    	if (organizationContactForm.hasErrors()) {
            organizationContactForm.getControl().setForeground(errorColor);
    	} else {
            organizationContactForm.getControl().setForeground(selectedColor);    		
    	}
        if (bag.getIsHoley()) {
        	if (fetchForm != null) {
            	if (fetchForm.hasErrors()) {
            		fetchForm.getControl().setForeground(errorColor);
            	} else {
            		fetchForm.getControl().setForeground(selectedColor);
            	}
        	}
        }
    }

    public String verifyForms(DefaultBag bag) {
        String messages = "";

        if (!userContactForm.hasErrors()) {
            userContactForm.commit();
        }
        user = (Contact)userContactForm.getFormObject();
        if (username == null || username.length() == 0) {
    		try {
            	username = URLEncoder.encode(user.getContactName(), "utf-8");
    		}
    		catch(Exception ex) {
    			logger.equals("ERROR BagInfoInputPane.verifyForms username: " + user.getContactName() + " exception: " + ex );
    		}
        }
        Person userPerson = user.getPerson();
        userPerson.parse(user.getContactName());
        user.setPerson(userPerson);

        if (!organizationContactForm.hasErrors()) {
            organizationContactForm.commit();            	
        }
        Contact newContact = (Contact)organizationContactForm.getFormObject();
        try {
        	Person contactPerson = newContact.getPerson();
        	contactPerson.parse(newContact.getContactName());
        	newContact.setPerson(contactPerson);
        } catch (Exception e) {
        	logger.error("BagInfoInputPane.verifyForms newContact: " + e.getMessage());
        }
        if (!bagInfoForm.hasErrors()) {
            bagInfoForm.commit();
        }
        DefaultBagInfo newInfo = (DefaultBagInfo)bagInfoForm.getFormObject();

        if (bag.getIsHoley()) {
        	if (fetchForm != null) {
            	if (!fetchForm.hasErrors()) {
            		fetchForm.commit();            	
            	}
            	BaggerFetch fetch = (BaggerFetch)fetchForm.getFormObject();
            	bag.getFetch().setBaseURL(fetch.getBaseURL());
                bag.updateFetch();
        	}
        }
        
        if (!organizationGeneralForm.hasErrors()) {
            organizationGeneralForm.commit();
        }
        BaggerOrganization newOrganization = (BaggerOrganization)organizationGeneralForm.getFormObject();
        try {
            Organization org = user.getOrganization();
            org.setName(newOrganization.getOrgName());
            org.setAddress(newOrganization.getOrgAddress());
            user.setOrganization(org);
            newContact.setOrganization(org);        	
        } catch (Exception e) {
        	logger.error("BagInfoInputPane.verifyForms newOrganization: " + e.getMessage());        	
        }

        newOrganization.setContact(newContact);
        newInfo.setBagOrganization(newOrganization);
        if (!bagInfoForm.hasErrors()) {
        	bag.setInfo(newInfo);
        }
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || userContactForm.hasErrors() || (bag.getIsHoley() && fetchForm.hasErrors())) {
        	messages = parentView.getPropertyMessage("bag.message.info.error") + "\n";
        }
        
        return messages;
    }
    
    public String updateForms(DefaultBag bag) {
        String messages = "";

        messages = verifyForms(bag);
        updateProfileForms(bag);
        update(bag);
        
        return messages;
    }
    
    public void updateSelected(DefaultBag bag) {
    	if (organizationContactForm.hasErrors()) {
    		this.setSelectedIndex(3);
    		organizationContactForm.getControl().grabFocus();
    	} else if (organizationGeneralForm.hasErrors()) {
    		this.setSelectedIndex(2);
    		organizationGeneralForm.getControl().grabFocus();
    	} else if (bag.getIsHoley() && fetchForm != null && fetchForm.hasErrors()) {
    		this.setSelectedIndex(4);
    		fetchForm.getControl().grabFocus();
    	} else if (bagInfoForm.hasErrors()) {
    		this.setSelectedIndex(0);
    		bagInfoForm.getControl().grabFocus();
    	} else if (userContactForm.hasErrors()) {
    		this.setSelectedIndex(1);
    		userContactForm.getControl().grabFocus();
    	}
/*
    	if (bagInfoForm.hasErrors()) {
    		this.setSelectedIndex(0);
    		bagInfoForm.getControl().grabFocus();
    	} else if (organizationGeneralForm.hasErrors()) {
    		this.setSelectedIndex(2);
    		organizationGeneralForm.getControl().grabFocus();
    	} else if (organizationContactForm.hasErrors()) {
    		this.setSelectedIndex(3);
    		organizationContactForm.getControl().grabFocus();
    	} else if (bag.getIsHoley() && fetchForm.hasErrors()) {
    		this.setSelectedIndex(4);
    		fetchForm.getControl().grabFocus();
    	} else if (userContactForm.hasErrors()) {
    		this.setSelectedIndex(1);
    		userContactForm.getControl().grabFocus();
    	}
 */
    	update(bag);
    }
    
    public boolean hasFormErrors(DefaultBag bag) {
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || userContactForm.hasErrors() || (bag.getIsHoley() && fetchForm.hasErrors())) {
        	return true;
        } else {
        	return false;
        }
    }
    
    public boolean hasValidBagForms(DefaultBag bag) {
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || bagInfoForm.hasErrors() || (bag.getIsHoley() && fetchForm.hasErrors())) {
        	return true;
        } else {
        	return false;
        }
    }

    public void update(DefaultBag bag) {
        java.awt.Component[] components = bagInfoForm.getControl().getComponents();
        for (int i=0; i<components.length; i++) {
        	java.awt.Component c = components[i];
        	c.invalidate();
        	c.repaint();
        }
        bagInfoForm.getControl().invalidate();
        userContactForm.getControl().invalidate();
        organizationGeneralForm.getControl().invalidate();
        organizationContactForm.getControl().invalidate();
        if (bag.getIsHoley()) {
        	if (fetchForm != null) fetchForm.getControl().invalidate();
        }
    	invalidate();
    	repaint();
    }

}