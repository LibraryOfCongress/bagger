package gov.loc.repository.bagger.bag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.ProfileField;
import gov.loc.repository.bagit.BagInfoTxt;

/**
 * Simple JavaBean domain object representing an organization.
 *
|    (Source-organization: California Digital Library                      )
|    (Organization-address: 415 20th Street, 4th Floor, Oakland, CA. 94612 )
|    (Contact-name: A. E. Newman                                           )
|    (Contact-phone: +1 510-555-1234                                       )
|    (Contact-email: alfred@ucop.edu                                       )
 *
 * @author Jon Steinbach
 */
public class BaggerSourceOrganization {
	private static final Log log = LogFactory.getLog(BaggerSourceOrganization.class);

	private String organizationName = "";

	private String organizationAddress = "";

	private Contact contact = new Contact(false);

	public BaggerSourceOrganization() {
	}
	
	public BaggerSourceOrganization(BagInfoTxt bagInfoTxt) {
		contact = new Contact(false);
		if (bagInfoTxt.getContactName() != null
				&& !bagInfoTxt.getContactName().trim().isEmpty()) {
			contact.setContactName(ProfileField.createProfileField(
					Contact.FIELD_CONTACT_NAME, bagInfoTxt.getContactName()));
		} else {
			contact.setContactName(ProfileField.createProfileField(
					Contact.FIELD_CONTACT_NAME, ""));
		}
		if (bagInfoTxt.getContactPhone() != null
				&& !bagInfoTxt.getContactPhone().trim().isEmpty()) {
			contact.setTelephone(ProfileField.createProfileField(
					Contact.FIELD_CONTACT_PHONE, bagInfoTxt.getContactPhone()));
		} else {
			contact.setTelephone(ProfileField.createProfileField(
					Contact.FIELD_CONTACT_PHONE, ""));
		}
		if (bagInfoTxt.getContactEmail() != null
				&& !bagInfoTxt.getContactEmail().trim().isEmpty()) {
			contact.setEmail(ProfileField.createProfileField(
					Contact.FIELD_CONTACT_EMAIL, bagInfoTxt.getContactEmail()));
		} else {
			contact.setEmail(ProfileField.createProfileField(
					Contact.FIELD_CONTACT_EMAIL, ""));
		}

		if (bagInfoTxt.getSourceOrganization() != null
				&& !bagInfoTxt.getSourceOrganization().trim().isEmpty()) {
			setOrganizationName(bagInfoTxt
					.getSourceOrganization());
		} else {
			setOrganizationName("");
		}
		if (bagInfoTxt.getOrganizationAddress() != null
				&& !bagInfoTxt.getOrganizationAddress().trim().isEmpty()) {
			setOrganizationAddress(bagInfoTxt
					.getOrganizationAddress());
		} else {
			setOrganizationAddress("");
		}
	}
	
	public String getOrganizationName() {
		return this.organizationName;
	}

	public void setOrganizationName(String name) {
		this.organizationName = name;
	}

	public String getOrganizationAddress() {
		return this.organizationAddress;
	}

	public void setOrganizationAddress(String address) {
		this.organizationAddress = address;
	}

	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public String toString() {
		log.info("SourceOrganization.toString");
		StringBuffer sb = new StringBuffer();
		sb.append(this.organizationName);
		sb.append('\n');
		sb.append(this.organizationAddress);
		sb.append('\n');
		sb.append(this.contact.getContactName());
		sb.append('\n');
		sb.append(this.contact.getTelephone());
		sb.append('\n');
		sb.append(this.contact.getEmail());
		sb.append('\n');

		return sb.toString();
	}
	
}
