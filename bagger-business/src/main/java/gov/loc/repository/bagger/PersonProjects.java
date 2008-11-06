package gov.loc.repository.bagger;

/**
 * The high-level Bagger Profile business interface.
 *
 * <p>This is basically a data access object.
 * Bagger doesn't have a dedicated business facade.
 *
 * @author Jon Steinbach
 */
public class PersonProjects {
	private int id = -1;
	private int personId;
	private int projectId;

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setPersonId(int id) {
		this.personId = id;
	}

	public int getPersonId() {
		return this.personId;
	}

	public void setProjectId(int id) {
		this.projectId = id;
	}

	public int getProjectId() {
		return this.projectId;
	}
}
