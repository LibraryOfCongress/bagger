
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.ProjectBagInfo;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.ProjectProfile;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.BaggerProfile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.acegisecurity.Authentication;
import org.acegisecurity.context.SecurityContextHolder;

public class BagProject {
    public Collection<Project> userProjects;
    public Collection<Profile> userProfiles;
    public Collection<ProjectProfile> userProjectProfiles;
    public BaggerProfile baggerProfile = new BaggerProfile();
    public ProjectBagInfo projectBagInfo = new ProjectBagInfo();
    public String username;
    public Contact projectContact;
	BagView bagView;
	DefaultBag bag;

	public BagProject(BagView bagView) {
		super();
		this.bagView = bagView;
	}

    public void setBaggerProfile(BaggerProfile profile) {
    	this.baggerProfile = profile;
    }

    public BaggerProfile getBaggerProfile() {
    	return this.baggerProfile;
    }

    public void setProjectBagInfo(ProjectBagInfo projBagInfo) {
    	this.projectBagInfo = projBagInfo;
    }

    public ProjectBagInfo getProjectBagInfo() {
    	return this.projectBagInfo;
    }

    public boolean projectExists(Project project) {
    	Collection<Project> projectList = this.userProjects;
		for (Iterator<Project> iter = projectList.iterator(); iter.hasNext();) {
			Project p = (Project) iter.next();
			if (p.getName().equalsIgnoreCase(project.getName())) {
				return true;
			}
		}
    	return false;
    }

    public void addProject(Project project) {
    	bag = bagView.getBag();
    	this.userProjects.add(project);

    	bagView.projectList.addItem(project.getName());
    	bagView.projectList.invalidate();
    	this.updateProject(project.getName());
    	bagView.bagger.storeProject(project);
    	bag.setProject(project);
    	bag.getInfo().setLcProject(project.getName());
    	bagView.setBag(bag);
    	ProjectProfile projectProfile = new ProjectProfile();
    	projectProfile.setProjectId(project.getId());
    	projectProfile.setFieldName(DefaultBagInfo.FIELD_LC_PROJECT);
    	projectProfile.setFieldValue(bag.getInfo().getLcProject());
    	projectProfile.setIsRequired(true);
    	projectProfile.setIsValueRequired(true);
    	userProjectProfiles.add(projectProfile);
		baggerProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
		bagView.bagInfoInputPane.updateProject(bagView);
		bagView.bagInfoInputPane.populateForms(bag, true);
    }

    public void addProjectField(BagInfoField field) {
    	if (field.isRequired() || field.isRequiredvalue() || !field.getValue().trim().isEmpty()) {
    		Project project = bag.getProject();
    		ProjectProfile projectProfile = new ProjectProfile();
	    	projectProfile.setProjectId(project.getId());
	    	projectProfile.setFieldName(field.getLabel());
	    	projectProfile.setFieldValue(field.getValue());
	    	projectProfile.setIsRequired(field.isRequired());
	    	projectProfile.setIsValueRequired(field.isRequiredvalue());
	    	userProjectProfiles.add(projectProfile);
			baggerProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
    	}
    }

    public void setProfiles(Collection<Profile> profiles) {
    	this.userProfiles = profiles;
    }

    public Collection<Profile> getProfiles() {
    	return this.userProfiles;
    }

    public void initializeProfile() {
    	bag = bagView.getBag();
   		userProjects = bagView.bagger.getProjects();
   		userProjectProfiles = bagView.bagger.getProjectProfiles();
    	Collection<ProjectProfile> projectProfileMap = userProjectProfiles;
		Object[] reqs = bag.getInfo().getRequiredStrings();
		for (Iterator<ProjectProfile> iter = projectProfileMap.iterator(); iter.hasNext();) {
			ProjectProfile projectProfile = (ProjectProfile) iter.next();
			if (projectProfile.getIsRequired()) {
				if (!bag.getInfo().getRequiredSet().contains(projectProfile.getFieldName())) {
					List<Object> list = new ArrayList<Object>();
					for (int i=0; i < reqs.length; i++) {list.add(reqs[i]);}
					list.add(projectProfile.getFieldName());
					bag.getInfo().setRequiredStrings(list.toArray());
				}
			}
		}

   		Object[] projectArray = userProjects.toArray();
    	Project bagProject = bag.getProject();
    	if (bagProject == null) {
    		for (int i=0; i < projectArray.length; i++) {
        		bagProject = (Project) projectArray[i];
        		if (bagProject.getIsDefault()) {
            		bag.setProject(bagProject);
            		break;
        		}
    		}
    	}
   		Authentication a = SecurityContextHolder.getContext().getAuthentication();
    	if (a != null) this.username = a.getName();
    	else this.username = bagView.getPropertyMessage("user.name");
    	if (projectContact == null) {
    		projectContact = new Contact();
    		Organization org = new Organization();
    		projectContact.setOrganization(org);
    	}
    	if (this.username != null && this.username.length() > 0) {
        	Collection<Profile> profiles = bagView.bagger.findProfiles(this.username);
        	if (profiles == null) profiles = new ArrayList<Profile>();
        	userProfiles = profiles;
        	Object[] profileArray = profiles.toArray();
        	for (int p=0; p < projectArray.length; p++) {
        		Project project = (Project) projectArray[p];
        		boolean found = false;
            	for (int i=0; i < profileArray.length; i++) {
            		Profile profile = (Profile) profileArray[i];
            		if (project.getId() == profile.getProject().getId()) {
            			found = true;
                   		if (project.getId() == bagProject.getId()) {
                       		DefaultBagInfo bagInfo = bag.getInfo();
                       		BaggerOrganization bagOrg = bagInfo.getBagOrganization();
                       		bagOrg.setContact(profile.getContact());
                       		Organization contactOrg = profile.getContact().getOrganization();
                       		bagOrg.setSourceOrganization(contactOrg.getName());
                       		bagOrg.setOrganizationAddress(contactOrg.getAddress());
                       		bagInfo.setBagOrganization(bagOrg);
                       		bag.setInfo(bagInfo);
                       		projectContact = profile.getPerson();
                       		baggerProfile.setOrganization(bagOrg);
                	   		baggerProfile.setSourceCountact(profile.getContact());
                       		baggerProfile.setToContact(projectContact);
                   		}
            		}
            	}
            	if (!found) {
            		userProfiles.add(createProfile(project));
            	}
            	if (userProjects == null || userProjects.isEmpty()) {
            		userProjects = bagView.bagger.getProjects();
            		Object[] projList = userProjects.toArray();
            		for (int i=0; i < projList.length; i++) {
            			Project proj = (Project) projList[i];
            			userProfiles.add(createProfile(proj));
            		}
            	}
        	}
    	} else {
    		username = bagView.getPropertyMessage("user.name");
    		projectContact = new Contact();
    		Organization org = new Organization();
    		projectContact.setOrganization(org);
    		userProfiles = new ArrayList<Profile>();
    		userProjects = bagView.bagger.getProjects();
    		Object[] projList = userProjects.toArray();
    		for (int i=0; i < projList.length; i++) {
    			Project project = (Project) projList[i];
    			userProfiles.add(createProfile(project));
    		}
    	}
    	bagView.setBag(bag);
    }

    private Profile createProfile(Project project) {
		Profile profile = new Profile();
		profile.setProject(project);
		profile.setProjectId(project.getId());
		profile.setPerson(projectContact);
		profile.setUsername(username);
		Contact contact = new Contact();
		contact.setOrganization(new Organization());
		profile.setContact(contact);
		return profile;
    }

    public String loadProfiles() {
    	bag = bagView.getBag();
    	try {
        	String message = bagView.bagger.loadProfiles();
        	this.username = bagView.getPropertyMessage("user.name");
        	this.initializeProfile();
        	Object[] array = userProjects.toArray();
        	boolean b = true;
        	for (int i=0; i < userProjects.size(); i++) {
        		String name = ((Project)array[i]).getName();
        		for (int j=0; j < bagView.projectList.getModel().getSize(); j++) {
        			String proj = (String) bagView.projectList.getModel().getElementAt(j);
            		if (name.trim().equalsIgnoreCase(proj.trim())) {
            			b = false;
            			break;
            		}
        		}
        		if (b) { bagView.projectList.addItem(name);	}
        		b = true;
        	}
        	bagView.projectList.invalidate();
        	bagView.bagInfoInputPane.updateProject(bagView);
        	bagView.bagInfoInputPane.populateForms(bag, true);
        	bagView.bagInfoInputPane.update(bag);
        	bagView.compositePane.updateCompositePaneTabs(bag, message);
        	return message;
    	} catch (Exception e) {
    		bagView.showWarningErrorDialog("Error Dialog", "Error trying to load project defaults:\n" + e.getMessage());
    		return null;
    	}
    }

    public String clearProfiles() {
    	String message = "";
    	bag = bagView.getBag();
    	ArrayList<Profile> newProfiles = new ArrayList<Profile>();
    	Object[] profiles = userProfiles.toArray();
    	for (int j=0; j < profiles.length; j++) {
    		Profile profile = (Profile) profiles[j];
    		Contact person = new Contact();
    		person.setOrganization(new Organization());
    		profile.setPerson(person);
    		Contact contact = new Contact();
    		contact.setOrganization(new Organization());
    		profile.setContact(contact);
    		newProfiles.add(profile);
    		if (j == 0) {
    			DefaultBagInfo bagInfo = bag.getInfo();
    	   		BaggerOrganization bagOrg = new BaggerOrganization();
    	   		bagOrg.setContact(contact);
    	   		bagInfo.setBagOrganization(bagOrg);
    	   		bag.setInfo(bagInfo);
    	   		projectContact = profile.getPerson();
    	   		baggerProfile.setOrganization(bagOrg);
    	   		baggerProfile.setSourceCountact(profile.getContact());
    	   		baggerProfile.setToContact(projectContact);
    		}
    	}
    	userProfiles = newProfiles;
    	bagView.bagInfoInputPane.populateForms(bag, true);
    	bagView.bagInfoInputPane.update(bag);
    	bagView.compositePane.updateCompositePaneTabs(bag, message);
    	return message;
    }

    public String saveProfiles() {
    	try {
    		bag = bagView.getBag();
        	Project bagProject = bag.getProject();
        	if (bagProject == null) bagProject = new Project();
    		projectBagInfo.setProjectId(bagProject.getId());
    		String defaults = "";
    		HashMap<String, BagInfoField> fieldMap = baggerProfile.getProfileMap();
    		if (fieldMap != null && !fieldMap.isEmpty()) {
    			Set<String> keys = fieldMap.keySet();
    			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
    				String key = (String) iter.next();
    				BagInfoField val = fieldMap.get(key);
    				defaults += key + "=" + val;
    				if (iter.hasNext()) defaults += ", ";
    			}
            }
    		String messages = bagView.bagger.storeBaggerUpdates(userProfiles, userProjects, userProjectProfiles, projectBagInfo, bagView.userHomeDir);
    		if (messages != null) {
    			bagView.showWarningErrorDialog("Error Dialog", "Error trying to store project defaults:\n" + messages);
        	    return null;
    		}

    		String message = bagView.getPropertyMessage("profile.message.saved") + " " + bag.getProject().getName() + "\n";
    		bagView.compositePane.updateCompositePaneTabs(bag, message);
    		bagView.showWarningErrorDialog("Project Defaults Stored", message);
    		return message;
    	} catch (Exception e) {
    		bagView.showWarningErrorDialog("Error Dialog", "Error trying to store project defaults:\n" + e.getMessage());
    		return null;
    	}
    }

    public String updateProject(String projectName) {
    	String messages = "";

    	bag = bagView.getBag();
   		Object[] projectArray = userProjects.toArray();
   		for (int i=0; i < projectArray.length; i++) {
   			Project bagProject = (Project) projectArray[i];
   			if (projectName != null && projectName.matches(bagProject.getName())) {
   				bag.setProject(bagProject);
   			}
   		}
   		messages += updateProfile();
    	if (projectName.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
    		bagView.projectList.setSelectedItem(projectName);
    		bag.isNoProject(true);
    	} else {
    		bagView.projectList.setSelectedItem(projectName);
      		bag.isNoProject(false);
    	}
    	bagView.setBag(bag);
		return messages;
    }

    public String updateProfile() {
    	String message = "";
    	bag = bagView.getBag();
    	Project project = bag.getProject();
    	if (project == null) return message;
    	Object[] profiles = this.userProfiles.toArray();
    	Collection<Profile> profileList = new ArrayList<Profile>();
    	for (int i=0; i < profiles.length; i++) {
    		Profile profile = (Profile) profiles[i];
    		if (profile.getProject().getId() == project.getId()) {
    			BaggerOrganization org = bag.getInfo().getBagOrganization();
    			Contact orgContact = bag.getInfo().getBagOrganization().getContact();
    			orgContact.getOrganization().setName(org.getSourceOrganization());
    			orgContact.getOrganization().setAddress(org.getOrganizationAddress());
    			profile.setContact(orgContact);
    			profile.setContactId(orgContact.getId());
    			profile.setProject(project);
    			profile.setProjectId(project.getId());
    			profile.setPerson(this.projectContact);
    			profile.setUsername(this.username);
    			message = bagView.getPropertyMessage("profile.message.changed") + " " + project.getName() + "\n";
    			profiles[i] = profile;
    		}
    		profileList.add(profile);
    	}
    	userProfiles = profileList;
    	return message;
    }

}
