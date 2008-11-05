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
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.VerifyStrategy;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.FetchTxt;
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
	private List<BaggerFileEntity> rootSrc;
	private File rootDir;
	private List<BaggerFileEntity> rootTree;
	private String name;
	private long size;
	private File file;

	private Project project;
    private BagInfo bagInfo = null;
    private Fetch fetch = null;
    private BagIt bagIt = null;
    private Data data = null;
	private List<BaggerManifest> baggerManifests = null;
	private List<BaggerTagManifest> baggerTagManifests = null;

    private boolean isNewBag = true;
	private boolean isHoley = false;
	private boolean isSerial = true;
	private boolean isCleanup = false;
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
	
	private void reset() {
		this.isComplete = false;
		this.isValid = false;
		this.isValidMetadata = false;
		this.isSerialized = false;
	}
	
	public void generate() {
		reset();
		if (rootSrc == null) rootSrc = new ArrayList<BaggerFileEntity>();
        if (rootTree == null) rootTree = new ArrayList<BaggerFileEntity>();
        if (fetch == null) fetch = new Fetch(this);
		if (bagIt == null) bagIt = new BagIt(getBagConstants());
		if (bagInfo == null) bagInfo = new BagInfo(this);
        if (data == null) data = new Data();
    	data.setFiles(rootTree);

		String fname = AbstractBagConstants.PAYLOAD_MANIFEST_PREFIX + ManifestType.MD5 + AbstractBagConstants.PAYLOAD_MANIFEST_SUFFIX;
    	BaggerManifest manifest = new BaggerManifest(fname, this);
    	manifest.setType(ManifestType.MD5);
    	data.setSizeFiles(manifest.getTotalSize());
    	data.setNumFiles(manifest.getNumFiles());
    	ArrayList<BaggerManifest> mset = new ArrayList<BaggerManifest>();
    	mset.add(manifest);
    	this.setBaggerManifests(mset);
/* */
    	List<BaggerTagManifest> tagManifestList = this.getBaggerTagManifests();
    	if (tagManifestList == null || tagManifestList.isEmpty()) {
        	ArrayList<BaggerTagManifest> tmset = new ArrayList<BaggerTagManifest>();
        	BaggerTagManifest tagManifest = new BaggerTagManifest(this);
        	tagManifest.setType(ManifestType.MD5);
        	tmset.add(tagManifest);
        	this.setBaggerTagManifests(tmset);
    	}
/* */
	}
	
	// The opens the given rootDir and tries to create a new bag out of it.
	// TODO: If zip read contents, else open bag and call createBag(file)
	public void openBag(File rootDir) {
		reset();
		if (rootSrc == null) rootSrc = new ArrayList<BaggerFileEntity>();
        if (rootTree == null) rootTree = new ArrayList<BaggerFileEntity>();
        if (fetch == null) fetch = new Fetch(this);
		if (bagIt == null) bagIt = new BagIt(getBagConstants());
		if (bagInfo == null) bagInfo = new BagInfo(this);
        if (data == null) data = new Data();
		isNewBag = false;
        setRootDir(rootDir);
        gov.loc.repository.bagit.Bag bagitBag = BagFactory.createBag(rootDir);

		BagItTxt bagItTxt = bagitBag.getBagItTxt();
		bagIt.setEncoding(bagItTxt.getCharacterEncoding());
		System.out.println("bagIt version: " + bagItTxt.getVersion());
		if (bagItTxt.getVersion() != null && !bagItTxt.getVersion().isEmpty() && !bagItTxt.getVersion().equalsIgnoreCase("null"))
			bagIt.setVersion(bagItTxt.getVersion());

        BagInfoTxt bagInfoTxt = bagitBag.getBagInfoTxt();
		BagOrganization bagOrganization = this.bagInfo.getBagOrganization();
		Contact contact = bagOrganization.getContact();
		contact.setContactName(bagInfoTxt.getContactName());
		contact.setTelephone(bagInfoTxt.getContactPhone());
		contact.setEmail(bagInfoTxt.getContactEmail());
		bagOrganization.setContact(contact);
		bagOrganization.setOrgName(bagInfoTxt.getSourceOrganization());
		bagOrganization.setOrgAddress(bagInfoTxt.getOrganizationAddress());
		this.bagInfo.setBagOrganization(bagOrganization);
		if (bagInfoTxt.getExternalDescription() != null && !bagInfoTxt.getExternalDescription().equalsIgnoreCase("null"))
			this.bagInfo.setExternalDescription(bagInfoTxt.getExternalDescription());
		else
			this.bagInfo.setExternalDescription("");
		if (bagInfoTxt.getBaggingDate() != null && !bagInfoTxt.getBaggingDate().equalsIgnoreCase("null"))
			this.bagInfo.setBaggingDate(bagInfoTxt.getBaggingDate());
		else
			this.bagInfo.setBaggingDate("");
		if (bagInfoTxt.getExternalIdentifier() != null && !bagInfoTxt.getExternalIdentifier().equalsIgnoreCase("null"))
			this.bagInfo.setExternalIdentifier(bagInfoTxt.getExternalIdentifier());
		else
			this.bagInfo.setExternalIdentifier("");
		if (bagInfoTxt.getBagSize() != null && !bagInfoTxt.getBagSize().equalsIgnoreCase("null"))
			this.bagInfo.setBagSize(bagInfoTxt.getBagSize());
		else
			this.bagInfo.setBagSize("");
		if (bagInfoTxt.getPayloadOssum() != null && !bagInfoTxt.getPayloadOssum().equalsIgnoreCase("null"))
			this.bagInfo.setPayloadOssum(bagInfoTxt.getPayloadOssum());
		else
			this.bagInfo.setPayloadOssum("");
		if (bagInfoTxt.getBagGroupIdentifier() != null && !bagInfoTxt.getBagGroupIdentifier().equalsIgnoreCase("null"))
			this.bagInfo.setBagGroupIdentifier(bagInfoTxt.getBagGroupIdentifier());
		else
			this.bagInfo.setBagGroupIdentifier("");
		if (bagInfoTxt.getBagCount() != null && !bagInfoTxt.getBagCount().equalsIgnoreCase("null"))
			this.bagInfo.setBagCount(bagInfoTxt.getBagCount());
		else
			this.bagInfo.setBagCount("");
		if (bagInfoTxt.getInternalSenderIdentifier() != null && !bagInfoTxt.getInternalSenderIdentifier().equalsIgnoreCase("null"))
			this.bagInfo.setInternalSenderIdentifier(bagInfoTxt.getInternalSenderIdentifier());
		else
			this.bagInfo.setInternalSenderIdentifier("");
		if (bagInfoTxt.getInternalSenderDescription() != null && !bagInfoTxt.getInternalSenderDescription().equalsIgnoreCase("null"))
			this.bagInfo.setInternalSenderDescription(bagInfoTxt.getInternalSenderDescription());
		else
			this.bagInfo.setInternalSenderDescription("");
/* */
		List<gov.loc.repository.bagit.Manifest> payloadManifests = bagitBag.getPayloadManifests();
		for (int index=0; index < payloadManifests.size(); index++) {
    		gov.loc.repository.bagit.Manifest manifest = payloadManifests.get(index);
    		this.putPayloadFile(manifest);
		}
/* */		
    	ArrayList<BaggerTagManifest> tagManifestList = new ArrayList<BaggerTagManifest>();
		List<gov.loc.repository.bagit.Manifest> tagManifests = bagitBag.getTagManifests();
    	if (tagManifests != null && !tagManifests.isEmpty()) {
        	for (int i=0; i < tagManifests.size(); i++) {
        		gov.loc.repository.bagit.Manifest manifest = tagManifests.get(i);
        		this.putTagFile(manifest);
        		
        		BaggerTagManifest tagManifest = new BaggerTagManifest(this);
            	tagManifest.setType(ManifestType.MD5);
            	tagManifest.fromString(manifest.toString());
            	tagManifestList.add(tagManifest);
        	}
        	this.setBaggerTagManifests(tagManifestList);
    	}
/* */
    	FetchTxt fetchTxt = bagitBag.getFetchTxt();
    	if (fetchTxt != null && !fetchTxt.isEmpty()) {
    		this.setIsHoley(true);
    		this.fetch.setContent(fetchTxt.toString());
    	}
/* */
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

	// Sets the root tree for this bag.  The root tree is a list of all the files
	// that are being display in the file tree selection window.
	public void setRootTree(List<BaggerFileEntity> rootTree) {
		this.rootTree = rootTree;
	}

	// Returns the root tree for this bag.  The root tree is a list of all the files
	// that are being display in the file tree selection window.
	public List<BaggerFileEntity> getRootTree() {
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

	// Returns the list of root sources for this bag.  A root source
	// is a directory bag looks for when it wants to copy source files to the bag
	// data directory.
	public List<BaggerFileEntity> getRootSrc() {
		return this.rootSrc;
	}
	
	// Add a root source to the list of root sources for this bag.  A root source
	// is a directory bag looks for when it wants to copy source files to the bag
	// data directory.
	public boolean addRootSrc(BaggerFileEntity src) {
		return this.rootSrc.add(src);
	}
	
	public void setRootDir(File rootDir) {
		this.rootDir = rootDir;
	}
	
	public File getRootDir() {
		return this.rootDir;
	}
/* */
	public void setBaggerManifests(List<BaggerManifest> manifests) {
		this.baggerManifests = manifests;
	}

	public List<BaggerManifest> getBaggerManifests() {
		return this.baggerManifests;
	}

	public void setBaggerTagManifests(List<BaggerTagManifest> tagManifests) {
		this.baggerTagManifests = tagManifests;
	}
	
	public List<BaggerTagManifest> getBaggerTagManifests() {
		return this.baggerTagManifests;
	}
/* */
/* */
	public void setFetch(Fetch fetch) {
		this.fetch = fetch;
	}

	public Fetch getFetch() {
		return this.fetch;
	}
/* */
/* */
	public void setBagIt(BagIt bagIt) {
		this.bagIt = bagIt;
	}

	public BagIt getBagIt() {
		return this.bagIt;
	}
/* */
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
	
	public void setIsNewBag(boolean b) {
		this.isNewBag = b;
	}
	
	public boolean getIsNewBag() {
		return this.isNewBag;
	}
	
	public void setIsCleanup(boolean b) {
		this.isCleanup = b;
	}
	
	public boolean getIsCleanup() {
		return this.isCleanup;
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
	
	// TODO Break this down into multiple steps so that each step can send bag progress 
	// message to the console.
    // TODO What if file already exists?  Error or message to overwrite
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
				if (this.isValidMetadata) {
					messages += serializeBag();
					if (this.isCleanup) {
						messages += cleanup();
					}
				}
			}
		} catch (Exception e) {
			messages += "\n" + "Exception while creating bag:\n" + e.toString();
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return messages;
	}
	
	public String createBagDir(File path) {
		boolean success = false;
		String messages = "";
		display("Bag.write: create and open bag: " + path.getAbsolutePath() + " " + this.getName());
		display("Bag.writePath: " + path.getAbsolutePath() + "/" + this.getName());
		messages += "Create and open bag name directory.\n";
		if (path.getAbsolutePath() == null || this.getName() == null) {
	    	messages += reportError(messages, "BagView.write failed to create directory because Bag path or name is NULL!");
			log.error(messages);
			return messages;
		}
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
	
	public String writeMetaFiles() {
		String messages = "\n";
/* */
		display("Bag.write: create and write manifest-<type>.txt in bag name directory");
		for (int i=0; i < baggerManifests.size(); i++) {
    		BaggerManifest manifest = baggerManifests.get(i);
			messages += "Create and write manifest-"+ manifest.getType() +".txt in bag name directory.\n";
    		manifest.writeData();
    		manifest.write(rootDir);
    	}
/* */		    	
/* */			
    	if (this.isHoley) {
			display("Bag.write: isHoley - create and write fetch.txt in bag name directory");
			messages += "Create and write fetch.txt in bag name directory.\n";
    	    fetch.setName("fetch.txt");
    	    fetch.writeData();
    	    fetch.write(rootDir);
    	}
/* */
/* */
		display("Bag.write: create and write bag-info.txt in bag name directory");
		messages += "Create and write bag-info.txt in bag name directory.\n";
		bagInfo.setName(AbstractBagConstants.BAGINFO_TXT);
		bagInfo.writeData();
		bagInfo.write(rootDir);
/* */
/* */
		display("Bag.write: create and write bagit.txt in bag name directory");
		messages += "Create and write bagit.txt in bag name directory.\n";
		bagIt.setName(AbstractBagConstants.BAGIT_TXT);
		bagIt.writeData();
		bagIt.write(rootDir);
/* */
/* */
		display("Bag.write: create and write tagmanifest-<type>.txt in bag name directory");
    	for (int i=0; i < baggerTagManifests.size(); i++) {
    		BaggerTagManifest tagManifest = baggerTagManifests.get(i);
			messages += "Create and write tagmanifest-"+ tagManifest.getType() +".txt in bag name directory.\n";
    		tagManifest.setType(ManifestType.MD5);
    		tagManifest.writeData();
    		tagManifest.write(rootDir);
    		baggerTagManifests.set(i, tagManifest);
    	}
    	this.setBaggerTagManifests(baggerTagManifests);
/* */
		return messages;
	}
	
	public String copyDataToBag(File rootDir) {
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
			if (this.isCleanup)	messages += cleanup();
			return messages;
	    }
		display("Bag.write: create and write list of src data to the bag data directory");
		List<BaggerFileEntity> srcList = this.getRootSrc();
		for (int i=0; i<srcList.size(); i++) {
			BaggerFileEntity bfe = srcList.get(i);
			if (!bfe.getIsInBag()) {
				File srcDir = bfe.getRootSrc();
				try
				{
					// TODO: Create the tree list of selected nodes, then in copyFiles 
					// check to see whether file to be copy is in the nodes list otherwise 
					// don't copy it.  If it already exists it needs to be deleted.
					display("Bag.write copyFiles: " + srcDir.getAbsolutePath() + " to: " + dataDir.getAbsolutePath());
					File rootFile = new File(dataDir, srcDir.getName());
					FileUtililties.copyFiles(srcDir, rootFile);
				}
				catch(IOException e)
				{
			    	messages += reportError(messages, "ERROR in BagView.write copyFiles: " + e.getMessage());
			    	log.error(messages);
					if (this.isCleanup) messages += cleanup();
			    	return messages;
				}
			}
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
		reset();
		gov.loc.repository.bagit.Bag bagitBag = this;
		String messages = "";
//		System.out.println("validateAndBag: " + this.rootDir.getAbsolutePath());
		try {
				bagitBag = BagFactory.createBag(this.rootDir);
//			}
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
	
	public String serializeBag() {
		String messages = "";
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
