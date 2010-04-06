
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.ui.BagView;

import java.awt.event.ActionEvent;
import java.util.Iterator;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JComboBox;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class ProjectListHandler extends AbstractAction {
	private static final Log log = LogFactory.getLog(ProjectListHandler.class);
   	private static final long serialVersionUID = 1L;
	BagView bagView;
	DefaultBag bag;

	public ProjectListHandler(BagView bagView) {
		super();
		this.bagView = bagView;
	}

	public void actionPerformed(ActionEvent e) {
		this.bag = bagView.getBag();

    	JComboBox jlist = (JComboBox)e.getSource();
    	String selected = (String) jlist.getSelectedItem();
    	log.info("BagView.projectList valueChanged: " + selected);
    	
    	if (selected == null || selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
      		bag.isNoProject(true);
    	} else {
    		bag.getInfo().setLcProject(selected);
    		bag.isNoProject(false);
    	}
	    // TODO: if LC-Project field exists then open Project Profile and
	    // add LC-Project to the baggerProfile map or modify it
    	bagView.updateBaggerRules();
    	changeProject(selected);
    	bagView.setBag(bag);
    	bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
        bagView.compositePane.updateCompositePaneTabs(bag, "Project changed.");
	}

    private void changeProject(String selected) {
        bagView.bagProject.updateProfile();

        Set<String> projectKeys = bagView.bagProject.userProfiles.keySet();
        for (Iterator<String> iter = projectKeys.iterator(); iter.hasNext();) {
        	String key = (String) iter.next();
        	Profile profile = bagView.bagProject.userProfiles.get(key);
        	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(profile.getName())) {
        		log.info("bagProject: " + profile.getName());
        		bag.setProfile(profile);
        		Profile bagProfile = bagView.bagProject.userProfiles.get(profile.getName());
        		Contact person = bagProfile.getSendToContact();
        		if (person == null) person = new Contact(true);
        		DefaultBagInfo bagInfo = bag.getInfo();
        		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
        		Contact contact = bagProfile.getSendFromContact();
        		if (contact == null) {
        			contact = new Contact(false);
        		}
        		bagOrg.setContact(contact);
        		Organization org = bagProfile.getOrganization();
        		if (org == null) org = new Organization();
        		bagOrg.setSourceOrganization(org.getName().getFieldValue());
        		bagOrg.setOrganizationAddress(org.getAddress().getFieldValue());
        		bagInfo.setBagOrganization(bagOrg);
        		
        		bagInfo.setToContactName(person.getContactName().getFieldValue());
        		bagInfo.setToContactPhone(person.getTelephone().getFieldValue());
        		bagInfo.setToContactEmail(person.getEmail().getFieldValue());
        		
        		bag.setInfo(bagInfo);
        		bagView.bagProject.projectContact = bagProfile.getSendToContact();
        		break;
        	}
        }
    }
}
