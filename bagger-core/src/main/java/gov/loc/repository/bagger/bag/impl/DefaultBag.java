package gov.loc.repository.bagger.bag.impl;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.bag.BagInfoField;
import gov.loc.repository.bagger.bag.BaggerFetch;
import gov.loc.repository.bagger.model.BagStatus;
import gov.loc.repository.bagger.model.Status;
import gov.loc.repository.bagger.profile.BaggerProfileStore;
import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.PreBag;
import gov.loc.repository.bagit.BagFactory.Version;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.Manifest.Algorithm;
import gov.loc.repository.bagit.transformer.HolePuncher;
import gov.loc.repository.bagit.transformer.impl.DefaultCompleter;
import gov.loc.repository.bagit.transformer.impl.HolePuncherImpl;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.Verifier;
import gov.loc.repository.bagit.verify.impl.CompleteVerifierImpl;
import gov.loc.repository.bagit.verify.impl.RequiredBagInfoTxtFieldsVerifier;
import gov.loc.repository.bagit.verify.impl.ValidVerifierImpl;
import gov.loc.repository.bagit.writer.Writer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class DefaultBag {
	private static final Log log = LogFactory.getLog(DefaultBag.class);
	public static final long KB = 1024;
	public static final long MB = 1048576;
	public static final long GB = 1073741824;
	public static final long MAX_SIZE = 104857600; // 100 MB
	public static final short NO_MODE = 0;
	public static final short ZIP_MODE = 1;
	public static final short TAR_MODE = 2;
	public static final short TAR_GZ_MODE = 3;
	public static final short TAR_BZ2_MODE = 4;
	public static final String NO_LABEL = "none";
	public static final String ZIP_LABEL = "zip";
	public static final String TAR_LABEL = "tar";
	public static final String GZ_LABEL = "gz";
	public static final String TAR_GZ_LABEL = "tar.gz";
	public static final String TAR_BZ2_LABEL = "tar.bz2";

	// Bag option flags
	private boolean isHoley = false;
	private boolean isSerial = true;
	private boolean isAddKeepFilesToEmptyFolders = false;

	// bag building (saving) options
	private boolean isBuildTagManifest = true;
	private boolean isBuildPayloadManifest = true;
	private String tagManifestAlgorithm;
	private String payloadManifestAlgorithm;
	private short serialMode = NO_MODE;

	// Bag state flags
	private boolean isValidateOnSave = false;
	private boolean isSerialized = false;
	
	private boolean dirty = false;

	private File rootDir = null;
	private String name = new String("bag_");
	private long size;
	private long totalSize = 0;

	private Bag bilBag;
	private DefaultBagInfo bagInfo = null;
	private Verifier bagStrategy;
	private BaggerFetch fetch;
	private Profile profile;
	private String versionString = null;
	private File bagFile = null;

	public DefaultBag() {
		this(null, Version.V0_96.versionString);
	}

	public DefaultBag(File rootDir, String version) {
		this.versionString = version;
		init(rootDir);
	}

	protected void display(String s) {
		log.info(this.getClass().getName() + ": " + s);
	}

	private void init(File rootDir) {
		boolean newBag = rootDir == null;
		resetStatus();
		this.rootDir = rootDir;

		display("DefaultBag.init file: " + rootDir + ", version: "
				+ versionString);
		BagFactory bagFactory = new BagFactory();
		if (!newBag) {
			bilBag = bagFactory.createBag(this.rootDir);
			versionString = bilBag.getVersion().versionString;
		} else if (versionString != null) {
			Version version = Version.valueOfString(versionString);
			bilBag = bagFactory.createBag(version);
		} else {
			bilBag = bagFactory.createBag();
		}
		initializeBilBag();
		
		bagInfo = new DefaultBagInfo(bilBag);
		
		FetchTxt fetchTxt = bilBag.getFetchTxt();
		if (fetchTxt != null && !fetchTxt.isEmpty()) {
			String url = getBaseUrl(fetchTxt);
			if (url != null && !url.isEmpty()) {
				isHoley(true);
				BaggerFetch fetch = this.getFetch();
				fetch.setBaseURL(url);
				this.fetch = fetch;
			} else {
				isHoley(false);
			}
		}
		
		this.payloadManifestAlgorithm = Manifest.Algorithm.MD5.bagItAlgorithm;
		this.tagManifestAlgorithm = Manifest.Algorithm.MD5.bagItAlgorithm;
		
		this.bagInfo.update(bilBag.getBagInfoTxt());
		
		// set profile
		String lcProject = bilBag.getBagInfoTxt().get(DefaultBagInfo.FIELD_LC_PROJECT);
		if (lcProject != null && !lcProject.isEmpty()){
    		Profile profile = BaggerProfileStore.getInstance().getProfile(lcProject);
    		setProfile(profile, newBag);
    	} else {
    		clearProfile();
    	}
	}
	
	private void initializeBilBag() {
		BagInfoTxt bagInfoTxt = bilBag.getBagInfoTxt();
		if (bagInfoTxt == null) {
			bagInfoTxt = bilBag.getBagPartFactory().createBagInfoTxt();
			/* */
			Set<String> keys = bagInfoTxt.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String key = (String) iter.next();
				bagInfoTxt.remove(key);
			}
			/* */
			bilBag.putBagFile(bagInfoTxt);
		}
		
		BagItTxt bagIt = bilBag.getBagItTxt();
		if (bagIt == null) {
			bagIt = bilBag.getBagPartFactory().createBagItTxt();
			bilBag.putBagFile(bagIt);
		}
	}

	public void createPreBag(File data, String version) {
		BagFactory bagFactory = new BagFactory();
		PreBag preBag = bagFactory.createPreBag(data);
		if (version == null) {
			Bag bag = preBag.makeBagInPlace(BagFactory.LATEST, false);
			bilBag = bag;
		} else {
			Bag bag = preBag.makeBagInPlace(Version.valueOfString(version),
					false);
			bilBag = bag;
		}
	}
	
	/*
     * Makes BIL API call to create Bag in place and 
     * adding .keep files in empty Pay load folders 
    */
	public void createPreBagAddKeepFilesToEmptyFolders(File data, String version) {
		BagFactory bagFactory = new BagFactory();
		PreBag preBag = bagFactory.createPreBag(data);
		if (version == null) {
			Bag bag = preBag.makeBagInPlace(BagFactory.LATEST, false, true);
			bilBag = bag;
		} else {
			Bag bag = preBag.makeBagInPlace(Version.valueOfString(version),
					false, true);
			bilBag = bag;
		}
	}	

	public File getBagFile() {
		return bagFile;
	}

	private void setBagFile(File fname) {
		this.bagFile = fname;
	}

	public String getDataDirectory() {
		return bilBag.getBagConstants().getDataDirectory();
	}

	private void resetStatus() {
		isComplete(Status.UNKNOWN);
		isValid(Status.UNKNOWN);
		isValidMetadata(Status.UNKNOWN);
	}

	public void setVersion(String v) {
		this.versionString = v;
	}

	public String getVersion() {
		return this.versionString;
	}

	public void setName(String name) {
		String[] list = name.split("\\.");
		if (list != null && list.length > 0)
			name = list[0];
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setSize(long size) {
		this.size = size;
	}

	public long getSize() {
		return this.size;
	}

	// This directory contains either the bag directory or serialized bag file
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}

	public File getRootDir() {
		return this.rootDir;
	}


	public void isHoley(boolean b) {
		this.isHoley = b;
	}

	public boolean isHoley() {
		return this.isHoley;
	}

	public void isSerial(boolean b) {
		this.isSerial = b;
	}

	public boolean isSerial() {
		return this.isSerial;
	}

	public void setSerialMode(short m) {
		this.serialMode = m;
	}

	public short getSerialMode() {
		return this.serialMode;
	}

	public boolean isNoProject() {
		return profile.isNoProfile();
	}

	public void isBuildTagManifest(boolean b) {
		this.isBuildTagManifest = b;
	}

	public boolean isBuildTagManifest() {
		return this.isBuildTagManifest;
	}

	public void isBuildPayloadManifest(boolean b) {
		this.isBuildPayloadManifest = b;
	}

	public boolean isBuildPayloadManifest() {
		return this.isBuildPayloadManifest;
	}

	public void setTagManifestAlgorithm(String s) {
		this.tagManifestAlgorithm = s;
	}

	public String getTagManifestAlgorithm() {
		return this.tagManifestAlgorithm;
	}

	public void setPayloadManifestAlgorithm(String s) {
		this.payloadManifestAlgorithm = s;
	}

	public String getPayloadManifestAlgorithm() {
		return this.payloadManifestAlgorithm;
	}

	/*
     *  Setter Method 
     *  for the passed value associated with the ".keep Files in Empty Folder(s):" Check Box 
    */	
	public void isAddKeepFilesToEmptyFolders(boolean b) {
		this.isAddKeepFilesToEmptyFolders = b;
	}

	/*
     *  Getter Method 
     *  for the value return value associated with the "Add .keep Files To Empty Folder" Check Box 
    */
	public boolean isAddKeepFilesToEmptyFolders() {
		return this.isAddKeepFilesToEmptyFolders;
	}	
	
	public void isValidateOnSave(boolean b) {
		this.isValidateOnSave = b;
	}

	public boolean isValidateOnSave() {
		return this.isValidateOnSave;
	}

	private void isComplete(Status status) {
		BagStatus.getInstance().getCompletenessStatus().setStatus(status);
	}

	private void isValid(Status status) {
		BagStatus.getInstance().getValidationStatus().setStatus(status);
	}

	private void isValidMetadata(Status status) {
		BagStatus.getInstance().getProfileComplianceStatus().setStatus(status);
	}

	public void isSerialized(boolean b) {
		this.isSerialized = b;
	}

	public boolean isSerialized() {
		return this.isSerialized;
	}

	public void updateBagInfo(Map<String, String> map) {
		changeToDirty();
		isValidMetadata(Status.UNKNOWN);
		bagInfo.update(map);
	}
	

	public DefaultBagInfo getInfo() {
		return this.bagInfo;
	}

	public String getBagInfoContent() {
		String bicontent = new String();
		if (this.bagInfo != null) {
			bicontent = this.bagInfo.toString();
		}
		return bicontent;
	}

	public String getBaseUrl(FetchTxt fetchTxt) {
		String httpToken = "http:";
		String delimToken = "bagit";
		String baseUrl = "";
		try {
			if (fetchTxt != null) {
				if (!fetchTxt.isEmpty()) {
					FilenameSizeUrl fsu = fetchTxt.get(0);
					if (fsu != null) {
						String url = fsu.getUrl();
						baseUrl = url;
						String[] list = url.split(delimToken);
						for (int i = 0; i < list.length; i++) {
							String s = list[i];
							if (s.trim().startsWith(httpToken)) {
								baseUrl = s;
							}
						}
					}
				}
			}
		} catch (Exception e) {
		}
		return baseUrl;
	}

	public void setFetch(BaggerFetch fetch) {
		this.fetch = fetch;
	}

	public BaggerFetch getFetch() {
		if (this.fetch == null)
			this.fetch = new BaggerFetch();
		return this.fetch;
	}

	public List<String> getFetchPayload() {
		List<String> list = new ArrayList<String>();

		FetchTxt fetchTxt = this.bilBag.getFetchTxt();
		if (fetchTxt == null)
			return list;
		if (fetchTxt != null) {
			for (int i = 0; i < fetchTxt.size(); i++) {
				FilenameSizeUrl fetch = fetchTxt.get(i);
				String s = fetch.getFilename();
				display("DefaultBag.getFetchPayload: " + fetch.toString());
				list.add(s);
			}
		}
		return list;
	}

	public String getDataContent() {
		totalSize = 0;
		StringBuffer dcontent = new StringBuffer();
		dcontent.append(this.getDataDirectory() + "/");
		dcontent.append('\n');
		Collection<BagFile> files = this.bilBag.getPayload();
		if (files != null) {
			for (Iterator<BagFile> it = files.iterator(); it.hasNext();) {
				try {
					BagFile bf = it.next();
					if (bf != null) {
						totalSize += bf.getSize();
						dcontent.append(bf.getFilepath());
						dcontent.append('\n');
					}
				} catch (Exception e) {
					log.error("DefaultBag.getDataContent: " + e.getMessage());
				}
			}
		}
		this.setSize(totalSize);
		return dcontent.toString();
	}

	public long getDataSize() {
		return this.totalSize;
	}

	public int getDataNumber() {
		return this.bilBag.getPayload().size();
	}
	
	public void clearProfile() {
		setProfile(BaggerProfileStore.getInstance().getProfile(Profile.NO_PROFILE_NAME), false);
	}

	public void setProfile(Profile profile, boolean newBag) {
		this.profile = profile;
		bagInfo.setProfile(profile, newBag);
	}
	 
	public Profile getProfile() {
		return this.profile;
	}

	public List<String> getPayloadPaths() {
		ArrayList<String> pathList = new ArrayList<String>();
		Collection<BagFile> payload = this.bilBag.getPayload();
		if (payload != null) {
			for (Iterator<BagFile> it = payload.iterator(); it.hasNext();) {
				BagFile bf = it.next();
				pathList.add(bf.getFilepath());
			}
		}
		return pathList;
	}

	public String addTagFile(File f) {
		changeToDirty();
		isComplete(Status.UNKNOWN);
        
		String message = null;
		if (f != null) {
			try {
				bilBag.addFileAsTag(f);
			} catch (Exception e) {
				message = "Error adding file: " + f + " due to: "
						+ e.getMessage();
			}
		}
		return message;
	}


	public String write(Writer bw) {
		prepareBilBagInfoIfDirty();

		generateManifestFiles();
		
		if (this.isHoley) {
			if (this.getFetch().getBaseURL() != null) {
				BagInfoTxt bagInfoTxt = bilBag.getBagInfoTxt();
				
				List<Manifest> manifests = bilBag.getPayloadManifests();
				List<Manifest> tags = bilBag.getTagManifests();

				HolePuncher puncher = new HolePuncherImpl(new BagFactory());
				bilBag = puncher.makeHoley(bilBag,
						this.getFetch().getBaseURL(), true, true, false);
				// makeHoley deletes baginfo so put back
				bilBag.putBagFile(bagInfoTxt);
				if (manifests != null) {
					for (int i = 0; i < manifests.size(); i++) {
						bilBag.putBagFile(manifests.get(i));
					}
				}
				if (tags != null) {
					for (int i = 0; i < tags.size(); i++) {
						bilBag.putBagFile(tags.get(i));
					}
				}
			}
		}
		
		String messages = writeBag(bw);
		
		if (bw.isCancelled()) {
			return "Save cancelled.";
		}
		return messages;
	}

	

	public String completeBag(CompleteVerifierImpl completeVerifier) {
		prepareBilBagInfoIfDirty();
		
		String messages = null;
		SimpleResult result = completeVerifier.verify(bilBag);
		
		if (completeVerifier.isCancelled()) {
			this.isComplete(Status.UNKNOWN);
			return "Completeness check cancelled.";
		} 
		
		if (!result.isSuccess()) {
			messages = "Bag is not complete:\n";
			messages += result.toString();
		}
		this.isComplete(result.isSuccess()? Status.PASS : Status.FAILURE);
		if (!isNoProject()) {
			try {
				String msgs = validateMetadata();
				if (msgs != null) {
					if (messages != null)
						messages += msgs;
					else
						messages = msgs;
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				String msgs = "ERROR validating bag: \n" + ex.getMessage()
						+ "\n";
				if (messages != null)
					messages += msgs;
				else
					messages = msgs;
			}
		}
		return messages;
	}

	public String validateMetadata() {
		prepareBilBagInfoIfDirty();
		
		String messages = null;
		updateStrategy();
		SimpleResult result = this.bilBag.verify(bagStrategy);
		if (result.toString() != null && !result.isSuccess()) {
			messages = "Bag-info fields are not all present for the project selected.\n";
			messages += result.toString();
		}
		this.isValidMetadata(result.isSuccess() ? Status.PASS : Status.FAILURE);
		return messages;
	}

	public String validateBag(ValidVerifierImpl validVerifier) {
		prepareBilBagInfoIfDirty();
		
		String messages = null;
		SimpleResult result = validVerifier.verify(bilBag);
		
		if (validVerifier.isCancelled()) {
			isValid(Status.UNKNOWN);
			return "Validation check cancelled.";
		} 
		
		if (!result.isSuccess()) {
			messages = "Bag is not valid:\n";
			messages += result.toString();
		}
		this.isValid(result.isSuccess()? Status.PASS : Status.FAILURE);
		if (result.isSuccess())
			isComplete(Status.PASS);
		if (!isNoProject()) {
			String msgs = validateMetadata();
			if (msgs != null) {
				if (messages != null)
					messages += msgs;
				else
					messages = msgs;
			}
		}
		return messages;
	}

	private String fileStripSuffix(String filename) {
		StringTokenizer st = new StringTokenizer(filename, ".");
		String name = st.nextToken();
		return name;
	}

	private String writeBag(Writer bw) {
		String messages = null;
		String bagName = "";
		File bagFile = null;
		File parentDir = null;
		bagName = fileStripSuffix(getRootDir().getName());
		parentDir = getRootDir().getParentFile();
		log.debug("DefaultBag.writeBag parentDir: " + parentDir + ", bagName: "
				+ bagName);
		
		this.setName(bagName);
		if (this.serialMode == NO_MODE) {
			bagFile = new File(parentDir, this.getName());
		} else if (this.serialMode == ZIP_MODE) {
			String s = bagName;
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) {
				String sub = s.substring(i + 1);
				if (!sub.equalsIgnoreCase(ZIP_LABEL)) {
					bagName += "." + ZIP_LABEL;
				}
			} else {
				bagName += "." + ZIP_LABEL;
			}
			bagFile = new File(parentDir, bagName);
			/*long zipSize = this.getSize() / MB;
			if (zipSize > 100) {
				messages = "WARNING: You may not be able to network transfer files > 100 MB!\n";
			}*/
		} else if (this.serialMode == TAR_MODE) {
			String s = bagName;
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) {
				if (!s.substring(i + 1).toLowerCase().equals(TAR_LABEL)) {
					bagName += "." + TAR_LABEL;
				}
			} else {
				bagName += "." + TAR_LABEL;
			}
			bagFile = new File(parentDir, bagName);
			/*long zipSize = this.getSize() / MB;
			if (zipSize > 100) {
				messages = "WARNING: You may not be able to network transfer files > 100 MB!\n";
			}*/
		} else if (this.serialMode == TAR_GZ_MODE) {
			String s = bagName;
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) {
				if (!s.substring(i + 1).toLowerCase().equals(TAR_GZ_LABEL)) {
					bagName += "." + TAR_GZ_LABEL;
				}
			} else {
				bagName += "." + TAR_GZ_LABEL;
			}
			bagFile = new File(parentDir, bagName);
			/*long zipSize = this.getSize() / MB;
			if (zipSize > 100) {
				messages = "WARNING: You may not be able to network transfer files > 100 MB!\n";
			}*/
		} else if (this.serialMode == TAR_BZ2_MODE) {
			String s = bagName;
			int i = s.lastIndexOf('.');
			if (i > 0 && i < s.length() - 1) {
				if (!s.substring(i + 1).toLowerCase().equals(TAR_BZ2_LABEL)) {
					bagName += "." + TAR_BZ2_LABEL;
				}
			} else {
				bagName += "." + TAR_BZ2_LABEL;
			}
			bagFile = new File(parentDir, bagName);
			/*long zipSize = this.getSize() / MB;
			if (zipSize > 100) {
				messages = "WARNING: You may not be able to network transfer files > 100 MB!\n";
			}*/
		}
		setBagFile(bagFile);
		
		log.info("Bag-Info to write: " + bilBag.getBagInfoTxt());
		
		this.isSerialized(false);
		Bag newBag = bw.write(bilBag, bagFile);
		if (newBag != null) {
			bilBag = newBag;
			// write successful
			this.isSerialized(true);
		}
		return messages;
	}

	public void updateStrategy() {
		bagStrategy = getBagInfoStrategy();
	}

	protected Verifier getBagInfoStrategy() {
		List<String> rulesList = new ArrayList<String>();
		HashMap<String, BagInfoField> fieldMap = this.getInfo().getFieldMap();
		if (fieldMap != null) {
			Set<String> keys = fieldMap.keySet();
			for (Iterator<String> iter = keys.iterator(); iter.hasNext();) {
				String label = (String) iter.next();
				BagInfoField field = fieldMap.get(label);
				if (field.isRequired()) {
					rulesList.add(field.getLabel());
				}
			}
		}
		String[] rules = new String[rulesList.size()];
		for (int i = 0; i < rulesList.size(); i++)
			rules[i] = new String(rulesList.get(i));

		Verifier strategy = new RequiredBagInfoTxtFieldsVerifier(rules);

		return strategy;
	}

	private void generateManifestFiles() {
		DefaultCompleter completer = new DefaultCompleter(new BagFactory());
		if (this.isBuildPayloadManifest) {
			if (this.payloadManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.MD5.bagItAlgorithm)) {
				completer.setPayloadManifestAlgorithm(Algorithm.MD5);
			} else if (this.payloadManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.SHA1.bagItAlgorithm)) {
				completer.setPayloadManifestAlgorithm(Algorithm.SHA1);
			} else if (this.payloadManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.SHA256.bagItAlgorithm)) {
				completer.setPayloadManifestAlgorithm(Algorithm.SHA256);
			} else if (this.payloadManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.SHA512.bagItAlgorithm)) {
				completer.setPayloadManifestAlgorithm(Algorithm.SHA512);
			} else {
				completer.setPayloadManifestAlgorithm(Algorithm.MD5);
			}
			if (this.isHoley) {
				completer.setClearExistingPayloadManifests(true);
			} else {
				completer.setClearExistingPayloadManifests(true);
			}
		}
		if (this.isBuildTagManifest) {
			completer.setClearExistingTagManifests(true);
			completer.setGenerateTagManifest(true);
			if (this.tagManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.MD5.bagItAlgorithm)) {
				completer.setTagManifestAlgorithm(Algorithm.MD5);
			} else if (this.tagManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.SHA1.bagItAlgorithm)) {
				completer.setTagManifestAlgorithm(Algorithm.SHA1);
			} else if (this.tagManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.SHA256.bagItAlgorithm)) {
				completer.setTagManifestAlgorithm(Algorithm.SHA256);
			} else if (this.tagManifestAlgorithm
					.equalsIgnoreCase(Manifest.Algorithm.SHA512.bagItAlgorithm)) {
				completer.setTagManifestAlgorithm(Algorithm.SHA512);
			} else {
				completer.setTagManifestAlgorithm(Algorithm.MD5);
			}
		}
		if (bilBag.getBagInfoTxt() != null)
			completer.setGenerateBagInfoTxt(true);
		bilBag = completer.complete(bilBag);
	}

	public void clear() {
		clearProfile();
		bagInfo.clearFields();
		
	}

	public void addField(BagInfoField field) {
		changeToDirty();
		isValidMetadata(Status.UNKNOWN);
		
		bagInfo.addField(field);
	}

	public void removeBagInfoField(String key) {
		changeToDirty();
		isValidMetadata(Status.UNKNOWN);
		
		bagInfo.removeField(key);
	}
	
	public void addFileToPayload(File file) {
		changeToDirty();
		isComplete(Status.UNKNOWN);
        
		bilBag.addFileToPayload(file);
	}
	
	public Collection<BagFile> getTags() {
		return bilBag.getTags();
	}
	
	public void removeBagFile(String fileName) {
		changeToDirty();
		isComplete(Status.UNKNOWN);
		
		bilBag.removeBagFile(fileName);
	}
	
	public void removePayloadDirectory(String fileName) {
		changeToDirty();
		isComplete(Status.UNKNOWN);
		bilBag.removePayloadDirectory(fileName);
	}
	
	public Collection<BagFile> getPayload() {
		return bilBag.getPayload();
	}
	
	public FetchTxt getFetchTxt() {
		return bilBag.getFetchTxt();
	}
	
	private void changeToDirty() {
		this.dirty = true;
		isValid(Status.UNKNOWN);
	}
	
	private void prepareBilBagInfoIfDirty() {
		if (dirty) {
			bagInfo.prepareBilBagInfo(bilBag.getBagInfoTxt());
		}
	}
}
