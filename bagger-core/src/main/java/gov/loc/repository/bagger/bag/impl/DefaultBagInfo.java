package gov.loc.repository.bagger.bag.impl;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.bag.BaggerOrganization;

import gov.loc.repository.bagit.impl.BagInfoTxtImpl;

/*
 * @author Jon Steinbach
 */
public class DefaultBagInfo extends BagInfoTxtImpl {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(DefaultBagInfo.class);
	
	public static final String EDEPOSIT_PUBLISHER = "Publisher";
	public static final String NDNP_AWARDEE_PHASE = "Awardee Phase";

	protected DefaultBag baggerBag;
	private String bagName = new String();
	private BaggerOrganization baggerOrganization = new BaggerOrganization();
	
	private String name;
	private String content;
	private String publisher = "";
	private String awardeePhase = "";

	public DefaultBagInfo(DefaultBag baggerBag) {
		super(baggerBag.getBag().getBagConstants());
		this.setBagName(baggerBag.getName());
		this.baggerBag = baggerBag;
		log.debug("DefaultBagInfo");
	}

	public void setBagName(String name) {
		this.bagName = name;
	}
	
	public String getBagName() {
		return this.bagName;
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

	public void setBagOrganization(BaggerOrganization baggerOrganization) {
		this.setSourceOrganization(baggerOrganization.getOrgName());
		this.setOrganizationAddress(baggerOrganization.getOrgAddress());
		this.baggerOrganization.setOrgName(this.getSourceOrganization());
		this.baggerOrganization.setOrgAddress(this.getOrganizationAddress());

		Contact contact = baggerOrganization.getContact();
		this.setContactName(contact.getContactName());
		this.setContactPhone(contact.getTelephone());
		this.setContactEmail(contact.getEmail());
		this.baggerOrganization.getContact().setContactName(this.getContactName());
		this.baggerOrganization.getContact().setTelephone(this.getContactPhone());
		this.baggerOrganization.getContact().setEmail(this.getContactEmail());
	}

	public BaggerOrganization getBagOrganization() {
		return this.baggerOrganization;
	}

	public void copy(DefaultBagInfo bagInfo) {
		BaggerOrganization baggerOrganization = bagInfo.getBagOrganization();
		this.setBagOrganization(baggerOrganization);
		if (bagInfo.getExternalDescription() != null && !bagInfo.getExternalDescription().isEmpty())
			this.setExternalDescription(bagInfo.getExternalDescription());
		else
			this.setExternalDescription("");
		if (bagInfo.getBaggingDate() != null && !bagInfo.getBaggingDate().isEmpty()) {
			this.setBaggingDate(bagInfo.getBaggingDate());
		} else {
			this.setBaggingDate(DefaultBagInfo.getTodaysDate());
		}
		if (bagInfo.getExternalIdentifier() != null && !bagInfo.getExternalIdentifier().isEmpty())
			this.setExternalIdentifier(bagInfo.getExternalIdentifier());
		else
			this.setExternalIdentifier("");
		if (bagInfo.getBagSize() != null && !bagInfo.getBagSize().isEmpty())
			this.setBagSize(bagInfo.getBagSize());
		else
			this.setBagSize("");
		if (bagInfo.getPayloadOxum() != null && !bagInfo.getPayloadOxum().isEmpty())
			this.setPayloadOxum(bagInfo.getPayloadOxum());
		else
			this.setPayloadOxum("");
		if (bagInfo.getBagGroupIdentifier() != null && !bagInfo.getBagGroupIdentifier().isEmpty())
			this.setBagGroupIdentifier(bagInfo.getBagGroupIdentifier());
		else
			this.setBagGroupIdentifier("");
		if (bagInfo.getBagCount() != null && !bagInfo.getBagCount().isEmpty())
			this.setBagCount(bagInfo.getBagCount());
		else
			this.setBagCount("");
		if (bagInfo.getInternalSenderIdentifier() != null && !bagInfo.getInternalSenderIdentifier().equalsIgnoreCase("null"))
			this.setInternalSenderIdentifier(bagInfo.getInternalSenderIdentifier());
		else
			this.setInternalSenderIdentifier("");
		if (bagInfo.getInternalSenderDescription() != null && !bagInfo.getInternalSenderDescription().equalsIgnoreCase("null"))
			this.setInternalSenderDescription(bagInfo.getInternalSenderDescription());
		else
			this.setInternalSenderDescription("");
		if (this.baggerBag.getIsEdeposit()) {
			if (bagInfo.getPublisher() != null && !bagInfo.getPublisher().isEmpty())
				this.setPublisher(bagInfo.getPublisher());
			else
				this.setPublisher("");			
		}
		if (this.baggerBag.getIsNdnp()) {
			if (bagInfo.getAwardeePhase() != null && !bagInfo.getAwardeePhase().isEmpty())
				this.setAwardeePhase(bagInfo.getAwardeePhase());
			else
				this.setAwardeePhase("");			
		}
	}

	@Override
	public String toString() {
		StringBuffer content = new StringBuffer();
		content.append(BagInfoTxtImpl.SOURCE_ORGANIZATION + ": ");
		if (this.getSourceOrganization() != null && !this.getSourceOrganization().isEmpty())
			content.append(this.getSourceOrganization() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.ORGANIZATION_ADDRESS + ": ");
		if (this.getOrganizationAddress() != null && !this.getOrganizationAddress().isEmpty())
			content.append(this.getOrganizationAddress() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.CONTACT_NAME + ": ");
		if (this.getContactName() != null && !this.getContactName().isEmpty())
			content.append(this.getContactName() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.CONTACT_PHONE + ": ");
		if (this.getContactPhone() != null && !this.getContactPhone().isEmpty())
			content.append(this.getContactPhone() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.CONTACT_EMAIL + ": ");
		if (this.getContactEmail() != null && !this.getContactEmail().isEmpty())
			content.append(this.getContactEmail() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.EXTERNAL_DESCRIPTION + ": ");
		if (this.getExternalDescription() != null && !this.getExternalDescription().isEmpty())
			content.append(this.getExternalDescription() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.BAGGING_DATE + ": ");
		if (this.getBaggingDate() != null && !this.getBaggingDate().isEmpty())
			content.append(this.getBaggingDate() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.EXTERNAL_IDENTIFIER + ": ");
		if (this.getExternalIdentifier() != null && !this.getExternalIdentifier().isEmpty())
			content.append(this.getExternalIdentifier() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.BAG_SIZE + ": ");
		if (this.getBagSize() != null && !this.getBagSize().isEmpty())
			content.append(this.getBagSize() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.PAYLOAD_OXUM + ": ");
		if (this.getPayloadOxum() != null && !this.getPayloadOxum().isEmpty())
			content.append(this.getPayloadOxum() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.BAG_GROUP_IDENTIFIER + ": ");
		if (this.getBagGroupIdentifier() != null && !this.getBagGroupIdentifier().isEmpty())
			content.append(this.getBagGroupIdentifier() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.BAG_COUNT + ": ");
		if (this.getBagCount() != null && !this.getBagCount().isEmpty())
			content.append(this.getBagCount() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.INTERNAL_SENDER_IDENTIFIER + ": ");
		if (this.getInternalSenderIdentifier() != null && !this.getInternalSenderIdentifier().isEmpty())
			content.append(this.getInternalSenderIdentifier() + "\n");
		else
			content.append("\n");
		content.append(BagInfoTxtImpl.INTERNAL_SENDER_DESCRIPTION + ": ");
		if (this.getInternalSenderDescription() != null && !this.getInternalSenderDescription().isEmpty())
			content.append(this.getInternalSenderDescription() + "\n");
		else
			content.append("\n");
		if (this.baggerBag.getIsEdeposit()) {
			content.append(EDEPOSIT_PUBLISHER + ": ");
			if (this.getPublisher() != null && !this.getPublisher().isEmpty())
				content.append(this.getPublisher() + "\n");
			else
				content.append("\n");			
		}
		if (this.baggerBag.getIsNdnp()) {
			content.append(NDNP_AWARDEE_PHASE + ": ");
			if (this.getAwardeePhase() != null && !this.getAwardeePhase().isEmpty())
				content.append(this.getAwardeePhase() + "\n");
			else
				content.append("\n");			
		}
		
		return content.toString();
	}

	public static String getTodaysDate() {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat formatter = new SimpleDateFormat(pattern);
		Date todaysDate = new Date();
		String baggingDate = formatter.format(todaysDate);
		
		return baggingDate;
	}
}