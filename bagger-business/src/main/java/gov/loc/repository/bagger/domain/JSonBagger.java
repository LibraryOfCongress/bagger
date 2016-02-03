package gov.loc.repository.bagger.domain;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.json.JSONException;
import gov.loc.repository.bagger.json.JSONObject;
import gov.loc.repository.bagger.json.JSONTokener;
import gov.loc.repository.bagger.json.JSONWriter;

/**
 * Provides JSONBagger business object.
 *
 * <P>
 *
 * Leverages saving and loading profiles in JSON format.
 *
 */
public class JSonBagger implements Bagger {
  protected static final Logger log = LoggerFactory.getLogger(JSonBagger.class);
  private File profilesFolder;
  
  private static final String BAGGER_PROFILES_HOME_PROPERTY = "BAGGER_PROFILES_HOME";
  private static final String RESOURCE_DIR = "gov/loc/repository/bagger/profiles";
  private static final String[] DEFAULT_PROFILES = new String[]{"eDeposit-profile.json", "ndiipp-profile.json", "ndnp-profile.json", "other-project-profile.json"};

  public JSonBagger() {
    String homeDir = System.getProperty("user.home");
    if(System.getProperties().containsKey(BAGGER_PROFILES_HOME_PROPERTY)){
      homeDir = System.getProperty(BAGGER_PROFILES_HOME_PROPERTY);
    }
    
    String profilesPath = homeDir + File.separator + "bagger";
    log.info("Using profiles from {}", profilesPath);
    
    profilesFolder = new File(profilesPath);
    String baggerJarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    copyDefautprofilesToUserFolder(baggerJarPath, profilesFolder);
  }

  public void copyDefautprofilesToUserFolder(String baggerJarPath, File folder) {
    if (!folder.exists()) {
      folder.mkdirs();
    }
    
    for(String profile : DEFAULT_PROFILES){
      InputStream inputStream = null;
      
      try{
        inputStream = this.getClass().getClassLoader().getResourceAsStream(RESOURCE_DIR + File.separator + profile);
        File target = new File(folder, profile);
        if(!target.exists()){
          Files.copy(inputStream, target.toPath());
        }
      }
      catch(Exception e){
        e.printStackTrace();
        break;
      }
      finally{
        closeStream(inputStream);
      }
      
    }
    
  }
  
  private void closeStream(InputStream stream){
    if(stream != null){
      try {
        stream.close();
      }
      catch (IOException e) {
        e.printStackTrace();
      }
    }
  }

  @Override
  public void loadProfile(String profileName) {
  }

  @Override
  public List<Profile> loadProfiles() {
    File[] profilesFiles = profilesFolder.listFiles();
    List<Profile> profilesToReturn = new ArrayList<Profile>();
    for (File file : profilesFiles) {
      try {
        FileReader reader = new FileReader(file);
        Profile profile = loadProfile(reader, file.getName());
        profilesToReturn.add(profile);
      }
      catch (FileNotFoundException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      catch (JSONException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    // <no profile>
    Profile profile = new Profile();
    profile.setName(Profile.NO_PROFILE_NAME);
    profile.setIsDefault(true);
    profilesToReturn.add(profile);
    return profilesToReturn;
  }

  private Profile loadProfile(FileReader reader, String jsonFileName) throws JSONException {
    JSONTokener tokenizer = new JSONTokener(reader);
    JSONObject jsonObject = null;
    try {
      jsonObject = new JSONObject(tokenizer);
    }
    catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    Profile profile = Profile.createProfile(jsonObject, getprofileName(jsonFileName));
    return profile;
  }

  @Override
  public void saveProfile(Profile profile) {
    if (profile.getName().equals("<no profile>"))
      return;
    try {
      String fileName = getJsonFileName(profile.getName());
      FileWriter writer = new FileWriter(profilesFolder.getAbsolutePath() + File.separator + fileName);
      StringWriter stringWriter = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(stringWriter);
      profile.serialize(jsonWriter);
      JSONObject jsonObject = new JSONObject(new JSONTokener(stringWriter.toString()));
      writer.write(jsonObject.toString(4));
      writer.flush();
      writer.close();
    }
    catch (IOException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    catch (JSONException e) {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
  }

  /**
   * Returns Profile Name from a JSON File Name.
   * 
   * @param jsonFileName
   *          A JSON file name
   */
  private String getprofileName(String jsonFileName) {
    return jsonFileName.substring(0, jsonFileName.indexOf("-profile.json"));
  }

  private String getJsonFileName(String name) {
    return name + "-profile.json";
  }

  @Override
  public void removeProfile(Profile profile) {
    String homeDir = System.getProperty("user.home");
    String profilesPath = homeDir + File.separator + "bagger";
    profilesFolder = new File(profilesPath);
    String profileFielName = getJsonFileName(profile.getName());
    File file = new File(profilesFolder, profileFielName);
    if (file.exists())
      file.delete();
  }

}