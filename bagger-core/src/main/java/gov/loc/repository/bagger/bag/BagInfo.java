package gov.loc.repository.bagger.bag;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagit.impl.AbstractBagConstants;
import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

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
public class BagInfo extends BagInfoTxtImpl {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(BagInfo.class);

	private BaggerBag baggerBag;
	private String bagName = new String("bag_");
	private BagOrganization bagOrganization = new BagOrganization();
	private String baggingDate = ""; // YYYY-MM-DD
	private String publisher = "";
	private String awardeePhase = "";
	private boolean isCopyright = false;
	private boolean isNdnp = false;
	public HashMap<String,String> bagInfoRules;
	private String name;
	private String content;

	public BagInfo(BaggerBag baggerBag) {
		super(baggerBag.getBagConstants());
		this.baggerBag = baggerBag;
		setup();
		baggerBag.setName(bagName);
	}
		
	private void setup() {
		this.setBagOrganization(new BagOrganization());
		this.setIsCopyright(baggerBag.getIsCopyright());
		bagInfoRules = initRules();
	    String pattern = "yyyy-MM-dd";
	    SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date todaysDate = new Date();
		this.baggingDate = formatter.format(todaysDate);
		this.setBaggingDate(baggingDate);		
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
		this.setSourceOrganization(bagOrganization.getOrgName());
		this.setOrganizationAddress(bagOrganization.getOrgAddress());
		if (bagOrganization.getContact() == null) {
			this.setContactName("");
			this.setContactPhone("");
			this.setContactEmail("");
		} else {
			this.setContactName(bagOrganization.getContact().getContactName());
			this.setContactPhone(bagOrganization.getContact().getTelephone());
			this.setContactEmail(bagOrganization.getContact().getEmail());
		}
	}

	public BagOrganization getBagOrganization() {
		return this.bagOrganization;
	}

	public void setPublisher(String publisher) {
		if (publisher != null) this.publisher = publisher;
	}
	
	public String getPublisher() {
		return this.publisher;
	}
	
	public void setAwardeePhase(String phase) {
		if (phase != null) this.awardeePhase = phase;
	}
	
	public String getAwardeePhase() {
		return this.awardeePhase;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Source-Organization: ");
		sb.append(this.getSourceOrganization());
		sb.append('\n');
		sb.append("Organization-Address: ");
		sb.append(this.getOrganizationAddress());
		sb.append('\n');
		sb.append("Contact-Name: ");
		sb.append(this.getContactName());
		sb.append('\n');
		sb.append("Contact-Phone: ");
		sb.append(this.getContactPhone());
		sb.append('\n');
		sb.append("Contact-Email: ");
		sb.append(this.getContactEmail());
		sb.append('\n');
		sb.append("External-Description: ");
		sb.append(this.getExternalDescription());
		sb.append('\n');
		sb.append("Bagging-Date: ");
		sb.append(this.getBaggingDate());
		sb.append('\n');
		sb.append("External-Identifier: ");
		sb.append(this.getExternalIdentifier());
		sb.append('\n');
		sb.append("Bag-Size: ");
		sb.append(this.getBagSize());
		sb.append('\n');
		sb.append("Payload-Oxum: ");
		//sb.append(this.getPayloadOxum());
		sb.append(this.getPayloadOssum());
		sb.append('\n');
		sb.append("Bag-Group-Identifier: ");
		sb.append(this.getBagGroupIdentifier());
		sb.append('\n');
		sb.append("Bag-Count: ");
		sb.append(this.getBagCount());
		sb.append('\n');
		sb.append("Internal-Sender-Identifier: ");
		sb.append(this.getInternalSenderIdentifier());
		sb.append('\n');
		sb.append("Internal-Sender-Description: ");
		sb.append(this.getInternalSenderDescription());
		sb.append('\n');
		sb.append("Publisher: ");
		sb.append(this.publisher);
		sb.append('\n');
		sb.append("Awardee Phase: ");
		sb.append(this.awardeePhase);
		sb.append('\n');

		return sb.toString();
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}

	public void setContent(String data) {
		this.content = data;
	}

	public String getContent() {
		return this.content;
	}
	
	public String write(File rootDir) {
		String message = null;
		try
		{
			File file = new File(rootDir, name);
			Writer writer = new OutputStreamWriter(new FileOutputStream(file), AbstractBagConstants.BAG_ENCODING);
			writer.write(this.toString());
			writer.close();
//			this.setFile(file);
		}
		catch(IOException e)
		{
			message = e.getMessage();
			log.error("EXCEPTION: FileEntity.write: " + e.getMessage());
		}
		return message;
	}

	public void writeData() {
		this.getContent();
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
		rules.put("Payload-Ossum", "");
		rules.put("Bag-Group-Identifier", "");
		rules.put("Bag-Count", "");
		rules.put("Internal-Sender-Identifier", "");
		rules.put("Internal-Sender-Description", "");
		if (baggerBag.getIsCopyright()) {
			rules.put("Publisher", "required");
		} else {
			rules.put("Publisher", "");
		}
		rules.put("Awardee Phase", "");
		
		return rules;
	}
}
