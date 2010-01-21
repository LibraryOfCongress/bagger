
package gov.loc.repository.bagger.ui.handlers;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
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
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.edeposit"))) {
    		bag.isEdeposit(true);
      		bag.isNoProject(false);
    	} else {
    		bag.isEdeposit(false);
    	}
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.ndnp"))) {
    		bag.isNdnp(true);
      		bag.isNoProject(false);
    	} else {
    		bag.isNdnp(false);
    	}
    	//TODO
    	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.wdl"))) {
    		bag.isWdl(true);
      		bag.isNoProject(false);
    	} else {
    		bag.isWdl(false);
    	}
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

        Set<String> projectKeys = bagView.bagProject.userProjects.keySet();
        for (Iterator<String> iter = projectKeys.iterator(); iter.hasNext();) {
        	String key = (String) iter.next();
        	Project project = bagView.bagProject.userProjects.get(key);
        	if (selected != null && !selected.isEmpty() && selected.equalsIgnoreCase(project.getName())) {
        		log.info("bagProject: " + project.getId());
        		bag.setProject(project);
        		Profile profile = bagView.bagProject.userProfiles.get(project.getName());
        		Contact person = profile.getPerson();
        		if (person == null) person = new Contact();
        		DefaultBagInfo bagInfo = bag.getInfo();
        		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
        		Contact contact = profile.getContact();
        		if (contact == null) {
        			contact = new Contact();
        		}
        		bagOrg.setContact(contact);
        		Organization org = contact.getOrganization();
        		if (org == null) org = new Organization();
        		bagOrg.setSourceOrganization(org.getName());
        		bagOrg.setOrganizationAddress(org.getAddress());
        		bagInfo.setBagOrganization(bagOrg);
        		//TODO
        		if (!bag.isWdl()){
        			bagInfo.setToContactName(person.getContactName());
        			bagInfo.setToContactPhone(person.getTelephone());
        			bagInfo.setToContactEmail(person.getEmail());
        		} else {
        			bagInfo.setToContactName(DefaultBagInfo.WDL_TO_CONTACT_NAME);
        			bagInfo.setToContactPhone(DefaultBagInfo.WDL_TO_CONTACT_PHONE);
        			bagInfo.setToContactEmail(DefaultBagInfo.WDL_TO_CONTACT_EMAIL);
        		}
        		
        		bag.setInfo(bagInfo);
        		bagView.bagProject.projectContact = profile.getPerson();
        	}
        }
    }
}
