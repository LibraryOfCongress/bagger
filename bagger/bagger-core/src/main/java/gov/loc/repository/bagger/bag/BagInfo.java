package gov.loc.repository.bagger.bag;

import java.text.SimpleDateFormat;
import java.util.Date;

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

		return sb.toString();
	}

	public void writeData() {
		this.fromString(toString());
	}
}
