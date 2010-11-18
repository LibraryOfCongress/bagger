package gov.loc.repository.bagger.bag;

import gov.loc.repository.bagger.Contact;

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
public class BaggerProfile {
	private BaggerSourceOrganization sourceOrganization = new BaggerSourceOrganization();
	private Contact toContact = new Contact(true);
	
	public BaggerSourceOrganization getOrganization() {
		return this.sourceOrganization;
	}
	
	public void setOrganization(BaggerSourceOrganization organization) {
		this.sourceOrganization = organization;
	}

	
	public Contact getSourceContact() {
		return this.sourceOrganization.getContact();
	}
	
	public void setSourceContact(Contact contact) {
		this.sourceOrganization.setContact(contact);
	}
	
	public Contact getToContact() {
		return this.toContact;
	}
	
	public void setToContact(Contact contact) {
		this.toContact = contact;
	}

	public String getSourceOrganization() {
		return this.sourceOrganization.getOrganizationName();
	}

	public void setSourceOrganization(String name) {
		this.sourceOrganization.setOrganizationName(name);
	}

	public String getOrganizationAddress() {
		return this.sourceOrganization.getOrganizationAddress();
	}

	public void setOrganizationAddress(String address) {
		this.sourceOrganization.setOrganizationAddress(address);
	}

	public String getSrcContactName() {
		return this.sourceOrganization.getContact().getContactName().getFieldValue();
	}

	public void setSrcContactName(String name) {
		this.sourceOrganization.getContact().getContactName().setFieldValue(name);
	}

	public String getSrcContactPhone() {
		return this.sourceOrganization.getContact().getTelephone().getFieldValue();
	}
	
	public void setSrcContactPhone(String phone) {
		this.sourceOrganization.getContact().getTelephone().setFieldValue(phone);
	}

	public String getSrcContactEmail() {
		return this.sourceOrganization.getContact().getEmail().getFieldValue();
	}
	
	public void setSrcContactEmail(String email) {
		this.sourceOrganization.getContact().getEmail().setFieldValue(email);
	}

	public String getToContactName() {
		return this.toContact.getContactName().getFieldValue();
	}

	public void setToContactName(String name) {
		this.toContact.getContactName().setFieldValue(name);
	}

	public String getToContactPhone() {
		return this.toContact.getTelephone().getFieldValue();
	}
	
	public void setToContactPhone(String phone) {
		this.toContact.getTelephone().setFieldValue(phone);
	}

	public String getToContactEmail() {
		return this.toContact.getEmail().getFieldValue();
	}
	
	public void setToContactEmail(String email) {
		this.toContact.getEmail().setFieldValue(email);
	}
}
