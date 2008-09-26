package gov.loc.repository.bagger.bag;

import java.util.List;
import java.util.Date;
import java.io.File;
import java.io.IOException;

import javax.swing.JTree;
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
        |      bagit-info.txt
        \--- data/
              |   [optional file hierarchy]
  *
 * @author Jon Steinbach
 */
public class Bag extends NamedEntity {
	private static final Log log = LogFactory.getLog(Bag.class);

	private Date createDate;
	private File rootSrc;

	private List<Manifest> manifests;
	private Fetch fetch;
	private BagIt bagIt;
	private BagItInfo info;
	private Data data;
	
	private boolean isHoley = false;	
	private boolean isFetch = false;	
	private boolean isComplete = false;	
	private boolean isValid = false;	
	private boolean isSerialized = false;

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

	public void setRootSrc(File rootSrc) {
		this.rootSrc = rootSrc;
	}
	
	public File getRootSrc() {
		return this.rootSrc;
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

	public void setIsComplete(boolean b) {
		this.isComplete = b;
	}
	
	public boolean getIsComplete() {
		return this.isComplete;
	}
	
	public void initialize() {
		// TODO: Load bag information from persisted storage
	}
	
	public String write(File path) {
		String errorMessages = null;
		boolean isComplete = false;
		boolean isValid = false;

		// create and open bag name directory
		display("Bag.writePath: " + path.getParent());
		File rootDir = new File(path.getAbsolutePath());
		boolean success = rootDir.mkdir();
	    if (!success) {
	    	errorMessages = reportError(errorMessages, "BagView.write failed to create directory: " + rootDir);
			log.error(errorMessages);
	    }
		// create and write manifest-<type>.txt in bag name directory
    	for (int i=0; i < manifests.size(); i++) {
    		Manifest manifest = manifests.get(i);
    		manifest.writeData();
    		manifest.write(rootDir);
    	}
		// create and write fetch.txt in bag name directory
	    fetch.setName(BagHelper.FETCH);
	    fetch.writeData();
	    fetch.write(rootDir);
		// create and write bagit-info.txt in bag name directory
		info.setName(BagHelper.INFO);
		info.writeData();
		info.write(rootDir);
		// create and write bagit.txt in bag name directory
		bagIt.setName(BagHelper.BAGIT);
		bagIt.writeData();
		bagIt.write(rootDir);
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

		BagGeneratorVerifier verifier = new BagGeneratorVerifierImpl(new FixityGeneratorManifestGeneratorVerifier(new JavaSecurityFixityGenerator()));
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
