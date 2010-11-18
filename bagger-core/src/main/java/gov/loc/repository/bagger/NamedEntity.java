package gov.loc.repository.bagger;

/**
 * Simple JavaBean domain object adds a name property to <code>BaseEntity</code>.
 * Used as a base class for objects needing these properties.
 *
 * @author Jon Steinbach
 */
public class NamedEntity extends BaseEntity {

	private String name;
	

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public String toString() {
		return this.getName();
	}

}
