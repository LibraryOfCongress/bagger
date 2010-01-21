
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
    public HashMap<String, Profile> userProfiles = new HashMap<String, Profile>();
    public HashMap<String, List<ProjectProfile>> userProjectProfiles = new HashMap<String, List<ProjectProfile>>();
    public HashMap<String, BaggerProfile> baggerProfile = new HashMap<String, BaggerProfile>();
    public ProjectBagInfo projectBagInfo = new ProjectBagInfo();
    public String username;
    public Contact projectContact;
	BagView bagView;
	DefaultBag bag;

	public BagProject(BagView bagView) {
		super();
		this.bagView = bagView;
	}

    public void setBaggerProfile(BaggerProfile profile, Project project) {
    	this.baggerProfile.put(project.getName(), profile);
    }

    public BaggerProfile getBaggerProfile(Project project) {
    	return this.baggerProfile.get(project.getName());
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
    	projectProfile.setFieldType(BagInfoField.TEXTFIELD_CODE);
    	projectProfile.setIsValueRequired(true);
    	addProjectProfile(project, projectProfile);
    	BaggerProfile bProfile = baggerProfile.get(project.getName());
   		if (bProfile == null) bProfile = new BaggerProfile();
		bProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
		baggerProfile.put(project.getName(), bProfile);
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
		    	BaggerProfile bProfile = baggerProfile.get(project.getName());
           		if (bProfile == null) bProfile = new BaggerProfile();
		    	bProfile.removeField(projectProfile.getFieldName());
		    	baggerProfile.put(project.getName(), bProfile);
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
    	    	if (field.getComponentType() == BagInfoField.TEXTFIELD_COMPONENT) {
    	    		projectProfile.setFieldType(BagInfoField.TEXTFIELD_CODE);
    	    	} else if (field.getComponentType() == BagInfoField.TEXTAREA_COMPONENT) {
    	    		projectProfile.setFieldType(BagInfoField.TEXTAREA_CODE);
    	    	} else if (field.getComponentType() == BagInfoField.LIST_COMPONENT) {
    	    		projectProfile.setFieldType(BagInfoField.LIST_CODE);
    	    	}
    	    	projectProfile.setElements(field.concatElements());
    	    	addProjectProfile(project, projectProfile);
    	    	BaggerProfile bProfile = baggerProfile.get(project.getName());
           		if (bProfile == null) bProfile = new BaggerProfile();
    			bProfile.addField(projectProfile.getFieldName(), projectProfile.getFieldValue(), projectProfile.getIsRequired(), !projectProfile.getIsValueRequired(), false);
    			baggerProfile.put(project.getName(), bProfile);
    		}
    	}
    }

    public void setProfiles(Profile profile, Project project) {
    	this.userProfiles.put(project.getName(), profile);
    }

    public Profile getProfile(Project project) {
    	return this.userProfiles.get(project.getName());
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
   		userProjectProfiles = new HashMap<String, List<ProjectProfile>>();
		Object[] reqs = bag.getInfo().getRequiredStrings();
		for (Iterator<ProjectProfile> iter = projectProfileMap.iterator(); iter.hasNext();) {
			ProjectProfile projectProfile = (ProjectProfile) iter.next();
			Project proj = bagView.getBagger().loadProject(projectProfile.getProjectId());
	    	addProjectProfile(proj, projectProfile);
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
        	Object[] profileArray = profiles.toArray();
    		for (Iterator<String> it = projectKeys.iterator(); it.hasNext();) {
    			String pkey = (String) it.next();
    			Project project = (Project) userProjects.get(pkey);
        		boolean found = false;
            	for (int i=0; i < profileArray.length; i++) {
            		Profile profile = (Profile) profileArray[i];
            		if (project.getId() == profile.getProject().getId()) {
            			found = true;
                    	userProfiles.put(project.getName(), profile);
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
                       		BaggerProfile bProfile = baggerProfile.get(project.getName());
                       		if (bProfile == null) bProfile = new BaggerProfile();
                       		bProfile.setOrganization(bagOrg);
                       		bProfile.setSourceContact(profile.getContact());
                       		bProfile.setToContact(projectContact);
                       		baggerProfile.put(project.getName(), bProfile);
                   		}
            		}
            	}
            	if (!found) {
            		Profile prof = createProfile(project);
            		userProfiles.put(project.getName(), prof);
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
        				Profile prof = createProfile(proj);
        				userProfiles.put(proj.getName(), prof);
        			}
            	}
        	}
    	} else {
    		username = bagView.getPropertyMessage("user.name");
    		projectContact = new Contact();
    		Organization org = new Organization();
    		projectContact.setOrganization(org);
    		userProfiles = new HashMap<String, Profile>();
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
				Profile prof = createProfile(project);
				userProfiles.put(project.getName(), prof);
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
    	HashMap<String, Profile> newProfiles = new HashMap<String, Profile>();
		Set<String> pkeys = userProfiles.keySet();
		for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
    		Profile profile = userProfiles.get(key);
    		Contact person = new Contact();
    		person.setOrganization(new Organization());
    		profile.setPerson(person);
    		Contact contact = new Contact();
    		contact.setOrganization(new Organization());
    		profile.setContact(contact);
    		newProfiles.put(key, profile);
    		if (key.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
    			DefaultBagInfo bagInfo = bag.getInfo();
    	   		BaggerOrganization bagOrg = new BaggerOrganization();
    	   		bagOrg.setContact(contact);
    	   		bagInfo.setBagOrganization(bagOrg);
    	   		bag.setInfo(bagInfo);
    	   		projectContact = profile.getPerson();
    	   		Project proj = bag.getProject();
    			String projName = "";
    	        if (proj == null) {
    	        	projName = bagView.getPropertyMessage("bag.project.noproject");
    	        	bagView.getBag().setProject(userProjects.get(projName));
    	        } else {
    	        	projName = proj.getName();
    	        }
    	   		BaggerProfile bProfile = baggerProfile.get(projName);
           		if (bProfile == null) bProfile = new BaggerProfile();
    	   		bProfile.setOrganization(bagOrg);
    	   		bProfile.setSourceContact(profile.getContact());
    	   		bProfile.setToContact(projectContact);
    	   		baggerProfile.put(projName, bProfile);
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

    		Collection<Profile> profiles = new ArrayList<Profile>();
			Set<String> ukeys = userProfiles.keySet();
			for (Iterator<String> iter = ukeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Profile profile = userProfiles.get(key);
				profiles.add(profile);
			}
			
    		Collection<ProjectProfile> projectProfiles = new ArrayList<ProjectProfile>();
			Set<String> keys = userProjectProfiles.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String label = (String) iter.next();
				List<ProjectProfile> list = userProjectProfiles.get(label);
				for (int i=0; i < list.size(); i++) {
					ProjectProfile projProfile = list.get(i);
					projectProfiles.add(projProfile);
				}
			}

			Collection<Project> projects = new ArrayList<Project>();
			Set<String> pkeys = userProjects.keySet();
			for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Project project = userProjects.get(key);
				projects.add(project);
				// Store bagger profile for each project
	        	//Project bagProject = bag.getProject();
	        	if (project == null) project = new Project();
	    		projectBagInfo.setProjectId(project.getId());
	    		String defaults = "";
	    		BaggerProfile bProfile = baggerProfile.get(project.getName());
	       		if (bProfile == null) bProfile = new BaggerProfile();
	    		HashMap<String, BagInfoField> fieldMap = bProfile.getProfileMap();
	    		if (fieldMap != null && !fieldMap.isEmpty()) {
	    			Set<String> bkeys = fieldMap.keySet();
	    			for (Iterator<String> biter = bkeys.iterator(); biter.hasNext();) {
	    				String bkey = (String) biter.next();
	    				BagInfoField val = fieldMap.get(bkey);
	    				defaults += bkey + "=" + val;
	    				if (biter.hasNext()) defaults += ", ";
	    			}
	            }
	    		//
			}
			String messages = bagView.getBagger().storeBaggerUpdates(profiles, projects, projectProfiles, projectBagInfo, bagView.userHomeDir);
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
    	Profile profile = userProfiles.get(project.getName());
    	if (profile == null) profile = new Profile();
    	BaggerOrganization org = bag.getInfo().getBagOrganization();
    	Contact orgContact = bag.getInfo().getBagOrganization().getContact();
    	orgContact.getOrganization().setName(org.getSourceOrganization());
    	orgContact.getOrganization().setAddress(org.getOrganizationAddress());
    	profile.setContact(orgContact);
    	profile.setContactId(orgContact.getId());
    	profile.setProject(project);
    	profile.setProjectId(project.getId());

    	//TODO
//    	profile.setPerson(this.projectContact);
//    	profile.setUsername(this.username);

    	Contact toContact = new Contact();
    	toContact.setContactName(bag.getInfo().getToContactName());
    	toContact.setTelephone(bag.getInfo().getToContactPhone());
    	toContact.setEmail(bag.getInfo().getToContactEmail());
    	profile.setPerson(toContact);
    	profile.setUsername(toContact.getContactName());
    	
    	userProfiles.put(project.getName(), profile);
    	message = bagView.getPropertyMessage("profile.message.changed") + " " + project.getName() + "\n";
    	return message;
    }

    public void addProjectProfile(Project project, ProjectProfile projectProfile) {
    	List<ProjectProfile> list = userProjectProfiles.get(project.getName());
    	if (list == null) list = new ArrayList<ProjectProfile>();
    	list.add(projectProfile);
    	userProjectProfiles.put(project.getName(), list);
    }
}
