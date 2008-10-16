package gov.loc.repository.bagger.bag;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.FileEntity;

/**
 * Simple JavaBean domain object representing bagit information.
 *
|   bag-info.txt
|    (Source-Organization: California Digital Library                      )
|    (Organization-Address: 415 20th Street, 4th Floor, Oakland, CA. 94612 )
|    (Contact-Name: A. E. Newman                                           )
|    (Contact-Phone: +1 510-555-1234                                       )
|    (Contact-Email: alfred@ucop.edu                                       )
|    (External-Description: The collection "Local Davis Flood Control      )
|      Collection" includes captured California State and local websites   )
|      containing information on flood control resources for the Davis and )
|      Sacramento area.  Sites were captured by UC Davis curator Wrigley   )
|      Spyder using the Web Archiving Service in February 2007 and         )
|      October 2007.                                                       )
|    (Packing-Date: 2008.04.15                                             )
|    (External-Identifier: ark:/13030/fk4jm2bcp                            )
|    (Package-Size: 260 GB                                                 )
|    (Bag-Group-Identifier: spengler_yoshimuri                             )
|    (Bag-Count: 1 of 15                                                   )
|    (Internal-Sender-Identifier: UCDL                                     )
|    (Internal-Sender-Description: University of California Davis Libraries)
 *
 *
 * @author Jon Steinbach
 */
public class BagInfo extends FileEntity {
	private static final Log log = LogFactory.getLog(BagInfo.class);

	private Bag bag;
	private String bagName = new String("bag_1");
	private BagOrganization bagOrganization = new BagOrganization();
	private String externalDescription = "";
	private String baggingDate = ""; // YYYY-MM-DD
	private String externalIdentifier = "";
	private String bagSize = "";
	private String payloadOssum = "";
	private String bagGroupIdentifier = "";
	private String bagCount = "1 of 1";
	private String internalSenderIdentifier = "";
	private String internalSenderDescription = "";
	private String publisher = "";
	private String awardeePhase = "";
	private boolean isCopyright = false;
	private boolean isNdnp = false;
	public HashMap<String,String> bagInfoRules;

	public BagInfo(Bag bag) {
		super();
		this.bag = bag;
		this.setIsCopyright(bag.getIsCopyright());
		bagInfoRules = initRules();
	}
	
	public void setIsCopyright(boolean b) {
		this.isCopyright = b;
	}
	
	public boolean getIsCopyright() {
		return this.isCopyright;
	}
	
	public void setIsNdnp(boolean b) {
		this.isNdnp = b;
	}
	
	public boolean getIsNdnp() {
		return this.isNdnp;
	}
	
	public void setRules(HashMap<String,String> rules) {
		this.bagInfoRules = rules;
	}
	
	public HashMap<String,String> getRules() {
		return this.bagInfoRules;
	}
	
	public void setBagName(String name) {
		this.bagName = name;
	}
	
	public String getBagName() {
		return this.bagName;
	}

	public void setBagOrganization(BagOrganization bagOrganization) {
	    String pattern = "yyyy-MM-dd";
	    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date todaysDate = new Date();
		this.baggingDate = formatter.format(todaysDate);
		this.bagOrganization = bagOrganization;
		this.bagOrganization.setOrgName(bagOrganization.getOrgName());
		this.bagOrganization.setOrgAddress(bagOrganization.getOrgAddress());
		this.bagOrganization.getContact().setContactName(bagOrganization.getContact().getContactName());
		this.bagOrganization.getContact().setTelephone(bagOrganization.getContact().getTelephone());
		this.bagOrganization.getContact().setEmail(bagOrganization.getContact().getEmail());
	}

	public BagOrganization getBagOrganization() {
		return this.bagOrganization;
	}

	public void setExternalDescription(String description) {
		this.externalDescription = description;
	}

	public String getExternalDescription() {
		return this.externalDescription;
	}

	public void setBaggingDate(String bagDate) {
		this.baggingDate = bagDate;
	}

	public String getBaggingDate() {
		return this.baggingDate;
	}

	public void setExternalIdentifier(String externalIdentifier) {
		this.externalIdentifier = externalIdentifier;
	}

	public String getExternalIdentifier() {
		return this.externalIdentifier;
	}

	public void setBagSize(String bagSize) {
		this.bagSize = bagSize;
	}

	public String getBagSize() {
		return this.bagSize;
	}
	
	/* 
	 * The "octetstream sum" of the payload, namely, a two-part number of the form "OctetCount.StreamCount", 
	 * where OctetCount is the total number of octets (8-bit bytes) across all payload file content and 
	 * StreamCount is the total number of payload files. Payload-Ossum is easy to compute 
	 * (e.g., on Unix "wc -lc `find data/ -type f`" does the hard part) and should be included in 
	 * "bag-info.txt" if at all possible. Compared to Bag-Size (above), Payload-Ossum is more intended for 
	 * machine consumption. 
	 */
	public void setPayloadOssum(String ossum) {
		this.payloadOssum = ossum;
	}
	
	public String getPayloadOssum() {
		return this.payloadOssum;
	}

	public void setBagGroupIdentifier(String bagGroupIdentifier) {
		this.bagGroupIdentifier = bagGroupIdentifier;
	}

	public String getBagGroupIdentifier() {
		return this.bagGroupIdentifier;
	}

	public void setBagCount(String bagCount) {
		this.bagCount = bagCount;
	}

	public String getBagCount() {
		return this.bagCount;
	}

	public void setInternalSenderIdentifier(String internalSenderIdentifier) {
		this.internalSenderIdentifier = internalSenderIdentifier;
	}

	public String getInternalSenderIdentifier() {
		return this.internalSenderIdentifier;
	}

	public void setInternalSenderDescription(String internalSenderDescription) {
		this.internalSenderDescription = internalSenderDescription;
	}

	public String getInternalSenderDescription() {
		return this.internalSenderDescription;
	}
	
	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}
	
	public String getPublisher() {
		return this.publisher;
	}
	
	public void setAwardeePhase(String phase) {
		this.awardeePhase = phase;
	}
	
	public String getAwardeePhase() {
		return this.awardeePhase;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Source-Organization: ");
		sb.append(bagOrganization.getOrgName());
		sb.append('\n');
		sb.append("Organization-Address: ");
		sb.append(bagOrganization.getOrgAddress());
		sb.append('\n');
		sb.append("Contact-Name: ");
		sb.append(bagOrganization.getContact().getContactName());
		sb.append('\n');
		sb.append("Contact-Phone: ");
		sb.append(bagOrganization.getContact().getTelephone());
		sb.append('\n');
		sb.append("Contact-Email: ");
		sb.append(bagOrganization.getContact().getEmail());
		sb.append('\n');
		sb.append("External-Description: ");
		sb.append(this.externalDescription);
		sb.append('\n');
		sb.append("Bagging-Date: ");
		sb.append(this.baggingDate);
		sb.append('\n');
		sb.append("External-Identifier: ");
		sb.append(this.externalIdentifier);
		sb.append('\n');
		sb.append("Bag-Size: ");
		sb.append(this.bagSize);
		sb.append('\n');
		sb.append("Payload-Ossum: ");
		sb.append(this.payloadOssum);
		sb.append('\n');
		sb.append("Bag-Group-Identifier: ");
		sb.append(this.bagGroupIdentifier);
		sb.append('\n');
		sb.append("Bag-Count: ");
		sb.append(this.bagCount);
		sb.append('\n');
		sb.append("Internal-Sender-Identifier: ");
		sb.append(this.internalSenderIdentifier);
		sb.append('\n');
		sb.append("Internal-Sender-Description: ");
		sb.append(this.internalSenderDescription);
		sb.append('\n');
		sb.append("Publisher: ");
		sb.append(this.publisher);
		sb.append('\n');
		sb.append("Awardee Phase: ");
		sb.append(this.awardeePhase);
		sb.append('\n');

		return sb.toString();
	}

	public void writeData() {
		this.fromString(toString());
	}
	
	public HashMap<String,String> initRules() {
		HashMap<String,String> rules = new HashMap<String,String>();
		rules.put("Source-Organization", "required");
		rules.put("Organization-Address", "required");
		rules.put("Contact-Name", "required");
		rules.put("Contact-Phone", "required");
		rules.put("Contact-Email", "required,[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,4}");
		rules.put("External-Description", "required");
		rules.put("Bagging-Date", "required,(19|20)\\d\\d[- /.](0[1-9]|1[012])[- /.](0[1-9]|[12][0-9]|3[01])");
		rules.put("External-Identifier", "required");
		rules.put("Bag-Size", "required");
		rules.put("Payload-Ossum", "required");
		rules.put("Bag-Group-Identifier", "");
		rules.put("Bag-Count", "");
		rules.put("Internal-Sender-Identifier", "");
		rules.put("Internal-Sender-Description", "");
		if (bag.getIsCopyright()) {
			rules.put("Publisher", "required");
		} else {
			rules.put("Publisher", "");
		}
		rules.put("Awardee Phase", "");
		
		return rules;
	}
}
