package gov.loc.repository.bagger.bag;

import java.util.List;
import java.util.Date;
import java.io.File;
import java.io.IOException;

import javax.swing.JTree;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.NamedEntity;
import gov.loc.repository.bagger.util.FileUtililties;

import gov.loc.repository.bagit.bag.BagGeneratorVerifier;
import gov.loc.repository.bagit.bag.BagHelper;
import gov.loc.repository.bagit.bag.impl.BagGeneratorVerifierImpl;
import gov.loc.repository.bagit.manifest.impl.FixityGeneratorManifestGeneratorVerifier;
import gov.loc.repository.bagit.manifest.impl.JavaSecurityFixityGenerator;
import gov.loc.repository.bagit.utilities.SimpleResult;

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
public class Bag extends NamedEntity {
	private static final Log log = LogFactory.getLog(Bag.class);

	private Date createDate;
	private File rootSrc;
	private File rootDir;

	private Project project;
	private List<Manifest> manifests;
	private List<TagManifest> tagManifests = null;
	private Fetch fetch;
	private BagIt bagIt;
	private BagItInfo info;
	private Data data;
	
	private BagGeneratorVerifier verifier = new BagGeneratorVerifierImpl(new FixityGeneratorManifestGeneratorVerifier(new JavaSecurityFixityGenerator()));
	private boolean isHoley = false;	
	private boolean isFetch = false;	
	private boolean isComplete = false;	
	private boolean isValid = false;	
	private boolean isSerialized = false;
	private boolean isCopyright = false;

	public Bag() {
		super();
	}

	public Bag(JTree tree) {
		super();
		data = new Data();
		data.setData(tree);
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

	public void setManifests(List<Manifest> manifests) {
		this.manifests = manifests;
	}

	public List<Manifest> getManifests() {
		return this.manifests;
	}

	public void addManifest(Manifest manifest) {
		manifests.add(manifest);
	}
	
	public void setTagManifests(List<TagManifest> tagManifests) {
		this.tagManifests = tagManifests;
	}
	
	public List<TagManifest> getTagManifests() {
		return this.tagManifests;
	}
	
	public void addTagManifest(TagManifest tagManifest) {
		tagManifests.add(tagManifest);
	}

	public void setFetch(Fetch fetch) {
		this.fetch = fetch;
	}

	public Fetch getFetch() {
		return this.fetch;
	}

	public void setBagIt(BagIt bagIt) {
		this.bagIt = bagIt;
	}

	public BagIt getBagIt() {
		return this.bagIt;
	}

	public void setInfo(BagItInfo bagItInfo) {
		this.info = bagItInfo;
		this.setName(bagItInfo.getBagName());
	}

	public BagItInfo getInfo() {
		return this.info;
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
	
	public void initialize() {
		// TODO: Load bag information from persisted storage
	}
	
	public String validate() {
		String messages = null;

		SimpleResult result = verifier.isComplete(this.rootDir);
		messages = "Is Complete? \n" + result.getMessage();
		this.isComplete = result.isSuccess();
		messages += "\n";
		messages += "Is Valid? \n";
		if (this.isComplete) {
			result = verifier.isValid(this.rootDir);
			messages += result.getMessage();
			this.isValid = result.isSuccess();
		} else {
			messages += "Bag is not complete.";
		}
		messages += "\n";

		return messages;
	}
	
	public String write(File path) {
		String errorMessages = null;
		boolean isComplete = false;
		boolean isValid = false;
		boolean success = false;

		try {
			// create and open bag name directory
			if (path.getAbsolutePath() == null || this.getName() == null) {
		    	errorMessages = reportError(errorMessages, "BagView.write failed to create directory: NULL");
				log.error(errorMessages);
				return errorMessages;			
			}
//			display("Bag.writePath: " + path.getParent());
			display("Bag.writePath: " + path.getAbsolutePath() + "/" + this.getName());
			File rootDir = new File(path.getAbsolutePath(), this.getName());
			success = rootDir.mkdir();
		    if (!success) {
		    	errorMessages = reportError(errorMessages, "BagView.write failed to create directory: " + rootDir);
				log.error(errorMessages);
				return errorMessages;
		    }
			this.setRootDir(rootDir);
			// create and write manifest-<type>.txt in bag name directory
	    	for (int i=0; i < manifests.size(); i++) {
	    		Manifest manifest = manifests.get(i);
	    		manifest.writeData();
	    		manifest.write(rootDir);
	    	}
	    	if (this.isHoley) {
	    		// create and write fetch.txt in bag name directory
	    	    fetch.setName(BagHelper.FETCH);
	    	    fetch.writeData();
	    	    fetch.write(rootDir);    		
	    	}
			// create and write bag-info.txt in bag name directory
			info.setName(BagHelper.INFO);
			info.writeData();
			info.write(rootDir);
			// create and write bagit.txt in bag name directory
			bagIt.setName(BagHelper.BAGIT);
			bagIt.writeData();
			bagIt.write(rootDir);
			// create and write tagmanifest-<type>.txt in bag name directory
	    	for (int i=0; i < tagManifests.size(); i++) {
	    		TagManifest tagManifest = tagManifests.get(i);
	    		tagManifest.setType(ManifestType.MD5);
	    		tagManifest.writeData();
	    		tagManifest.write(rootDir);
	    		tagManifests.set(i, tagManifest);
	    	}
	    	this.setTagManifests(tagManifests);
			// create and open data directory
			File dataDir = new File(rootDir, BagHelper.DATA_DIRECTORY);
			success = dataDir.mkdir();
		    if (!success) {
		    	errorMessages = reportError(errorMessages, "ERROR in BagView.write failed to create directory: " + dataDir);
				log.error(errorMessages);
		    }
			// create and write data directory
			//List<File> fileList = data.getFiles();
			File parent = this.getRootSrc();
			
			try
			{
				display("Bag.write copyFiles: " + parent.getAbsolutePath() + " to: " + dataDir.getAbsolutePath());
				FileUtililties.copyFiles(parent, dataDir);
			}
			catch(IOException e)
			{
		    	errorMessages = reportError(errorMessages, "ERROR in BagView.write copyFiles: " + e.getMessage());
		    	log.error(errorMessages);
			}

			SimpleResult result = verifier.isComplete(rootDir);
			isComplete = result.isSuccess();
			this.isComplete = isComplete;
			display("Bag.write isComplete: " + isComplete);
			if (errorMessages == null && isComplete) {
				result = verifier.isValid(rootDir);
				isValid = result.isSuccess();
				this.isValid = isValid;
				display("Bag.write isValid: " + isValid);
				if (isValid) {
					String msg = null;
					// Create a  zip file for serialized transfer of the bag
					msg = FileUtililties.createZip(rootDir);
					if (msg == null) {
						// Clean up the files since bag zip is created
						this.isSerialized = true;
						boolean b = FileUtililties.deleteDir(rootDir);
						if (!b) reportError(errorMessages, "Error deleting directory: " + rootDir);					
					} else {
						reportError(errorMessages, msg);					
					}				
				} else {
					errorMessages = reportError(errorMessages, result.getMessage());
				}
			} else {
				errorMessages = reportError(errorMessages, result.getMessage());
			}
		} catch (Exception e) {
			errorMessages += "\n" + "Exception while creating bag:\n" + e.getMessage();
			log.error(e.getMessage());
		}
		return errorMessages;
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
