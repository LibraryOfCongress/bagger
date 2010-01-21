package gov.loc.repository.bagger;

/**
 * The high-level Bagger Profile business interface.
 *
 * <p>This is basically a data access object.
 * Bagger doesn't have a dedicated business facade.
 *
 * @author Jon Steinbach
 */
public class UserContact {
	private int id;
	private String username;
	private int contactId;

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setContactId(int id) {
		this.contactId = id;
	}

	public int getContactId() {
		return this.contactId;
	}

	public void setUsername(String s) {
		this.username = s;
	}

	public String getUsername() {
		return this.username;
	}
}
