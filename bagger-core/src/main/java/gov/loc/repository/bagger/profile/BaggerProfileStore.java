package gov.loc.repository.bagger.profile;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.ProfileField;

public class BaggerProfileStore {
  protected static final Logger log = LoggerFactory.getLogger(BaggerProfileStore.class);
  private static BaggerProfileStore instance;

  private HashMap<String, Profile> userProfiles = new HashMap<String, Profile>();
  private LinkedHashMap<String, List<ProfileField>> profileFieldsMap = new LinkedHashMap<String, List<ProfileField>>();

  public BaggerProfileStore(Bagger bagger) {
    initializeProfile(bagger);
    instance = this;
  }

  public Profile getProfile(String name) {
    Profile profile = this.userProfiles.get(name);
    if(profile == null){
      log.error("Could not load profile [{}]! Using default profile instead", name);
      return getDefaultProfile();
    }
    return profile;
  }

  private void initializeProfile(Bagger bagger) {
    Collection<Profile> profiles = bagger.loadProfiles();
    userProfiles = new HashMap<String, Profile>();
    profileFieldsMap = new LinkedHashMap<String, List<ProfileField>>();

    for (Profile profile : profiles) {
      userProfiles.put(profile.getName(), profile);
      LinkedHashMap<String, ProfileField> standardFields = profile.getStandardFields();
      LinkedHashMap<String, ProfileField> customFields = profile.getCustomFields();
      LinkedHashMap<String, ProfileField> mergedMap = new LinkedHashMap<String, ProfileField>();
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
    for(Entry<String, Profile> entry: userProfiles.entrySet()){
      if(entry.getValue().getIsDefault()){
        return entry.getValue();
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
