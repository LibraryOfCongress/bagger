package gov.loc.repository.bagger;

/**
 * The high-level Bagger Profile business interface.
 *
 * <p>This is basically a data access object.
 * Bagger doesn't have a dedicated business facade.
 *
 * @author Jon Steinbach
 */
public class Person {
	private int id;
	private String firstName = "";
	private String middleInit = "";
	private String lastName = "";

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
		
	public void setFirstName(String n) {
		this.firstName = n;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setMiddleInit(String n) {
		this.middleInit = n;
	}
	
	public String getMiddleInit() {
		return this.middleInit;
	}
	
	public void setLastName(String n) {
		this.lastName = n;
	}
	
	public String getLastName() {
		return this.lastName;
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(this.getFirstName());
		sb.append(' ');
		sb.append(this.getMiddleInit());
		sb.append(' ');
		sb.append(this.getLastName());
		
		return sb.toString();
	}
}
