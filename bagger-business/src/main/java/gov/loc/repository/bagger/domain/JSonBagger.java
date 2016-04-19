package gov.loc.repository.bagger.domain;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Profile;

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
  private static final String[] DEFAULT_PROFILES = new String[]{"eDeposit-profile.json", "ndiipp-profile.json", 
      "ndnp-profile.json", "other-project-profile.json", "Digital-Records-Accession-Generic-profile.json", 
      "Digital-Records-Accession-IARA-Indiana-profile.json"};

  public JSonBagger() {
    String homeDir = System.getProperty("user.home");
    if(System.getProperties().containsKey(BAGGER_PROFILES_HOME_PROPERTY)){
      homeDir = System.getProperty(BAGGER_PROFILES_HOME_PROPERTY);
    }
    
    String profilesPath = homeDir + File.separator + "bagger";
    log.info("Using profiles from {}", profilesPath);
    
    profilesFolder = new File(profilesPath);
    copyDefautprofilesToUserFolder(profilesFolder);
  }

  public void copyDefautprofilesToUserFolder(File folder) {
    if (!folder.exists()) {
      boolean madeDirs = folder.mkdirs();
      log.debug("Made directories {} ? {}", folder, madeDirs);
    }
    
    for(String profile : DEFAULT_PROFILES){
      InputStream inputStream = null;
      
      try{
        inputStream = this.getClass().getClassLoader().getResourceAsStream(RESOURCE_DIR + File.separator + profile);
        log.debug("Checking if {} exists", profile);
        File target = new File(folder, profile);
        if(!target.exists()){
          log.debug("Profile {} does not already exist on the filesystem. Copying it from jar", profile);
          Files.copy(inputStream, target.toPath());
        }
      }
      catch(Exception e){
        log.error("Failed to copy profile {}", profile, e);
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
    if(profilesFiles != null){
      for (File file : profilesFiles) {
        try {
          InputStreamReader reader = new InputStreamReader(new FileInputStream(file), "UTF-8");
          Profile profile = loadProfile(reader, file.getName());
          profilesToReturn.add(profile);
        }
        catch (FileNotFoundException e) {
          log.error("Could not find profile file[{}]!", file, e);
        }
        catch (JSONException e) {
          log.error("Error parsing json profile[{}]!", file, e);
        } catch (UnsupportedEncodingException e) {
          log.error("Expected UTF-8 encoded file for {}", file, e);
        }
      }
    }

    // <no profile>
    Profile profile = new Profile();
    profile.setName(Profile.NO_PROFILE_NAME);
    profile.setIsDefault(true);
    profilesToReturn.add(profile);
    return profilesToReturn;
  }

  private Profile loadProfile(Reader reader, String jsonFileName) throws JSONException {
    JSONTokener tokenizer = new JSONTokener(reader);
    JSONObject jsonObject = new JSONObject(tokenizer);
    Profile profile = Profile.createProfile(jsonObject, getprofileName(jsonFileName));
    
    return profile;
  }

  @Override
  public void saveProfile(Profile profile) {
    if (profile.getName().equals("<no profile>")){ return; }
    
    try {
      String fileName = getJsonFileName(profile.getName());
      OutputStreamWriter writer = new OutputStreamWriter(
          new FileOutputStream(profilesFolder.getAbsolutePath() + File.separator + fileName),
          Charset.forName("UTF-8"));
      StringWriter stringWriter = new StringWriter();
      JSONWriter jsonWriter = new JSONWriter(stringWriter);
      profile.serialize(jsonWriter);
      JSONObject jsonObject = new JSONObject(new JSONTokener(stringWriter.toString()));
      writer.write(jsonObject.toString(4));
      writer.flush();
      writer.close();
    }
    catch (IOException e) {
      log.error("Failed to write profile {}", profile.getName(), e);
    }
    catch (JSONException e) {
      log.error("Failed to write JSON", e);
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
    if (file.exists()){
      boolean wasDeleted = file.delete();
      log.debug("File {} was deleted ? {}", file, wasDeleted);
    }
  }

}