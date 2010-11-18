package gov.loc.repository.bagger.bag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

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
public class BaggerOrganization {
	private static final Log log = LogFactory.getLog(BaggerOrganization.class);

	private String orgName = "";

	private String orgAddress = "";

	private Contact contact = new Contact();

	public String getOrgName() {
		return this.orgName;
	}

	public void setOrgName(String name) {
		this.orgName = name;
	}

	public String getOrgAddress() {
		return this.orgAddress;
	}

	public void setOrgAddress(String address) {
		this.orgAddress = address;
	}

	public Contact getContact() {
		return this.contact;
	}

	public void setContact(Contact contact) {
		this.contact = contact;
	}

	@Override
	public String toString() {
		log.info("BaggerOrganization.toString");
		StringBuffer sb = new StringBuffer();
		sb.append(this.orgName);
		sb.append('\n');
		sb.append(this.orgAddress);
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
