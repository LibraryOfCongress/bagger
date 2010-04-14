
package gov.loc.repository.bagger.ui;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.bag.impl.DefaultBag;
import gov.loc.repository.bagger.bag.impl.DefaultBagInfo;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BagProject {
	
    public HashMap<String, Profile> userProfiles = new HashMap<String, Profile>();
    public HashMap<String, List<ProfileField>> userProjectProfiles = new HashMap<String, List<ProfileField>>();
    public String username;
    public Contact projectContact;
	BagView bagView;
	DefaultBag bag;

	public BagProject(BagView bagView) {
		super();
		this.bagView = bagView;
	}

    public boolean ProfileExists(String profileName) {
    	if (userProfiles.containsKey(profileName)) {
    		return true;
    	} else {
    		return false;
    	}
    }

    public void addProfile(Profile profile) {
    	if (profile == null) return;
    	bag = bagView.getBag();
    	userProfiles.put(profile.getName(), profile);
    	bagView.infoInputPane.profileList.addItem(profile.getName());
    	bagView.infoInputPane.profileList.invalidate();
    	this.updateProfile(profile.getName());
    	bagView.getBagger().saveProfile(profile);
    	bag.setProfile(profile);
    	bag.getInfo().setLcProject(profile.getName());
    	bagView.setBag(bag);
    	ProfileField projectProfile = new ProfileField();
    	projectProfile.setFieldName(DefaultBagInfo.FIELD_LC_PROJECT);
    	projectProfile.setFieldValue(bag.getInfo().getLcProject());
    	projectProfile.setIsRequired(true);
    	projectProfile.setFieldType(BagInfoField.TEXTFIELD_CODE);
    	projectProfile.setIsValueRequired(true);
    	addProjectProfile(profile, projectProfile);
		bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
		bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
    }
    
    public void removeProject(String name) {
    	bag = bagView.getBag();
    	if (name.trim().equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
    		bagView.showWarningErrorDialog("Project Profile Dialog", "You cannot delete the " + bagView.getPropertyMessage("bag.project.noproject") + " project.");
    		return;
    	}
    	Profile profile = null;
    	Profile noProfile = null;
    	try {
    		profile = userProfiles.get(name.trim());
    		noProfile = userProfiles.get(bagView.getPropertyMessage("bag.project.noproject"));
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
		if (profile != null) {
			userProfiles.remove(profile);
	    	bagView.infoInputPane.profileList.removeItem(profile.getName());
			if (noProfile != null) {
				bagView.infoInputPane.profileList.setSelectedItem(bagView.getPropertyMessage("bag.project.noproject"));
		    	bag.setProfile(noProfile);
			}
	    	
		    userProjectProfiles.remove(profile.getName());
	    	bagView.getBagger().removeProfile(profile);
		}
		
    	bagView.infoInputPane.profileList.invalidate();
    	bagView.setBag(bag);
		bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
		bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
    }

    public void addProjectField(BagInfoField field) {
    	{
    		Profile project = bag.getProfile();
    		if (project != null) {
        		ProfileField projectProfile = new ProfileField();
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
    	    	projectProfile.setElements(field.getElements());
    	    	addProjectProfile(project, projectProfile);
    	    	project.getCustomFields().put(projectProfile.getFieldName(), projectProfile);
    		}
    	}
    }

    public void setProfiles(Profile profile) {
    	this.userProfiles.put(profile.getName(), profile);
    }

    public Profile getProfile(String name) {
    	return this.userProfiles.get(name);
    }

    public void initializeProfile() {
    	bag = bagView.getBag();
    	Collection<Profile> profiles = bagView.getBagger().loadProfiles();
    	userProfiles = new HashMap<String, Profile>();
    	userProjectProfiles = new HashMap<String, List<ProfileField>>();
    	Object[] reqs = bag.getInfo().getRequiredStrings();
    	
    	for(Profile profile: profiles)
    	{
    		userProfiles.put(profile.getName(), profile);
    		HashMap<String,ProfileField> standardFields =  profile.getStandardFields();
    		HashMap<String,ProfileField> customFields =  profile.getCustomFields();
    		HashMap<String,ProfileField> mergedMap =  new HashMap<String, ProfileField>();
    		mergedMap.putAll(standardFields);
    		mergedMap.putAll(customFields);
    		
    		for (ProfileField profileField : mergedMap.values()) {
    			ProfileField projectProfile = (ProfileField) profileField;
    	    	addProjectProfile(profile, projectProfile);
    			if (projectProfile.getIsRequired()) {
    				if (!bag.getInfo().getRequiredSet().contains(projectProfile.getFieldName())) {
    					List<Object> list = new ArrayList<Object>();
    					for (int i=0; i < reqs.length; i++) {list.add(reqs[i]);}
    					list.add(projectProfile.getFieldName());
    					bag.getInfo().setRequiredStrings(list.toArray());
    				}
    			}
    		}
    	}
    	
		Set<String> profileKeys = userProfiles.keySet();
    	Profile bagProfile = bag.getProfile();
    	if (bagProfile == null) {
    		for (Iterator<String> iter = profileKeys.iterator(); iter.hasNext();) {
    			String key = (String) iter.next();
    			bagProfile = userProfiles.get(key);
        		if (bagProfile.getIsDefault()) {
            		bag.setProfile(bagProfile);
            		break;
        		}
    		}
    	}
    	bagView.setBag(bag);
    }

  

    public String loadProfiles() {
    	bag = bagView.getBag();
    	try {
    		List<Profile> profiles = bagView.getBagger().loadProfiles();
        	this.username = bagView.getPropertyMessage("user.name");
        	this.initializeProfile();
        	boolean b = true;
			Set<String> pkeys = userProfiles.keySet();
			for (Iterator<String> iter = pkeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Profile p = userProfiles.get(key);
				String name = p.getName();
        		for (int j=0; j < bagView.infoInputPane.profileList.getModel().getSize(); j++) {
        			String proj = (String) bagView.infoInputPane.profileList.getModel().getElementAt(j);
            		if (name.trim().equalsIgnoreCase(proj.trim())) {
            			b = false;
            			break;
            		}
        		}
        		if (b) { bagView.infoInputPane.profileList.addItem(name);	}
        		b = true;
        	}
        	bagView.infoInputPane.profileList.invalidate();
        	bagView.infoInputPane.bagInfoInputPane.updateProject(bagView);
        	bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
        	bagView.infoInputPane.bagInfoInputPane.update(bag);
        	bagView.compositePane.updateCompositePaneTabs(bag, "");
        	return "";
    	} catch (Exception e) {
    		bagView.showWarningErrorDialog("Error Dialog", "Error trying to load project defaults:\n" + e.getMessage());
    		return null;
    	}
    }

    public String clearProfiles() {
    	String message = "";
    	bag = bagView.getBag();
    	Profile profile = userProfiles.get(bagView.getPropertyMessage("bag.project.noproject"));
    	bag.setProfile(profile);
    	bagView.infoInputPane.bagInfoInputPane.populateForms(bag, true);
    	bagView.infoInputPane.bagInfoInputPane.update(bag);
    	bagView.compositePane.updateCompositePaneTabs(bag, message);
    	return message;
    }

    public String saveProfiles() {
    	try {
    		bag = bagView.getBag();

    		Profile bagProfile = bag.getProfile();
        	if (bagProfile == null) bagProfile = new Profile();
    		
    		Collection<Profile> profiles = new ArrayList<Profile>();
			Set<String> ukeys = userProfiles.keySet();
			for (Iterator<String> iter = ukeys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				Profile profile = userProfiles.get(key);
				bagView.getBagger().saveProfile(profile);
			}
			
    		String message = bagView.getPropertyMessage("profile.message.saved");
    		bagView.compositePane.updateCompositePaneTabs(bag, message);
    		bagView.showWarningErrorDialog("Project Defaults Stored", message);
    		return message;
    	} catch (Exception e) {
    		bagView.showWarningErrorDialog("Error Dialog", "Error trying to store project defaults:\n" + e.getMessage());
    		return null;
    	}
    }

    public String updateProfile(String profileName) {
    	String messages = "";
    	bag = bagView.getBag();
    	Profile bagProfile = userProfiles.get(profileName);
    	if (bagProfile != null && profileName != null && profileName.matches(bagProfile.getName())) {
    		bag.setProfile(bagProfile);
    	}
   		messages += updateProfile();
    	if (profileName.equalsIgnoreCase(bagView.getPropertyMessage("bag.project.noproject"))) {
    		bagView.infoInputPane.profileList.setSelectedItem(profileName);
    		bag.isNoProject(true);
    	} else {
    		bagView.infoInputPane.profileList.setSelectedItem(profileName);
      		bag.isNoProject(false);
    	}
    	bagView.setBag(bag);
		return messages;
    }

    public String updateProfile() {
    	String message = "";
    	bag = bagView.getBag();
    	Profile bagProfile = bag.getProfile();
    	if (bagProfile == null) return message;
    	Profile profile = userProfiles.get(bagProfile.getName());
    	
    	if (profile == null) profile = new Profile();
    	BaggerOrganization org = bag.getInfo().getBagOrganization();
    	Contact orgContact = bag.getInfo().getBagOrganization().getContact();
    	
    	Organization organization = new Organization();
    	organization.setName(ProfileField.createProfileField(
    			          Organization.FIELD_SOURCE_ORGANIZATION, org.getSourceOrganization())); 
    	organization.setAddress(ProfileField.createProfileField(
		          Organization.FIELD_ORGANIZATION_ADDRESS, org.getOrganizationAddress())); 
    	profile.setOrganization(organization);
    	
    	profile.setSendFromContact(orgContact);
    
    	Contact toContact = new Contact(true);
    	toContact.setContactName(ProfileField.createProfileField(
    			          Contact.FIELD_TO_CONTACT_NAME,bag.getInfo().getToContactName()));
    	toContact.setTelephone(ProfileField.createProfileField(
    			          Contact.FIELD_TO_CONTACT_PHONE,bag.getInfo().getToContactPhone()));
    	toContact.setEmail(ProfileField.createProfileField(
    			          Contact.FIELD_TO_CONTACT_EMAIL,bag.getInfo().getToContactEmail()));
    	
    	
    	userProfiles.put(profile.getName(), profile);
    	message = bagView.getPropertyMessage("profile.message.changed") + " " + profile.getName() + "\n";
    	return message;
    }

    public void addProjectProfile(Profile profile, ProfileField projectProfile) {
    	List<ProfileField> list = userProjectProfiles.get(profile.getName());
    	if (list == null) list = new ArrayList<ProfileField>();
    	list.add(projectProfile);
    	userProjectProfiles.put(profile.getName(), list);
    }
    
    public void removeProjectProfile(Profile profile, String fieldName) {
    	
    	List<ProfileField> list = userProjectProfiles.get(profile.getName());
    	if (list == null) list = new ArrayList<ProfileField>();
    	else
    	{
    		ProfileField fieldToRemove = null;
    		for(ProfileField profileField: list)
    		{
    			if(profileField.getFieldName().equals(fieldName))
    			{
    				fieldToRemove = profileField;
    			}
    		}
    		if(fieldToRemove != null)
    		list.remove(fieldToRemove);
    	}
    	
    	HashMap<String,ProfileField> customFields = profile.getCustomFields();
    	HashMap<String,ProfileField> standardFields = profile.getCustomFields();
    	
    	if(customFields.containsKey(fieldName))
    		customFields.remove(fieldName);
    	else if(standardFields.containsKey(fieldName))
    		standardFields.remove(fieldName);
    	
    	userProjectProfiles.put(profile.getName(), list);
    }
}
