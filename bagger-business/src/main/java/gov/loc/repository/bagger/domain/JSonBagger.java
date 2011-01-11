
package gov.loc.repository.bagger.domain;

import gov.loc.repository.bagger.Bagger;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.json.JSONException;
import gov.loc.repository.bagger.json.JSONObject;
import gov.loc.repository.bagger.json.JSONTokener;
import gov.loc.repository.bagger.json.JSONWriter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Provides JSONBagger business object.
 *
 * <P>
 *
 * Leverages saving and loading profiles in JSON format.
 *
 * @author Praveen Bokka
 */
public class JSonBagger implements Bagger {

    private final Log logger = LogFactory.getLog(getClass());
	private File profilesFolder;
    
    public JSonBagger()
    {
    	String homeDir = System.getProperty("user.home");
    	String profilesPath = homeDir+File.separator+"bagger";
    	profilesFolder = new File(profilesPath);
    	String baggerJarPath = this.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
    	copyDefautprofilesToUserFolder(baggerJarPath, profilesFolder);
    }
    
  
    public void copyDefautprofilesToUserFolder(String baggerJarPath, File profilesFolder)
	{
    	if(!profilesFolder.exists())
    	{
    		profilesFolder.mkdirs();
    	}

    	if(baggerJarPath != null && !baggerJarPath.endsWith(".jar")){

    		String name = new String("gov.loc.repository.bagger.profiles");
    		if (!name.startsWith("/")) {
    		name = "/" + name;
    		}
    		name = name.replace('.','/');



    		// Get a File object for the package
    		URL url = JSonBagger.class.getResource(name);
    		File directory = new File(url.getFile());
    		// New code
    		// ======
    		if (directory.exists()) {
    			// Get the list of the files contained in the package
    			File [] files = directory.listFiles();
    			for(File file : files)
    			{
    				try {
    					FileInputStream fileInputStream = new FileInputStream(file);

    					String entryName = file.getName();
    					String fileName = entryName.substring(entryName.lastIndexOf("/")+1, entryName.length());
    					File outFile = new File(profilesFolder +File.separator+ fileName);
    					FileOutputStream os = new FileOutputStream(outFile);
    					int content = fileInputStream.read();
    					while(content != -1)
    					{
    						os.write(content);
    						content = fileInputStream.read();
    					}
    					os.flush();
    					os.close();
    					



    				} catch (FileNotFoundException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				} catch (IOException e) {
    					// TODO Auto-generated catch block
    					e.printStackTrace();
    				}

    			}
    			return;

    		}
    	}
    	else
    	{
    		try {
    			baggerJarPath = URLDecoder.decode(baggerJarPath, "UTF-8");
    			java.util.jar.JarFile jf = new java.util.jar.JarFile(baggerJarPath);
    			Enumeration<JarEntry> resources = jf.entries();
    			while ( resources.hasMoreElements() ) {
    				java.util.jar.JarEntry je = (java.util.jar.JarEntry) resources.nextElement();
    				if ( je.getName().matches(".*\\.json") ) {
    					try {
    						InputStream is = jf.getInputStream(je);
    						String entryName = je.getName();
    						String fileName = entryName.substring(entryName.lastIndexOf("/")+1, entryName.length());
    						File file = new File(profilesFolder +File.separator+ fileName);
    						FileOutputStream os = new FileOutputStream(file);
    						int content = is.read();
    						while(content != -1)
    						{
    							os.write(content);
    							content = is.read();
    						}
    						os.flush();
    						os.close();
    					} catch (IOException e) {
    						e.printStackTrace();
    					}
    				}
    			}
    		} catch (java.io.IOException e) {
    			e.printStackTrace();
    		}
    	}
	}
    
	public void loadProfile(String profileName) {	
	}

	public List<Profile> loadProfiles() {
		File[] profilesFiles  = profilesFolder.listFiles();
		List<Profile>  profilesToReturn = new ArrayList<Profile>();
		for(File file:profilesFiles)
		{
			try {
				FileReader reader = new FileReader(file);
				Profile profile = loadProfile(reader,file.getName());
				profilesToReturn.add(profile);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		//<no profile>
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
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Profile profile = Profile.createProfile(jsonObject, getprofileName(jsonFileName));
		return profile;
	}
	
	public void saveProfile(Profile profile) {
		if(profile.getName().equals("<no profile>"))
			return;
		try {
			String fileName = getJsonFileName(profile.getName());
			FileWriter writer = new FileWriter(profilesFolder.getAbsolutePath() +File.separator+ fileName);
			StringWriter stringWriter = new StringWriter();
			JSONWriter jsonWriter = new JSONWriter(stringWriter);
			profile.serialize(jsonWriter);
			JSONObject jsonObject = new JSONObject(new JSONTokener(stringWriter.toString()));
			writer.write(jsonObject.toString(4));
			writer.flush();
			writer.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
    /**
     * Returns Profile Name from a JSON File Name.
     * @param jsonFileName A JSON file name
     */
	private String getprofileName(String jsonFileName)
	{
		return jsonFileName.substring(0, jsonFileName.indexOf("-profile.json"));
	}		
	
	private String getJsonFileName(String name)
	{
		return name+"-profile.json";
	}
	
	public void removeProfile(Profile profile) {
		String homeDir = System.getProperty("user.home");
    	String profilesPath = homeDir+File.separator+"bagger";
    	profilesFolder = new File(profilesPath);
    	String profileFielName = getJsonFileName(profile.getName());
    	File file = new File(profilesFolder,profileFielName);
    	if(file.exists())
    		file.delete();
	}
	
}