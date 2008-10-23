package gov.loc.repository.bagger;

import gov.loc.repository.bagger.Contact;
import gov.loc.repository.bagger.Project;

public class Profile {
	private int id;
	private String username = "";
	private int profilePersonId;
	private Contact  person;
	private int contactId;
	private Contact contact;
	private int projectId;
	private Project project;

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setProfilePersonId(int id) {
		this.profilePersonId = id;
	}
	
	public int getProfilePersonId() {
		return this.profilePersonId;
	}
	
	public void setPerson(Contact person) {
		this.person = person;
	}
	
	public Contact getPerson() {
		return this.person;
	}
	
	public void setUsername(String n) {
		this.username = n;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setContactId(int id) {
		this.contactId = id;
	}
	
	public int getContactId() {
		return this.contactId;
	}
	
	public void setContact(Contact contact) {
		this.contact = contact;
	}
	
	public Contact getContact() {
		return this.contact;
	}
	
	public void setProjectId(int id) {
		this.projectId = id;
	}
	
	public int getProjectId() {
		return this.projectId;
	}
	
	public void setProject(Project project) {
		this.project = project;
	}
	
	public Project getProject() {
		return this.project;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Project Id: ");
		sb.append(this.id);
		sb.append('\n');
		sb.append("Username: ");
		sb.append(this.username);
		sb.append('\n');
		sb.append("Profile Person: ");
		sb.append(this.getPerson().toString());
		sb.append('\n');
		sb.append("Contact: ");
		sb.append(this.getContact().toString());
		sb.append('\n');
		sb.append("Project: ");
		sb.append(this.getProject().toString());
		sb.append('\n');
		
		return sb.toString();
	}
}
