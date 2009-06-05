
package gov.loc.repository.bagger.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.net.URLEncoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;
import java.awt.event.ActionEvent;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.springframework.binding.validation.ValidationListener;
import org.springframework.binding.validation.ValidationResults;
import org.springframework.binding.form.HierarchicalFormModel;
import org.springframework.richclient.form.FormModelHelper;

import gov.loc.repository.bagger.Contact;
//import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Person;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerFetch;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagit.BagFile;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BagInfoInputPane extends JTabbedPane {
	private static final long serialVersionUID = 1L;
	private static final Log logger = LogFactory.getLog(BagInfoInputPane.class);
	private static final int BAGINFO_INDEX = 0;
	private static final int ORGCONTACT_INDEX = 1;
	private static final int ORGANIZATION_INDEX = 2;
	private static final int SENDTOCONTACT_INDEX = 3;
	private static final int FORM_INDEX = 4;
	
	private BagView parentView;
    private OrganizationInfoForm bagInfoForm = null;
    private OrganizationGeneralForm organizationGeneralForm = null;
    private OrganizationContactForm organizationContactForm = null;
    private OrganizationContactForm sendToContactForm = null;
    private OrganizationFetchForm fetchForm = null;
	private HierarchicalFormModel organizationFormModel = null;
    private HierarchicalFormModel contactFormModel = null;
    private HierarchicalFormModel sendToFormModel = null;
    private HierarchicalFormModel infoFormModel = null;
    private HierarchicalFormModel fetchFormModel = null;

    private String username;
	private Contact projectContact;
	private Color errorColor = new Color(200, 100, 100);
	private Color selectedColor = new Color(100, 100, 120);
	private Color unselectedColor = Color.black; //new Color(140, 140, 160);
    private Dimension dimension = new Dimension(400, 370);

    public BagInfoInputPane(BagView bagView, String username, Contact c, boolean b ) {
    	this.parentView = bagView;
    	this.projectContact = c;
    	this.username = username;
    	populateForms(bagView.getBag(), b);
//        setPreferredSize(bagInfoForm.getControl().getPreferredSize());
    	setMinimumSize(dimension);
    	setPreferredSize(bagInfoForm.getPreferredSize());
        this.addChangeListener(new BagInfoChangeListener());

        ValidationListener validationListener = new ValidationListener() {
        	public void validationResultsChanged(ValidationResults results) {
        		results.getHasErrors();
        	}
        };
//        bagInfoForm.addValidationListener(validationListener);
        
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
    
    public void setProjectContact(Contact projectContact) {
    	this.projectContact = projectContact;
    }
    
    public Contact getProjectContact() {
    	return this.projectContact;
    }
    
    public void enableForms(DefaultBag bag, boolean b) {
    	bagInfoForm.setEnabled(b);
        organizationGeneralForm.setEnabled(b);
        organizationContactForm.setEnabled(b);
        sendToContactForm.setEnabled(b);
        if (bag.getIsHoley()) {
            fetchForm.setEnabled(b);
        }
    }
    
    // Define the information forms
    public void populateForms(DefaultBag bag, boolean enabled) {
    	DefaultBagInfo bagInfo = bag.getInfo();
        BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();

        Contact orgContact = bagInfo.getBagOrganization().getContact();
        if (orgContact == null) {
        	orgContact = new Contact();
        }

        Contact projectContact = this.projectContact;
        if (projectContact == null) {
        	projectContact = new Contact();
        }

        infoFormModel = FormModelHelper.createCompoundFormModel(bagInfo);
        List<BagInfoField> fieldList = bagInfo.getFieldList();
        bagInfoForm = new OrganizationInfoForm(FormModelHelper.createChildPageFormModel(infoFormModel, null), parentView, fieldList, enabled);
//        infoFormModel.addPropertyChangeListener(bagInfoForm);

        organizationFormModel = FormModelHelper.createCompoundFormModel(baggerOrganization);
        organizationGeneralForm = new OrganizationGeneralForm(FormModelHelper.createChildPageFormModel(organizationFormModel, null), parentView);
        organizationFormModel.addPropertyChangeListener(organizationGeneralForm);

        contactFormModel = FormModelHelper.createCompoundFormModel(orgContact);
        organizationContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(contactFormModel, null), this.parentView);
        contactFormModel.addPropertyChangeListener(organizationContactForm);
        
        sendToFormModel = FormModelHelper.createCompoundFormModel(projectContact);
        sendToContactForm = new OrganizationContactForm(FormModelHelper.createChildPageFormModel(sendToFormModel, null), this.parentView);
        sendToFormModel.addPropertyChangeListener(sendToContactForm);

        if (bag.getIsHoley()) {
        	BaggerFetch fetch = bag.getFetch();
            fetchFormModel = FormModelHelper.createCompoundFormModel(fetch);
            fetchForm = new OrganizationFetchForm(FormModelHelper.createChildPageFormModel(fetchFormModel, null), this.parentView);
        }

        updateProfileForms(bag);
    }

    // Create a tabbed pane for the information forms and checkbox panel
    public void updateProfileForms(DefaultBag bag) {
        removeAll();
        invalidate();
        setName("Profile");
        bagInfoForm.setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.details.help"));
        addTab(parentView.getPropertyMessage("infoInputPane.tab.details"), bagInfoForm);
        organizationContactForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.orgContact.help"));
        addTab(parentView.getPropertyMessage("infoInputPane.tab.orgContact"), organizationContactForm.getControl()); 
        organizationGeneralForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.organization.help"));
        addTab(parentView.getPropertyMessage("infoInputPane.tab.organization"), organizationGeneralForm.getControl());
        sendToContactForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.contact.help"));
        addTab(parentView.getPropertyMessage("infoInputPane.tab.contact"), sendToContactForm.getControl());
        if (bag.getIsHoley()) {
        	if (fetchForm != null) {
            	fetchForm.getControl().setToolTipText(parentView.getPropertyMessage("infoinputpane.tab.fetch.help"));
                addTab(parentView.getPropertyMessage("infoInputPane.tab.fetch"), fetchForm.getControl());        		
        	}
        }
/*
    	if (bagInfoForm.hasErrors()) {
            bagInfoForm.getControl().setForeground(errorColor);
    	} else {
            bagInfoForm.getControl().setForeground(unselectedColor);    		
    	} */
    	if (sendToContactForm.hasErrors()) {
    		sendToContactForm.getControl().setForeground(errorColor);    		
    	} else {
    		sendToContactForm.getControl().setForeground(selectedColor);    		
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

        if (!sendToContactForm.hasErrors()) {
        	sendToContactForm.commit();
        }
        projectContact = (Contact)sendToContactForm.getFormObject();
        if (username == null || username.length() == 0) {
    		try {
            	username = URLEncoder.encode(projectContact.getContactName(), "utf-8");
    		}
    		catch(Exception ex) {
    			logger.equals("ERROR BagInfoInputPane.verifyForms username: " + projectContact.getContactName() + " exception: " + ex );
    		}
        }
        Person userPerson = projectContact.getPerson();
        userPerson.parse(projectContact.getContactName());
        projectContact.setPerson(userPerson);

        if (!organizationContactForm.hasErrors()) {
            organizationContactForm.commit();            	
        }
        Contact orgContact = (Contact)organizationContactForm.getFormObject();
        try {
        	Person contactPerson = orgContact.getPerson();
        	contactPerson.parse(orgContact.getContactName());
        	orgContact.setPerson(contactPerson);
        } catch (Exception e) {
        	logger.error("BagInfoInputPane.verifyForms newContact: " + e.getMessage());
        }
 /*       if (!bagInfoForm.hasErrors()) {
            bagInfoForm.commit();
        } 
        DefaultBagInfo newInfo = (DefaultBagInfo)bagInfoForm.getFormObject();
*/
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
        newOrganization.setContact(orgContact);

        DefaultBagInfo newInfo = createBagInfo(bag);
        bag.getInfo().setBagOrganization(newOrganization);
    	bag.setInfo(newInfo);
/*        if (!bagInfoForm.hasErrors()) {
        	bag.setInfo(newInfo);
        } */
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || sendToContactForm.hasErrors() || (bag.getIsHoley() && fetchForm.hasErrors())) {
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
    	if (bag.getIsHoley() && fetchForm != null && fetchForm.hasErrors()) {
    		this.setSelectedIndex(FORM_INDEX);
    		fetchForm.getControl().grabFocus();
    	} else if (sendToContactForm.hasErrors()) {
    		this.setSelectedIndex(SENDTOCONTACT_INDEX);
    		sendToContactForm.getControl().grabFocus();
    	} else if (organizationGeneralForm.hasErrors()) {
    		this.setSelectedIndex(ORGANIZATION_INDEX);
    		organizationGeneralForm.getControl().grabFocus();
    	} else if (organizationContactForm.hasErrors()) {
    		this.setSelectedIndex(ORGCONTACT_INDEX);
    		organizationContactForm.getControl().grabFocus();
 /*   	} else if (bagInfoForm.hasErrors()) {
    		this.setSelectedIndex(BAGINFO_INDEX);
    		bagInfoForm.getControl().grabFocus(); */
    	} else { 
//    		bagInfoForm.getControl().grabFocus();    		
    	}
    	update(bag);
    }
    
    public boolean hasFormErrors(DefaultBag bag) {
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || sendToContactForm.hasErrors() || (bag.getIsHoley() && fetchForm.hasErrors())) {
        	return true;
        } else {
        	return false;
        }
    }
    
    public boolean hasValidBagForms(DefaultBag bag) {
        if (organizationGeneralForm.hasErrors() || organizationContactForm.hasErrors() || (bag.getIsHoley() && fetchForm.hasErrors())) {
        	return true;
        } else {
        	return false;
        }
    }

    public void update(DefaultBag bag) {
        java.awt.Component[] components = bagInfoForm.getComponents();
        for (int i=0; i<components.length; i++) {
        	java.awt.Component c = components[i];
        	c.invalidate();
        	c.repaint();
        }
        bagInfoForm.invalidate();
        sendToContactForm.getControl().invalidate();
        organizationGeneralForm.getControl().invalidate();
        organizationContactForm.getControl().invalidate();
        if (bag.getIsHoley()) {
        	if (fetchForm != null) fetchForm.getControl().invalidate();
        }
    	invalidate();
    	repaint();
    }

    private class BagInfoChangeListener implements ChangeListener {
    	public void stateChanged(ChangeEvent changeEvent) {
    		JTabbedPane sourceTabbedPane = (JTabbedPane) changeEvent.getSource();
            int count = sourceTabbedPane.getTabCount();
            int selected = sourceTabbedPane.getSelectedIndex();
            for (int i = 0; i < count; ++i) {
                Color c = (i == selected) ? unselectedColor : selectedColor;
                switch(i) {
                case BAGINFO_INDEX:
/*                    	if (bagInfoForm.hasErrors()) {
                		c = errorColor;
                		bagInfoForm.getControl().grabFocus();
                	} else {
                		c = unselectedColor;
                	} */
                	break;
                case ORGCONTACT_INDEX:
                	if (organizationContactForm.hasErrors()) {
                		c = errorColor;
                		organizationContactForm.getControl().grabFocus();
                	} else {
                		c = unselectedColor;
                	}
                	break;
                case ORGANIZATION_INDEX:
                	if (organizationGeneralForm.hasErrors()) {
                		c = errorColor;
                		organizationGeneralForm.getControl().grabFocus();
                	} else {
                		c = unselectedColor;
                	}
                	break;
                case SENDTOCONTACT_INDEX:
                	if (sendToContactForm.hasErrors()) {
                		c = errorColor;
                		sendToContactForm.getControl().grabFocus();
                	} else {
                		c = unselectedColor;
                	}
                	break;
                case FORM_INDEX:
                	if (parentView.getBag().getIsHoley() && fetchForm.hasErrors()) {
                		c = errorColor;
                		fetchForm.getControl().grabFocus();
                	} else {
                		c = unselectedColor;
                	}
                	break;
                default:
                }
                sourceTabbedPane.setBackgroundAt(i, c);
                sourceTabbedPane.setForegroundAt(i, c);
            }
    	}
    }

    private DefaultBagInfo createBagInfo(DefaultBag bag) {
        DefaultBagInfo newInfo = bag.getInfo();
        HashMap<String,String> map = new HashMap<String,String>();
        String key = "";
        String value = "";
        JComponent infoForm = this.bagInfoForm.getForm();
        java.awt.Component[] components = infoForm.getComponents();
        logger.debug("createBagInfo size: " + components.length);
        for (int i=0; i<components.length; i++) {
        	java.awt.Component c;
        	c = components[i];
        	if (c instanceof JLabel) {
            	JLabel label = (JLabel) c;
            	key = label.getText();
        	}
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
        	logger.debug("createBagInfo key: " + key + ", value: " + value);
        	i++;
        	c = components[i];
        	if (c instanceof JCheckBox) {
        		JCheckBox cb = (JCheckBox) c;
        	}
        }
        bag.createBagInfo(map);
        bag.copyBagToForm();
        newInfo = bag.getInfo();
        return newInfo;
    }
}