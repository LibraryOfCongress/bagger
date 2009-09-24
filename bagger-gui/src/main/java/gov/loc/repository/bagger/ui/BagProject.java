
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
	public HashMap<String, Project> userProjects = new HashMap<String, Project>();
    public Collection<Profile> userProfiles;
    public HashMap<String, ProjectProfile> userProjectProfiles = new HashMap<String, ProjectProfile>();
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
    	if (userProjects.containsKey(project.getName())) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public void addProject(Project project) {
    	if (project == null) return;
    	bag = bagView.getBag();
    	userProjects.put(project.getName(), project);

    	bagView.infoInputPane.projectList.addItem(project.getName());
    	bagView.infoInputPane.projectList.invalidate();
    	this.updateProject(project.getName());
    	bagView.getBagger().storeProject(project);
    	bag.setProject(project);
    	bag.getInfo().setLcProject(project.getName());
    	bagView.setBag(bag);
    	ProjectProfile projectProfile = new ProjectProfile();
    	projectProfile.setProjectId(project.getId());
    	projectProfile.setFieldName(DefaultBagInfo.FIELD_LC_PROJECT);
    	projectProfile.setFieldValue(bag.getInfo().getLcProject());
    	projectProfile.setIsRequired(true);
    	projectProfile.setIsValueRequired(true);
    	userProjectProfiles.put(project.getName(), projectProfile);
		baggerProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
		bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
		bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
    }
    
    public void removeProject(String name) {
    	bag = bagView.getBag();
    	if (name.trim().equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
    		bagView.showWarningErrorDialog("Project Profile Dialog", "You cannot delete the " + bagView.getPropertyMessage("bag.project.noproject") + " project.");
    		return;
    	}
    	Project project = null;
    	Project noProject = null;
    	try {
    		project = userProjects.get(name.trim());
    		noProject = userProjects.get(bagView.getPropertyMessage("bag.project.noproject"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		if (project != null) {
			userProjects.remove(project);
	    	bagView.infoInputPane.projectList.removeItem(project.getName());
			if (noProject != null) {
				bagView.infoInputPane.projectList.setSelectedItem(bagView.getPropertyMessage("bag.project.noproject"));
		    	bag.setProject(noProject);
			}
	    	ProjectProfile projectProfile = null;
			projectProfile = (ProjectProfile) userProjectProfiles.get(project.getName());
	    	if (projectProfile != null) {
		    	userProjectProfiles.remove(projectProfile);
				baggerProfile.removeField(projectProfile.getFieldName());
	    	}
		}
    	bagView.infoInputPane.projectList.invalidate();
    	bagView.setBag(bag);
		bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
		bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
    }

    public void addProjectField(BagInfoField field) {
    	if (field.isRequired() || field.isRequiredvalue() || !field.getValue().trim().isEmpty()) {
    		Project project = bag.getProject();
    		if (project != null) {
        		ProjectProfile projectProfile = new ProjectProfile();
    	    	projectProfile.setProjectId(project.getId());
    	    	projectProfile.setFieldName(field.getLabel());
    	    	projectProfile.setFieldValue(field.getValue());
    	    	projectProfile.setIsRequired(field.isRequired());
    	    	projectProfile.setIsValueRequired(field.isRequiredvalue());
    	    	userProjectProfiles.put(project.getName(), projectProfile);
    			baggerProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
    		}
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
    	Collection<Project> projects = bagView.getBagger().getProjects();
   		userProjects = new HashMap<String, Project>();
   		for (Iterator<Project> iter = projects.iterator(); iter.hasNext();) {
   			Project p = (Project) iter.next();
   			userProjects.put(p.getName(), p);
   		}
   		Collection<ProjectProfile> projectProfileMap = bagView.getBagger().getProjectProfiles();
   		userProjectProfiles = new HashMap<String, ProjectProfile>();
		Object[] reqs = bag.getInfo().getRequiredStrings();
		for (Iterator<ProjectProfile> iter = projectProfileMap.iterator(); iter.hasNext();) {
			ProjectProfile projectProfile = (ProjectProfile) iter.next();
			Project proj = bagView.getBagger().loadProject(projectProfile.getProjectId());
			String projName = proj.getName();
			userProjectProfiles.put(projName, projectProfile);
			if (projectProfile.getIsRequired()) {
				if (!bag.getInfo().getRequiredSet().contains(projectProfile.getFieldName())) {
					List<Object> list = new ArrayList<Object>();
					for (int i=0; i < reqs.length; i++) {list.add(reqs[i]);}
					list.add(projectProfile.getFieldName());
					bag.getInfo().setRequiredStrings(list.toArray());
				}
			}
		}

		Set<String> projectKeys = userProjects.keySet();
    	Project bagProject = bag.getProject();
    	if (bagProject == null) {
    		for (Iterator<String> iter = projectKeys.iterator(); iter.hasNext();) {
    			String key = (String) iter.next();
    			bagProject = userProjects.get(key);
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
        	Collection<Profile> profiles = bagView.getBagger().findProfiles(this.username);
        	if (profiles == null) profiles = new ArrayList<Profile>();
        	userProfiles = profiles;
        	Object[] profileArray = profiles.toArray();
    		for (Iterator<String> it = projectKeys.iterator(); it.hasNext();) {
    			String pkey = (String) it.next();
    			Project project = (Project) userProjects.get(pkey);
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
                	Collection<Project> cp = bagView.getBagger().getProjects();
               		userProjects = new HashMap<String, Project>();
               		for (Iterator<Project> iter = cp.iterator(); iter.hasNext();) {
               			Project pj = (Project) iter.next();
               			userProjects.put(pj.getName(), pj);
               		}
        			Set<String> pkeys = userProjects.keySet();
        			for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
        				String key = (String) iter.next();
        				Project proj = userProjects.get(key);
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
        	Collection<Project> cp = bagView.getBagger().getProjects();
       		userProjects = new HashMap<String, Project>();
       		for (Iterator<Project> iter = cp.iterator(); iter.hasNext();) {
       			Project pj = (Project) iter.next();
       			userProjects.put(pj.getName(), pj);
       		}
			Set<String> pkeys = userProjects.keySet();
			for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Project project = userProjects.get(key);
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
        	String message = bagView.getBagger().loadProfiles();
        	this.username = bagView.getPropertyMessage("user.name");
        	this.initializeProfile();
        	boolean b = true;
			Set<String> pkeys = userProjects.keySet();
			for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Project p = userProjects.get(key);
				String name = p.getName();
        		for (int j=0; j < bagView.infoInputPane.projectList.getModel().getSize(); j++) {
        			String proj = (String) bagView.infoInputPane.projectList.getModel().getElementAt(j);
            		if (name.trim().equalsIgnoreCase(proj.trim())) {
            			b = false;
            			break;
            		}
        		}
        		if (b) { bagView.infoInputPane.projectList.addItem(name);	}
        		b = true;
        	}
        	bagView.infoInputPane.projectList.invalidate();
        	bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
        	bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
        	bagView.infoInputPane.bagInfoInputPane.update(bag);
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
    	bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
    	bagView.infoInputPane.bagInfoInputPane.update(bag);
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
    		Collection<ProjectProfile> projectProfiles = new ArrayList<ProjectProfile>();
			Set<String> keys = userProjectProfiles.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String label = (String) iter.next();
				ProjectProfile projProfile = (ProjectProfile) userProjectProfiles.get(label);
				projectProfiles.add(projProfile);
			}
			Collection<Project> projects = new ArrayList<Project>();
			Set<String> pkeys = userProjects.keySet();
			for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Project pj = userProjects.get(key);
				projects.add(pj);
			}
			String messages = bagView.getBagger().storeBaggerUpdates(userProfiles, projects, projectProfiles, projectBagInfo, bagView.userHomeDir);
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
    	Project bagProject = userProjects.get(projectName);
    	if (bagProject != null && projectName != null && projectName.matches(bagProject.getName())) {
    		bag.setProject(bagProject);
    	}
   		messages += updateProfile();
    	if (projectName.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
    		bagView.infoInputPane.projectList.setSelectedItem(projectName);
    		bag.isNoProject(true);
    	} else {
    		bagView.infoInputPane.projectList.setSelectedItem(projectName);
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
