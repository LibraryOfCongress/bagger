package gov.loc.repository.bagger.bag.impl;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ArrayList;
import java.io.File;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.bag.BaggerFetch;
import gov.loc.repository.bagger.bag.BaggerFileEntity;
import gov.loc.repository.bagger.bag.BaggerOrganization;
import gov.loc.repository.bagger.util.FileUtililties;

import gov.loc.repository.bagit.Bag;
import gov.loc.repository.bagit.BagFactory;
import gov.loc.repository.bagit.BagFile;
import gov.loc.repository.bagit.BagInfoTxt;
import gov.loc.repository.bagit.BagItTxt;
import gov.loc.repository.bagit.BagWriter;
import gov.loc.repository.bagit.FetchTxt;
import gov.loc.repository.bagit.Manifest;
import gov.loc.repository.bagit.VerifyStrategy;
import gov.loc.repository.bagit.FetchTxt.FilenameSizeUrl;
import gov.loc.repository.bagit.bagwriter.FileSystemBagWriter;
import gov.loc.repository.bagit.bagwriter.ZipBagWriter;
import gov.loc.repository.bagit.bagwriter.TarBagWriter;
//import gov.loc.repository.bagit.bagwriter.TarBagWriter.Compression;
import gov.loc.repository.bagit.impl.BagItTxtImpl;
import gov.loc.repository.bagit.utilities.SimpleResult;
import gov.loc.repository.bagit.verify.RequiredBagInfoTxtFieldsStrategy;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.richclient.application.Application;
import org.springframework.richclient.dialog.CloseAction;
import org.springframework.richclient.dialog.ConfirmationDialog;
import org.springframework.richclient.progress.BusyIndicator;

/**
 * @author Jon Steinbach
 */
public class DefaultBag {
	private static final Log log = LogFactory.getLog(DefaultBag.class);
	public static final long KB = 1024;
	public static final long MB = 1048576;
	public static final long GB = 1073741824;
	public static final long MAX_SIZE = 104857600;  // 100 MB
	public static final short NO_MODE = 0;
	public static final short ZIP_MODE = 1;
	public static final short TAR_MODE = 2; 

	// Bag option flags
	private boolean isHoley = false;
	private boolean isSerial = true;
	private boolean isEdeposit = false;
	private boolean isNdnp = false;
	private boolean isCleanup = false;
	private boolean isNewbag = true;
	private short serialMode = NO_MODE;

	// Bag state flags
	private boolean isComplete = false;
	private boolean isValid = false;
	private boolean isValidForms = false;
	private boolean isValidMetadata = false;
	private boolean isSerialized = false;
	
	private File rootDir = null;
	private String name = new String("bag_");
	private long size;
	private File file;
	private long totalSize = 0;

	protected Bag bilBag;
	private Bag bagToValidate;

	private Collection<BagFile> rootPayload = null;
	private List<BaggerFileEntity> rootTree;

	protected DefaultBagInfo bagInfo = null;
	protected VerifyStrategy bagStrategy;
	protected BaggerFetch fetch;
	private Project project;

	public DefaultBag(File rootDir) {
		reset();
        if (rootTree == null) rootTree = new ArrayList<BaggerFileEntity>();
		this.rootDir = rootDir;
		if (rootDir != null) {
			bilBag = BagFactory.createBag(this.rootDir);
		} else {
			bilBag = BagFactory.createBag();
		}
		BagItTxt bagIt = bilBag.getBagItTxt();
		if (bagIt == null) {
			bagIt = bilBag.getBagPartFactory().createBagItTxt();
			bilBag.setBagItTxt(bagIt);
		}
		bagInfo = new DefaultBagInfo(this);
		BagInfoTxt bagInfoTxt = bilBag.getBagInfoTxt();
		if (bagInfoTxt == null) {
			bagInfoTxt = bilBag.getBagPartFactory().createBagInfoTxt();
			bilBag.setBagInfoTxt(bagInfoTxt);
		}
		if (bilBag.getFetchTxt() != null) {
        	setIsHoley(true);
    		String url = getBaseUrl(bilBag.getFetchTxt());
        	BaggerFetch fetch = this.getFetch();
        	fetch.setBaseURL(url);
        	this.fetch = fetch;
		}
    	//updateStrategy();
    }

	public String getDataDirectory() {
		return bilBag.getBagConstants().getDataDirectory();
	}
	
	protected void reset() {
		this.isComplete = false;
		this.isValid = false;
		this.isValidForms = false;
		this.isValidMetadata = false;
		this.isSerialized = false;
	}

	protected void display(String s) {
		//log.info(this.getClass().getName() + "." + s);
	}

	public Bag getBag() {
		return this.bilBag;
	}
	
	public void setBag(Bag bag) {
		this.bilBag = bag;
	}

	public void setName(String name) {
		String[] list = name.split("\\.");
		if (list != null && list.length > 0) name = list[0];
		this.name = name;
		this.getInfo().setBagName(name);
	}

	public String getName() {
		return this.name;
	}

	public void setSize(long size) {
		this.size = size;
    	String bagSize = "";
        long fsize = size;
        bagSize += fsize + " ";
        if (fsize > DefaultBag.GB) {
        	fsize /= DefaultBag.GB;
        	bagSize += "GB";
        } else if (fsize > DefaultBag.MB) {
        	fsize /= DefaultBag.MB;
        	bagSize += "MB";
        } else if (fsize > DefaultBag.KB) {
        	fsize /= DefaultBag.KB;
        	bagSize += "KB";
        } else {
        	bagSize += "Bytes";
        }
    	bagInfo.setBagSize(bagSize);
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

	// This directory contains either the bag directory or serialized bag file
	public void setRootDir(File rootDir) {
		try {
    		if (rootDir != null && rootDir.isFile()) {
    			rootDir = rootDir.getParentFile();
    			this.rootDir = rootDir;
    		} else {
        		this.rootDir = rootDir;
    		}
    	} catch (Exception e) {
    		e.printStackTrace();
        	log.error("DefaultBag.setName create rootDir: " + e.getMessage());
    	}
	}

	public File getRootDir() {
		return this.rootDir;
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
	
	public void setSerialMode(short m) {
		this.serialMode = m;
	}
	
	public short getSerialMode() {
		return this.serialMode;
	}

	public void setIsEdeposit(boolean b) {
		this.isEdeposit = b;
	}

	public boolean getIsEdeposit() {
		return this.isEdeposit;
	}

	public void setIsNdnp(boolean b) {
		this.isNdnp = b;
	}

	public boolean getIsNdnp() {
		return this.isNdnp;
	}
	
	public void setIsCleanup(boolean b) {
		this.isCleanup = b;
	}
	
	public boolean getIsCleanup() {
		return this.isCleanup;
	}
	
	public void setIsNewbag(boolean b) {
		this.isNewbag = b;
	}
	
	public boolean getIsNewbag() {
		return this.isNewbag;
	}

	public void isComplete(boolean b) {
		this.isComplete = b;
	}

	public boolean isComplete() {
		return this.isComplete;
	}

	public void isValid(boolean b) {
		this.isValid = b;
	}

	public boolean isValid() {
		return this.isValid;
	}

	public void isValidForms(boolean b) {
		this.isValidForms = b;
	}

	public boolean isValidForms() {
		return this.isValidForms;
	}

	public void isValidMetadata(boolean b) {
		this.isValidMetadata = b;
	}

	public boolean isValidMetadata() {
		return this.isValidMetadata;
	}

	public void isSerialized(boolean b) {
		this.isSerialized = b;
	}

	public boolean isSerialized() {
		return this.isSerialized;
	}

	public void copyBagToForm() {
		BagInfoTxt bagInfoTxt = this.bilBag.getBagInfoTxt();
		if (bagInfoTxt == null) {return;}
		// Replace the profile org and contact with info from existing bag
		BaggerOrganization baggerOrganization = new BaggerOrganization(); //this.bagInfo.getBagOrganization();
		Contact contact = new Contact(); //baggerOrganization.getContact();
		if (bagInfoTxt.getContactName() != null && !bagInfoTxt.getContactName().isEmpty()) 
    		contact.setContactName(bagInfoTxt.getContactName());
		else
    		contact.setContactName("");
		if (bagInfoTxt.getContactPhone() != null && !bagInfoTxt.getContactPhone().isEmpty()) 
    		contact.setTelephone(bagInfoTxt.getContactPhone());
		else
    		contact.setTelephone("");
		if (bagInfoTxt.getContactEmail() != null && !bagInfoTxt.getContactEmail().isEmpty()) 
    		contact.setEmail(bagInfoTxt.getContactEmail());
		else
    		contact.setEmail("");
		baggerOrganization.setContact(contact);
		if (bagInfoTxt.getSourceOrganization() != null && !bagInfoTxt.getSourceOrganization().isEmpty()) 
    		baggerOrganization.setOrgName(bagInfoTxt.getSourceOrganization());
		else
    		baggerOrganization.setOrgName("");
		if (bagInfoTxt.getOrganizationAddress() != null && !bagInfoTxt.getOrganizationAddress().isEmpty()) 
    		baggerOrganization.setOrgAddress(bagInfoTxt.getOrganizationAddress());
		else
    		baggerOrganization.setOrgAddress("");
		this.bagInfo.setBagOrganization(baggerOrganization);
		if (bagInfoTxt.getExternalDescription() != null && !bagInfoTxt.getExternalDescription().isEmpty())
			this.bagInfo.setExternalDescription(bagInfoTxt.getExternalDescription());
		else
			this.bagInfo.setExternalDescription("");
		if (bagInfoTxt.getBaggingDate() != null && !bagInfoTxt.getBaggingDate().isEmpty())
			this.bagInfo.setBaggingDate(bagInfoTxt.getBaggingDate());
		else
			this.bagInfo.setBaggingDate(DefaultBagInfo.getTodaysDate());
		if (bagInfoTxt.getExternalIdentifier() != null && !bagInfoTxt.getExternalIdentifier().isEmpty())
			this.bagInfo.setExternalIdentifier(bagInfoTxt.getExternalIdentifier());
		else
			this.bagInfo.setExternalIdentifier("");
		if (bagInfoTxt.getBagSize() != null && !bagInfoTxt.getBagSize().isEmpty())
			this.bagInfo.setBagSize(bagInfoTxt.getBagSize());
		else
			this.bagInfo.setBagSize("");
		if (bagInfoTxt.getPayloadOxum() != null && !bagInfoTxt.getPayloadOxum().isEmpty())
			this.bagInfo.setPayloadOxum(bagInfoTxt.getPayloadOxum());
		else
			this.bagInfo.setPayloadOxum("");
		if (bagInfoTxt.getBagGroupIdentifier() != null && !bagInfoTxt.getBagGroupIdentifier().isEmpty())
			this.bagInfo.setBagGroupIdentifier(bagInfoTxt.getBagGroupIdentifier());
		else
			this.bagInfo.setBagGroupIdentifier("");
		if (bagInfoTxt.getBagCount() != null && !bagInfoTxt.getBagCount().isEmpty())
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
		if (bagInfoTxt.containsKey(DefaultBagInfo.EDEPOSIT_PUBLISHER)) {
			String publisher = bagInfoTxt.get(DefaultBagInfo.EDEPOSIT_PUBLISHER);
			if (publisher != null && !publisher.isEmpty()) {
				this.bagInfo.setPublisher(publisher);
			} else {
				this.bagInfo.setPublisher("");
			}
			this.setIsEdeposit(true);
		}
		if (bagInfoTxt.containsKey(DefaultBagInfo.NDNP_AWARDEE_PHASE)) {
			String awardeePhase = bagInfoTxt.get(DefaultBagInfo.NDNP_AWARDEE_PHASE);
			if (awardeePhase != null && !awardeePhase.isEmpty()) {
				this.bagInfo.setAwardeePhase(awardeePhase);
			} else {
				this.bagInfo.setAwardeePhase("");
			}
			this.setIsNdnp(true);
		}
	}

	public void copyFormToBag(Bag bag) {
		BaggerOrganization baggerOrganization = this.bagInfo.getBagOrganization();
		Contact contact = baggerOrganization.getContact();
		if (bag.getBagInfoTxt() != null) {
			bag.getBagInfoTxt().setSourceOrganization(baggerOrganization.getOrgName());
			bag.getBagInfoTxt().setOrganizationAddress(baggerOrganization.getOrgAddress());
			bag.getBagInfoTxt().setContactName(contact.getContactName());
			bag.getBagInfoTxt().setContactPhone(contact.getTelephone());
			bag.getBagInfoTxt().setContactEmail(contact.getEmail());
			bag.getBagInfoTxt().setExternalDescription(bagInfo.getExternalDescription());
			bag.getBagInfoTxt().setBaggingDate(bagInfo.getBaggingDate());
			bag.getBagInfoTxt().setExternalIdentifier(bagInfo.getExternalIdentifier());
			bag.getBagInfoTxt().setBagSize(bagInfo.getBagSize());
			bag.getBagInfoTxt().setPayloadOxum(bagInfo.getPayloadOxum());
			bag.getBagInfoTxt().setBagGroupIdentifier(bagInfo.getBagGroupIdentifier());
			bag.getBagInfoTxt().setBagCount(bagInfo.getBagCount());
			bag.getBagInfoTxt().setInternalSenderIdentifier(bagInfo.getInternalSenderIdentifier());
			bag.getBagInfoTxt().setInternalSenderDescription(bagInfo.getInternalSenderDescription());			
			if (this.getIsEdeposit()) {
				bag.getBagInfoTxt().put(DefaultBagInfo.EDEPOSIT_PUBLISHER, bagInfo.getPublisher());
			}
			if (this.getIsNdnp()) {
				bag.getBagInfoTxt().put(DefaultBagInfo.NDNP_AWARDEE_PHASE, bagInfo.getAwardeePhase());		
			}
		}
		// TODO: complete bag when is has changed
        bag.complete();
	}

	public void setInfo(DefaultBagInfo bagInfo) {
        String bagName = bagInfo.getBagName();
        if (bagName == null || bagName.trim().length() == 0 || bagName.trim().equalsIgnoreCase("null")) {
        	this.setName(this.bagInfo.getName());
        } else {
            this.setName(bagName);
        }
		this.bagInfo.copy(bagInfo);
		this.bagInfo = bagInfo;
		this.copyFormToBag(this.bilBag);
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

	// TODO: Bagger currently only supports one base URL location per bag
	public String getBaseUrl(FetchTxt fetchTxt) {
		String httpToken = "http:\\/\\/";
		String delimToken = "\\/";
		String baseUrl = "";
		if (fetchTxt != null) {
			if (!fetchTxt.isEmpty()) {
    			FilenameSizeUrl fsu = fetchTxt.get(0);
    			if (fsu != null) {
    				String url = fsu.getUrl();
    				String[] list = url.split(httpToken);
    				if (list != null && list.length > 1) {
    					String urlSuffix = list[1];
    					String[] hostUrl = urlSuffix.split(delimToken);
    					if (hostUrl != null && hostUrl.length > 1) {
            				baseUrl = "http://" + hostUrl[0];
    					}
    				}
    			}
			}
		}
		return baseUrl;
	}

	public void updateFetch() {
		if (this.getIsHoley()) {
			if (this.fetch != null && this.fetch.getBaseURL() != null) {
				String baseUrl = this.fetch.getBaseURL();
				this.bilBag.makeHoley(baseUrl, true);
			}
		} else {
			this.bilBag.putFetchTxt(null);
		}
	}

	public void setFetch(BaggerFetch fetch) {
		this.fetch = fetch;
	}

	public BaggerFetch getFetch() {
		if (this.fetch == null) this.fetch = new BaggerFetch();
		return this.fetch;
	}
	
	public String getFetchContent() {
		StringBuffer fcontent = new StringBuffer();
		FetchTxt fetchTxt = this.bilBag.getFetchTxt();
		if (fetchTxt != null) {
			for (int i=0; i < fetchTxt.size(); i++) {
				FilenameSizeUrl fetch = fetchTxt.get(i);
				String s = fetch.toString();
				fcontent.append(s);
			}
		}
		return fcontent.toString();
	}
	
	public String getBagItContent() {
		StringBuffer bcontent = new StringBuffer();
        if (this.bilBag.getBagItTxt() != null) {
    		bcontent.append(BagItTxtImpl.VERSION_KEY + ": ");
    		bcontent.append(bilBag.getBagItTxt().getVersion() + "\n");
    		bcontent.append(BagItTxtImpl.CHARACTER_ENCODING_KEY + ": ");
    		bcontent.append(bilBag.getBagItTxt().getCharacterEncoding() + "\n");
        } else {
        	bcontent.append("getBagItTxt is NULL" + "\n");
        }
		return bcontent.toString();
	}
	
	public String getManifestContent() {
    	StringBuffer mcontent = new StringBuffer();
    	List<Manifest> manifests = this.bilBag.getPayloadManifests();
    	for (int i=0; i < manifests.size(); i++) {
    		Manifest manifest = manifests.get(i);
    		mcontent.append("\n");
    		mcontent.append(manifest.getFilepath());
    		mcontent.append(manifest.toString());
    		mcontent.append("\n");
    	}
    	return mcontent.toString();
	}
	
	public String getTagManifestContent() {
    	StringBuffer tmcontent = new StringBuffer();
    	List<Manifest> manifests = this.bilBag.getTagManifests();
    	for (int i=0; i < manifests.size(); i++) {
    		Manifest manifest = manifests.get(i);
    		tmcontent.append("\n");
    		tmcontent.append(manifest.getFilepath());
    		tmcontent.append(manifest.toString());
    		tmcontent.append("\n");
    	}
    	return tmcontent.toString();
	}
	
	public String getDataContent() {
		totalSize = 0;
		StringBuffer dcontent = new StringBuffer();
		dcontent.append(this.getDataDirectory() + "/");
		dcontent.append('\n');
		Collection<BagFile> files = this.bilBag.getPayloadFiles();
        for (Iterator<BagFile> it=files.iterator(); it.hasNext(); ) {
        	try {
            	BagFile bf = it.next();
            	if (bf != null) {
                	totalSize += bf.getSize();
                	dcontent.append(bf.getFilepath());            		
            	}
            	dcontent.append('\n');
        	} catch (Exception e) {
        		log.error("DefaultBag.getDataContent: " + e.getMessage());
        	}
        }
        this.setSize(totalSize);
		return dcontent.toString();
	}
	
	public long getDataSize() {
		return this.totalSize;
	}
	
	public int getDataNumber() {
		return this.bilBag.getPayloadFiles().size();
	}
	
	public void setProject(Project project) {
		this.project = project;
	}

	public Project getProject() {
		return this.project;
	}

	public Collection<BagFile> getRootPayload() {
		return this.rootPayload;
	}
	
	public List<String> getRootPayloadPaths() {
		ArrayList<String> pathList = new ArrayList<String>();
		Collection<BagFile> payload = this.getRootPayload();
        for (Iterator<BagFile> it=payload.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
        	pathList.add(bf.getFilepath());
        }
		return pathList;
	}
	
	public List<String> getPayloadPaths() {
		ArrayList<String> pathList = new ArrayList<String>();
		Collection<BagFile> payload = this.bilBag.getPayloadFiles();
        for (Iterator<BagFile> it=payload.iterator(); it.hasNext(); ) {
        	BagFile bf = it.next();
        	pathList.add(bf.getFilepath());
        }
		return pathList;		
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
	
	public String write(boolean validFormFields, File path) {
		boolean isContinue = true;
		String messages = "";
		reset();
		try {
			// validate form
			if (isContinue) {
				messages += validateForms(validFormFields);
				if (this.isValidForms()) {
					isContinue = true;
				} else {
					if (this.getIsEdeposit()) isContinue = false;
					messages += "\nBagger form fields are missing valid values.\n";
				}
				display("DefaultBag.write isValidForms: " + messages);
			}
			// is complete
			if (isContinue) {
				messages += completeBag();
				if (this.isComplete()) {
					isContinue = true;
				} else {
					isContinue = false;
					messages += "\nBag is not complete.\n";
				}
				display("DefaultBag.write isComplete: " + messages);
			}
			// write bag
			if (isContinue) {
				//this.bilBag.complete();
				messages += writeBag();
			}
		} catch (Exception e) {
			messages += "An error occurred writing the bag:\n" + e.toString() + "\n";
			e.printStackTrace();
			log.error(e.getMessage());
		}
		return messages;
	}

	public String validateForms(boolean b) {
		String messages = "";

		this.isValidForms(b);
		if (b) {
			if (this.getIsHoley()) {
				String baseURL = null;
				FetchTxt fetchTxt = bilBag.getFetchTxt();
				if (fetchTxt != null && !fetchTxt.isEmpty()) {
					FilenameSizeUrl filenameSizeUrl = fetchTxt.get(0);
					baseURL = filenameSizeUrl.getUrl();
					if (baseURL == null || baseURL.trim().length() == 0) {
						this.isValidForms(false);
						messages += "A holey bag requires a base URL.\n";
					}
				}
			}
			if (this.isEdeposit) {
				String publisher = this.bagInfo.getPublisher();
				if (publisher == null || publisher.trim().length() == 0) {
					this.isValidForms(false);
					messages += "An Edeposit bag requires a publisher.\n";
				}
			}
			if (this.isNdnp) {
				String awardeePhase = this.bagInfo.getAwardeePhase();
				if (awardeePhase == null || awardeePhase.trim().length() == 0) {
					this.isValidForms(false);
					messages += "An NDNP bag requires an awardee phase.\n";
				}
			}
		}
		return messages;
	}

	public String completeBag() {
		String messages = "";
		try {
			SimpleResult result = this.bilBag.isComplete();
			if (result.messagesToString() != null) messages += result.messagesToString();
			this.isComplete(result.isSuccess());
		} catch (Exception e) {
			this.isComplete(false);
			e.printStackTrace();
			messages += "Bag is not complete: " + e.getMessage() + "\n";
		}
		return messages;
	}
	
	public String validateMetadata() {
		String messages = "";
		try {
			if (bagStrategy == null) updateStrategy();
			SimpleResult result = this.bilBag.additionalVerify(bagStrategy);
			if (result.messagesToString() != null) messages += result.messagesToString();
			this.isValidMetadata(result.isSuccess());
		} catch (Exception e) {
			this.isValidMetadata(false);
			messages += "Bag-info fields are not correct: " + e.getMessage() + "\n";
			e.printStackTrace();
		}
		return messages;
	}

	public String validateBag(Bag bag) {
		String messages = "";
		display("validateBag");
		bagToValidate = bag;
		try {
	    	if (this.getDataSize() > MAX_SIZE) {
	    		confirmValidateBag();
	    	} else {
				SimpleResult result = bag.isValid();
				if (result.messagesToString() != null) messages += result.messagesToString();
				this.isValid(result.isSuccess());
	    	}
		} catch (Exception e) {
			this.isValid(false);
			e.printStackTrace();
			messages += "Bag is not valid: " + e.getMessage() + "\n";
		}
		return messages;
	}
	
	public String writeBag() {
		String messages = "";
		String bagName = "";
		File bagFile = null;
		BagWriter bw = null;
		try {
			if (this.serialMode == NO_MODE) {
				this.isSerialized(true);
				bagName = this.getName();
				bagFile = new File(getRootDir(), bagName);
				bw = new FileSystemBagWriter(bagFile, true);				
				if (this.isCleanup) { messages += cleanup(); }
				messages += "Successfully created bag: " + this.getInfo().getBagName() + "\n";
			} else if (this.serialMode == ZIP_MODE) {
				this.isSerialized(true);
				bagName = this.getName() + ".zip";
				bagFile = new File(getRootDir(), bagName);
				bw = new ZipBagWriter(bagFile);
				String zipName = bagFile.getName();
				long zipSize = this.getSize() / MB;
				messages += "Successfully created serialized file: " + zipName + " of size: " + zipSize + "(MB)\n";
				if (zipSize > 100) {
					messages += "WARNING: You may not be able to network transfer files > 100 MB!\n";
				}
			} else if (this.serialMode == TAR_MODE) {
				this.isSerialized(true);
				bagName = this.getName() + ".tar";
				bagFile = new File(getRootDir(), bagName);
				bw = new TarBagWriter(bagFile);
				String zipName = bagFile.getName();
				long zipSize = this.getSize() / MB;
				messages += "Successfully created serialized file: " + zipName + " of size: " + zipSize + "(MB)\n";
				if (zipSize > 100) {
					messages += "WARNING: You may not be able to network transfer files > 100 MB!\n";
				}
			}
			this.bilBag.write(bw);
			this.setIsNewbag(false);

			try {
				// read bag
				log.info("DefaultBag.writeBag-createBag: " + bagFile);
				Bag bag = BagFactory.createBag(bagFile);

				/* */
				// is valid metadata
				messages += validateMetadata();
				display("DefaultBag.write isValidMetadata: " + messages);
				if (this.isValidMetadata()) {
				} else {
					messages += "\nBag-info fields are not all present for the project selected.\n";
				}
				/* */
				// is valid bag
				messages += validateBag(bag);
				display("DefaultBag.write isValid: " + messages);
				if (this.isValid()) {
				} else {
					messages += "\nBag is not valid.\n";
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				messages += "ERROR validating bag: " + bagFile + "\n" + ex.getMessage() + "\n";
			}
		} catch (Exception e) {
			this.isSerialized(false);
			e.printStackTrace();
			messages += "ERROR creating bag: " + bagFile + "\n" + e.getMessage() + "\n";
		}
		return messages;
	}
	
	private String cleanup() {
		boolean b = false;
		String messages = "";
		if (this.getIsSerial()) {
			display("cleanup");
			if (!this.getIsHoley()) {
				b = FileUtililties.deleteDir(this.getRootDir());
				if (!b) messages += "Could not delete directory: " + this.getRootDir() + "\n";
				else messages += "Cleaning up bag directory.\n";
			}
			this.getRootDir().deleteOnExit();
		}
		return messages;
	}
	
	public void updateStrategy() {
		bagStrategy = getBagInfoStrategy();		
	}

	protected VerifyStrategy getBagInfoStrategy() {
		List<String> rulesList = new ArrayList<String>();
		rulesList.add(DefaultBagInfo.SOURCE_ORGANIZATION);
		rulesList.add(DefaultBagInfo.ORGANIZATION_ADDRESS);
		rulesList.add(DefaultBagInfo.CONTACT_NAME);
		rulesList.add(DefaultBagInfo.CONTACT_PHONE);
		rulesList.add(DefaultBagInfo.CONTACT_EMAIL);
		rulesList.add(DefaultBagInfo.EXTERNAL_DESCRIPTION);
		rulesList.add(DefaultBagInfo.BAGGING_DATE);
		rulesList.add(DefaultBagInfo.EXTERNAL_IDENTIFIER);
		rulesList.add(DefaultBagInfo.BAG_SIZE);
		if (this.getIsEdeposit()) rulesList.add(DefaultBagInfo.EDEPOSIT_PUBLISHER);
		if (this.getIsNdnp()) rulesList.add(DefaultBagInfo.NDNP_AWARDEE_PHASE);
		String[] rules = new String[rulesList.size()];
		for (int i=0; i< rulesList.size(); i++) rules[i] = new String(rulesList.get(i));

		VerifyStrategy strategy = new RequiredBagInfoTxtFieldsStrategy(rules);

		return strategy;
	}

	private void confirmValidateBag() {
	    ConfirmationDialog dialog = new ConfirmationDialog() {
	        protected void onConfirm() {
	        	BusyIndicator.showAt(Application.instance().getActiveWindow().getControl());
				SimpleResult result = bagToValidate.isValid();
				isValid(result.isSuccess());
		    	BusyIndicator.clearAt(Application.instance().getActiveWindow().getControl());
	        }
	    };

	    dialog.setCloseAction(CloseAction.DISPOSE);
	    dialog.setTitle("Validate Bag");
	    dialog.setConfirmationMessage("The contents of this bag are larger than 100 MB; this may cause performance problems.  Would you like to continue validation?");
	    dialog.showDialog();
	}
}
