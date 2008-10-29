package gov.loc.repository.bagger.bag;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;
import java.io.File;
import java.io.IOException;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.FileEntity;
import gov.loc.repository.bagger.util.FileUtililties;

import gov.loc.repository.bagit.v0_96.impl.BagImpl;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.VerifyStrategy;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;
import gov.loc.repository.bagit.verify.RequiredBagInfoTxtFieldsStrategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Simple JavaBean business object representing a bag.
 *
 *
        <bag_dir>/
        |   manifest-<algorithm>.txt
        |   bagit.txt
        |   [optional additional tag files]
        |      fetch.txt
        |      bag-info.txt
        \--- data/
              |   [optional file hierarchy]
  *
 * @author Jon Steinbach
 */
public class BaggerBag extends BagImpl {
	public static final long KB = 1024;
	public static final long MB = 1048576;
	public static final long GB = 1073741824;
	public static final long MAX_SIZE = 104857600;
	private static final Log log = LogFactory.getLog(BaggerBag.class);

	private Date createDate;
	private File rootSrc;
	private File rootDir;
	private List<File> rootTree;
	private String name;
	private long size;
	private File file;

	private Project project;
    private BagInfo bagInfo = null;
    private Data data = null;

    private boolean isNewBag = true;
	private boolean isHoley = false;
	private boolean isSerial = true;
	private boolean isFetch = false;	
	private boolean isComplete = false;	
	private boolean isValid = false;
	private boolean isValidForms = false;
	private boolean isValidMetadata = false;
	private boolean isSerialized = false;
	private boolean isCopyright = false;

	public BaggerBag() {
		super();
	}
	
	public void init() {
		FetchTxt fetch = getBagPartFactory().createFetchTxt(this);
/*
        if (fetch == null) fetch = new Fetch();
        else fetch = this.getFetch();
        this.setFetch(fetch);
*/
		BagItTxt bagIt = getBagPartFactory().createBagItTxt(getBagConstants());
/*		
		if (bagIt == null) bagIt = new BagIt();
		else bagIt = this.getBagIt();
		this.setBagIt(bagIt);
*/
		BagInfoTxt bagInfoTxt = getBagPartFactory().createBagInfoTxt(getBagConstants());

/* */
		if (bagInfo == null) bagInfo = new BagInfo(this);
        else bagInfo = this.getInfo();
/* */
        if (rootTree == null) rootTree = new ArrayList<File>();
/* */
        if (data == null) data = new Data();
    	else data = this.getData();
    	data.setFiles(rootTree);
    	this.setData(data);
/* */
    	if (rootSrc != null) this.setRootSrc(rootSrc);
/*
    	Manifest manifest = new Manifest(this);
    	manifest.setType(ManifestType.MD5);
    	data.setSizeFiles(manifest.getTotalSize());
    	data.setNumFiles(manifest.getNumFiles());
    	this.setData(data);
    	ArrayList<Manifest> mset = new ArrayList<Manifest>();
    	mset.add(manifest);
    	this.setManifests(mset);
*/
/*
    	List<TagManifest> tagManifestList = this.getTagManifests();
    	if (tagManifestList == null || tagManifestList.isEmpty()) {
        	ArrayList<TagManifest> tmset = new ArrayList<TagManifest>();
        	TagManifest tagManifest = new TagManifest(this);
        	tagManifest.setType(ManifestType.MD5);
        	tmset.add(tagManifest);    		
        	this.setTagManifests(tmset);
    	}
*/
	}
	
	// TODO: If zip read contents, else open bag and call createBag(file)
	public void openBag(File rootDir) {
		isNewBag = false;
        this.setRootDir(rootDir);

        gov.loc.repository.bagit.Bag bagitBag = BagFactory.createBag(rootDir);	
		BagInfoTxt bagInfoTxt = bagitBag.getBagInfoTxt();
		this.bagInfo.setBagCount(bagInfoTxt.getBagCount());
		this.bagInfo.setBaggingDate(bagInfoTxt.getBaggingDate());
		this.bagInfo.setBagGroupIdentifier(bagInfoTxt.getBagGroupIdentifier());
		BagOrganization bagOrganization = this.bagInfo.getBagOrganization();
		Contact contact = bagOrganization.getContact();
		contact.setContactName(bagInfoTxt.getContactName());
		contact.setTelephone(bagInfoTxt.getContactPhone());
		contact.setEmail(bagInfoTxt.getContactEmail());
		bagOrganization.setContact(contact);
		bagOrganization.setOrgName(bagInfoTxt.getSourceOrganization());
		bagOrganization.setOrgAddress(bagInfoTxt.getOrganizationAddress());
		this.bagInfo.setBagOrganization(bagOrganization);
		this.bagInfo.setBagSize(bagInfoTxt.getBagSize());
		this.bagInfo.setExternalDescription(bagInfoTxt.getExternalDescription());
		this.bagInfo.setExternalIdentifier(bagInfoTxt.getExternalIdentifier());
		this.bagInfo.setInternalSenderDescription(bagInfoTxt.getInternalSenderDescription());
		this.bagInfo.setInternalSenderIdentifier(bagInfoTxt.getInternalSenderIdentifier());
		this.bagInfo.setPayloadOssum(bagInfoTxt.getPayloadOssum());
/*
		BagItTxt bagItTxt = bagitBag.getBagItTxt();
		bagIt.setVersion(bagItTxt.getVersion());
		bagIt.setEncoding(bagItTxt.getCharacterEncoding());
*/
//		gov.loc.repository.bagit.FetchTxt fetchTxt = bagitBag.getFetchTxt();

        if (rootTree == null) rootTree = new ArrayList<File>();
/*
        List<gov.loc.repository.bagit.Manifest> manifests = bagitBag.getPayloadManifests();
		Object[] listManifest = manifests.toArray();
		for (int i=0; i < listManifest.length; i++) {
			gov.loc.repository.bagit.Manifest manifest = (gov.loc.repository.bagit.Manifest) listManifest[i];
			rootTree.add(new File(manifest.getFilepath()));
		}
*/
		Collection<BagFile> bagFiles = bagitBag.getPayloadFiles();
		Object[] listManifest = bagFiles.toArray();
		for (int i=0; i < listManifest.length; i++) {
			BagFile bagFile = (BagFile) listManifest[i];
			File file = new File(bagFile.getFilepath());
			if (i == 0) {
//				this.setRootSrc(file.getParentFile());
//				System.out.println("rootSrc: " + this.rootSrc.getAbsolutePath());
			}
			rootTree.add(file);				
		}

		data.setFiles(rootTree);
    	this.setData(data);
/*
    	Manifest manifest = new Manifest(this);
    	manifest.setType(ManifestType.MD5);
    	data.setSizeFiles(manifest.getTotalSize());
    	data.setNumFiles(manifest.getNumFiles());
    	this.setData(data);
    	ArrayList<Manifest> mset = new ArrayList<Manifest>();
    	mset.add(manifest);
    	this.setManifests(mset);
*/
/*
		List<gov.loc.repository.bagit.Manifest> tagManifests = bagitBag.getTagManifests();
    	List<TagManifest> tagManifestList = this.getTagManifests();
    	if (tagManifestList == null || tagManifestList.isEmpty()) {
        	ArrayList<TagManifest> tmset = new ArrayList<TagManifest>();
        	TagManifest tagManifest = new TagManifest(this);
        	tagManifest.setType(ManifestType.MD5);
        	tmset.add(tagManifest);    		
        	this.setTagManifests(tmset);
    	}
*/
	}
	
	public void setName(String name) {
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

	public void setFile(File file) {
		this.file = file;
	}

	public File getFile() {
		return this.file;
	}

	public void setRootTree(List<File> rootTree) {
		this.rootTree = rootTree;
	}
	
	public List<File> getRootTree() {
		return this.rootTree;
	}

	public void setCreateDate(Date createDate) {
		this.createDate = createDate;
	}

	public Date getCreateDate() {
		return this.createDate;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public Project getProject() {
		return this.project;
	}

	public void setRootSrc(File rootSrc) {
		this.rootSrc = rootSrc;
	}
	
	public File getRootSrc() {
		return this.rootSrc;
	}
	
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}
	
	public File getRootDir() {
		return this.rootDir;
	}
/*
	public void setManifests(List<Manifest> manifests) {
		this.manifests = manifests;
	}

	public List<Manifest> getManifests() {
		return this.manifests;
	}

	public void addManifest(Manifest manifest) {
		manifests.add(manifest);
	}
*/
/*	
	public void setTagManifests(List<TagManifest> tagManifests) {
		this.tagManifests = tagManifests;
	}
	
	public List<TagManifest> getTagManifests() {
		return this.tagManifests;
	}
	
	public void addTagManifest(TagManifest tagManifest) {
		tagManifests.add(tagManifest);
	}
*/
/*
	public void setFetch(Fetch fetch) {
		this.fetch = fetch;
	}

	public Fetch getFetch() {
		return this.fetch;
	}
*/
/*
	public void setBagIt(BagIt bagIt) {
		this.bagIt = bagIt;
	}

	public BagIt getBagIt() {
		return this.bagIt;
	}
*/
	public void setInfo(BagInfo bagInfo) {
		this.bagInfo = bagInfo;
		this.setName(bagInfo.getBagName());
	}

	public BagInfo getInfo() {
		return this.bagInfo;
	}

	public void setData(Data data) {
		this.data = data;
	}

	public Data getData() {
		return this.data;
	}
	
	public void setIsHoley(boolean b) {
		this.isHoley = b;
	}
	
	public boolean getIsHoley() {
		return this.isHoley;
	}
	
	public void setIsSerial(boolean b) {
		this.isSerial = b;
	}
	
	public boolean getIsSerial() {
		return this.isSerial;
	}

	public void setIsFetch(boolean b) {
		this.isFetch = b;
	}
	
	public boolean getIsFetch() {
		return this.isFetch;
	}

	public void setIsValid(boolean b) {
		this.isValid = b;
	}
	
	public boolean getIsValid() {
		return this.isValid;
	}
	
	public void setIsValidForms(boolean b) {
		this.isValidForms = b;
	}
	
	public boolean getIsValidForms() {
		return this.isValidForms;
	}
	
	public void setIsValidMetadata(boolean b) {
		this.isValidMetadata = b;
	}
	
	public boolean getIsValidMetadata() {
		return this.isValidMetadata;
	}

	public void setIsSerialized(boolean b) {
		this.isSerialized = b;
	}
	
	public boolean getIsSerialized() {
		return this.isSerialized;
	}
	
	public void setIsCopyright(boolean b) {
		this.isCopyright = b;
	}
	
	public boolean getIsCopyright() {
		return this.isCopyright;
	}

	public void setIsComplete(boolean b) {
		this.isComplete = b;
	}
	
	public boolean getIsComplete() {
		return this.isComplete;
	}
	
	// Break this down into multiple steps so that each step can send bag progress message to the console.
	public String write(File path) {
		String messages = "";

		try {
			display("Bag.write: validateAndBag");
			messages += validateForms();
			if (isValidForms) {
				messages += createBagDir(path);
				messages += writeMetaFiles();
				messages += copyDataToBag(rootDir);
				if (isValidForms) {
					messages += validateAndBag();
				}
				messages += cleanup();
			}
		} catch (Exception e) {
			messages += "\n" + "Exception while creating bag:\n" + e.toString();
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return messages;
	}
	
	private String createBagDir(File path) {
		boolean success = false;
		String messages = "";
		display("Bag.write: create and open bag name directory");
		messages += "Create and open bag name directory.\n";
		if (path.getAbsolutePath() == null || this.getName() == null) {
	    	messages += reportError(messages, "BagView.write failed to create directory: NULL");
			log.error(messages);
			return messages;			
		}
		display("Bag.writePath: " + path.getAbsolutePath() + "/" + this.getName());
		File rootDir = new File(path.getAbsolutePath(), this.getName());
		if (rootDir.exists()) success = true;
		else success = rootDir.mkdir();
	    if (!success) {
	    	messages += reportError(messages, "BagView.write failed to create directory: " + rootDir);
			log.error(messages);
			return messages;
	    }
		this.setRootDir(rootDir);
		
		return messages;
	}
	
	private String writeMetaFiles() {
		String messages = "";
/*
		display("Bag.write: create and write manifest-<type>.txt in bag name directory");
		for (int i=0; i < manifests.size(); i++) {
    		Manifest manifest = manifests.get(i);
			messages += "Create and write manifest-"+ manifest.getType() +".txt in bag name directory.\n";
    		manifest.writeData();
    		manifest.write(rootDir);
    	}
*/		    	
/*				
    	if (this.isHoley) {
			display("Bag.write: isHoley - create and write fetch.txt in bag name directory");
			messages += "Create and write fetch.txt in bag name directory.\n";
    	    fetch.setName("fetch.txt");
    	    fetch.writeData();
    	    fetch.write(rootDir);
    	}
*/
/*
		display("Bag.write: create and write bag-info.txt in bag name directory");
		messages += "Create and write bag-info.txt in bag name directory.\n";
		bagInfo.setName(AbstractBagConstants.BAGINFO_TXT);
		bagInfo.writeData();
		bagInfo.write(rootDir);
*/
/*
		display("Bag.write: create and write bagit.txt in bag name directory");
		messages += "Create and write bagit.txt in bag name directory.\n";
		bagIt.setName(AbstractBagConstants.BAGIT_TXT);
		bagIt.writeData();
		bagIt.write(rootDir);
*/
/*
		display("Bag.write: create and write tagmanifest-<type>.txt in bag name directory");
    	for (int i=0; i < tagManifests.size(); i++) {
    		TagManifest tagManifest = tagManifests.get(i);
			messages += "Create and write tagmanifest-"+ tagManifest.getType() +".txt in bag name directory.\n";
    		tagManifest.setType(ManifestType.MD5);
    		tagManifest.writeData();
    		tagManifest.write(rootDir);
    		tagManifests.set(i, tagManifest);
    	}
    	this.setTagManifests(tagManifests);
*/
		return messages;
	}
	
	private String copyDataToBag(File rootDir) {
		boolean success = false;
		String messages = "";
    	display("Bag.write: create and open data directory");
		messages += "Create and write data payload directory.\n";
		File dataDir = new File(rootDir, AbstractBagConstants.DATA_DIRECTORY);
		if (dataDir.exists()) success = true;
		else success = dataDir.mkdir();
	    if (!success) {
	    	messages += reportError(messages, "ERROR in BagView.write failed to create directory: " + dataDir);
			log.error(messages);
			messages += cleanup();
			return messages;
	    }
		display("Bag.write: create and write data directory");
		File parent = this.getRootSrc();
		try
		{
			display("Bag.write copyFiles: " + parent.getAbsolutePath() + " to: " + dataDir.getAbsolutePath());
			FileUtililties.copyFiles(parent, dataDir);
		}
		catch(IOException e)
		{
	    	messages += reportError(messages, "ERROR in BagView.write copyFiles: " + e.getMessage());
	    	log.error(messages);
			messages += cleanup();
	    	return messages;
		}
		return messages;
	}
	
	private String cleanup() {
		boolean b = false;
		String messages = "";
		if (this.isSerial) {
			display("Bag.write: Clean up the files");
			display("Bag space: " + rootDir.getTotalSpace());
			b = FileUtililties.deleteDir(rootDir);
			if (!b) messages += reportError(messages, "Could not delete directory: " + rootDir);
			else messages += "Cleaning up bag directory.";

			rootDir.deleteOnExit();			
		}
		return messages;
	}
	
	public String validateForms() {
		String messages = "";
		
		this.isValidForms = true;
		messages = "Is bag form input valid? \n";
		if (this.isCopyright) {
			String publisher = this.getInfo().getPublisher();
			if (publisher == null || publisher.trim().isEmpty()) {
				this.isValidForms = false;
				messages += "eDeposit project require a publisher.";
			}
		}
		if (this.isValidForms) {
			messages += "Bag form input is valid.";
		}
		messages += "\n";
		return messages;
	}
	
	public String validateAndBag() {
		String messages = "";
		System.out.println("validateAndBag: " + this.rootDir.getAbsolutePath());
		try {
			//BagFactory bagFactory = new BagFactory();
			gov.loc.repository.bagit.Bag bagitBag = BagFactory.createBag(this.rootDir);	
			display("Bag.write: verifier isComplete?");
			SimpleResult result = bagitBag.isComplete();
			if (result.messagesToString() != null) messages += result.messagesToString();
			this.isComplete = result.isSuccess();
			display("Bag.write isComplete: " + isComplete);
			if (this.isComplete) {
				display("Bag.write: verifier isValid?");
				result = bagitBag.isValid();
				if (result.messagesToString() != null) messages += result.messagesToString();
				this.isValid = result.isSuccess();
				display("Bag.write isValid: " + isValid);
				if (this.isValid) {
					VerifyStrategy strategy = getBagInfoStrategy();
					result = bagitBag.additionalVerify(strategy);
					if (result.messagesToString() != null) messages += result.messagesToString();
					this.isValidMetadata = result.isSuccess();
					if (this.isValidMetadata) {
						String msg = null;
						if (this.isSerial) {
							display("Bag.write: Create a  zip file for serialized transfer of the bag");
							messages += "\nSuccessfully created bag: " + this.getInfo().getBagName();
							msg = FileUtililties.createZip(this, rootDir);
							if (msg == null) {
								messages += "Creating serialized zip file.";
								this.isSerialized = true;
								String zipName = this.getFile().getName();
								long zipSize = this.getSize() / MB;
								messages += "\nSuccessfully created zip file: " + zipName + " of size: " + zipSize + "(MB)";
								if (zipSize > 100) {
									messages += "\nWARNING: You may not be able to network transfer files > 100 MB!";
								}
							} else {
								reportError(messages, msg);	
							}
						} else {
							messages += "Successfully created bag: " + this.getInfo().getBagName();						
						}
					} else {
						reportError(messages, "Bag metadata is not valid for the project selected.");
					}
				} else {
					reportError(messages, "Bag is not valid.");	
				}
			} else {
				reportError(messages, "Bag is not complete.");
			}
			messages += "\n";
		} catch (Exception e) {
			e.printStackTrace();
			reportError(messages, e.getMessage());
		}

		return messages;
	}
	
	private VerifyStrategy getBagInfoStrategy() {
		List<String> rulesList = new ArrayList<String>();
		rulesList.add(BagInfoTxtImpl.SOURCE_ORGANIZATION);
		rulesList.add(BagInfoTxtImpl.ORGANIZATION_ADDRESS);
		rulesList.add(BagInfoTxtImpl.CONTACT_NAME);
		rulesList.add(BagInfoTxtImpl.CONTACT_PHONE);
		rulesList.add(BagInfoTxtImpl.CONTACT_EMAIL);
		rulesList.add(BagInfoTxtImpl.EXTERNAL_DESCRIPTION);
		rulesList.add(BagInfoTxtImpl.BAGGING_DATE);
		rulesList.add(BagInfoTxtImpl.EXTERNAL_IDENTIFIER);
		rulesList.add(BagInfoTxtImpl.BAG_SIZE);
		if (getIsCopyright()) {
			rulesList.add("Publisher");			
		}
		String[] rules = new String[rulesList.size()];
		for (int i=0; i< rulesList.size(); i++) rules[i] = new String(rulesList.get(i));
		
		VerifyStrategy strategy = new RequiredBagInfoTxtFieldsStrategy(rules);

		return strategy;		
	}

	private String reportError(String errors, String message) {
		if (errors == null) errors = message;
		else errors += "\n" + message;
		return errors;
	}

	public void display(String s) {
		//log.debug(s);
		log.info(s);
	}
}
