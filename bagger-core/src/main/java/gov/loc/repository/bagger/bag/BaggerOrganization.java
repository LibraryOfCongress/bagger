package gov.loc.repository.bagger.bag;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import gov.loc.repository.bagger.Contact;

/**
 * Simple JavaBean domain object representing an organization.
 *
 * | (Source-organization: California Digital Library )
 * | (Organization-address: 415 20th Street, 4th Floor, Oakland, CA. 94612 )
 * | (Contact-name: A. E. Newman )
 * | (Contact-phone: +1 510-555-1234 )
 * | (Contact-email: alfred@ucop.edu )
 *
 * @author Jon Steinbach
 */
public class BaggerOrganization {
  protected static final Logger log = LoggerFactory.getLogger(BaggerOrganization.class);

  private String sourceOrganization = "";

  private String organizationAddress = "";

  private Contact contact = new Contact(false);

  public String getSourceOrganization() {
    return this.sourceOrganization;
  }

  public void setSourceOrganization(String name) {
    this.sourceOrganization = name;
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
    log.info("BaggerOrganization.toString");
    StringBuffer sb = new StringBuffer();
    sb.append(this.sourceOrganization);
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
