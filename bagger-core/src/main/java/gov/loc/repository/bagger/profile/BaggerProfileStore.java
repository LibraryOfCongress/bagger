
package gov.loc.repository.bagger.profile;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class BaggerProfileStore {
	
	private static BaggerProfileStore instance;
	
	private HashMap<String, Profile> userProfiles = new HashMap<String, Profile>();
	private HashMap<String, List<ProfileField>> profileFieldsMap = new HashMap<String, List<ProfileField>>();
	
	public BaggerProfileStore(Bagger bagger) {
		initializeProfile(bagger);
		instance = this;
	}

    public Profile getProfile(String name) {
    	return this.userProfiles.get(name);
    }

    private void initializeProfile(Bagger bagger) {
    	Collection<Profile> profiles = bagger.loadProfiles();
    	userProfiles = new HashMap<String, Profile>();
    	profileFieldsMap = new HashMap<String, List<ProfileField>>();
    	
    	for(Profile profile: profiles)
    	{
    		userProfiles.put(profile.getName(), profile);
    		HashMap<String,ProfileField> standardFields =  profile.getStandardFields();
    		HashMap<String,ProfileField> customFields =  profile.getCustomFields();
    		HashMap<String,ProfileField> mergedMap =  new HashMap<String, ProfileField>();
    		mergedMap.putAll(standardFields);
    		mergedMap.putAll(customFields);
    		
    		for (ProfileField profileField : mergedMap.values()) {
    	    	List<ProfileField> list = profileFieldsMap.get(profile.getName());
    	    	if (list == null) {
    	    		list = new ArrayList<ProfileField>();
    	    		profileFieldsMap.put(profile.getName(), list);
    	    	}
    	    	list.add(profileField);
    		}
    	}
    }
    
    public Profile getDefaultProfile() {
    	Set<String> profileKeys = userProfiles.keySet();
    	for (Iterator<String> iter = profileKeys.iterator(); iter.hasNext();) {
			String key = (String) iter.next();
			Profile bagProfile = userProfiles.get(key);
    		if (bagProfile.getIsDefault()) {
        		return bagProfile;
    		}
		}
    	return null;
    }
    
    public static BaggerProfileStore getInstance() {
    	return instance;
    }
    
    public String[] getProfileNames() {
    	return userProfiles.keySet().toArray(new String[0]);
    }
    
    public List<ProfileField> getProfileFields(String profileName) {
    	return profileFieldsMap.get(profileName);
    }


}
