package gov.loc.repository.bagger;

/**
 *
|    (Contact-Name: Edna Janssen                                          )
|    (Contact-Phone: +1 408-555-1212                                      )
|    (Contact-Nmail: ej@spengler.edu                                      )
 *
 * @author Jon Steinbach
 */
public class Contact {
	private int id;
	private int typeId;
	private Person person;
	private int personId;
	private Organization organization;
	private int organizationId;
	private String contactName = "";
	private String telephone = "";
	private String email = "";

	public int getId() {
		return this.id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getTypeId() {
		return this.typeId;
	}
	
	public void setTypeId(int id) {
		this.typeId = id;
	}
	
	public Person getPerson() {
		return this.person;
	}
	
	public void setPerson(Person person) {
		this.person = person;
		this.contactName = person.getFirstName() + " " + person.getLastName();
	}
	
	public int getPersonId() {
		return this.personId;
	}
	
	public void setPersonId(int id) {
		this.personId = id;
	}
	
	public Organization getOrganization() {
		return this.organization;
	}
	
	public void setOrganization(Organization organization) {
		this.organization = organization;
	}
	
	public int getOrganizationId() {
		return this.organizationId;
	}
	
	public void setOrganizationId(int id) {
		this.organizationId = id;
	}
	
	public String getContactName() {
		return this.contactName;
	}
	
	public void setContactName(String name) {
		this.contactName = name;
	}

	public String getTelephone() {
		return this.telephone;
	}
	
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getContactName());
		sb.append('\n');
		sb.append(this.getOrganization().toString());
		sb.append('\n');
		sb.append(this.getTelephone());
		sb.append('\n');
		sb.append(this.getEmail());
		sb.append('\n');
		
		return sb.toString();
	}
}
