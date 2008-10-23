package gov.loc.repository.bagger;

public class ContactType {
	private int id;
	private String name = "";

	public void setId(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}

	public void setName(String n) {
		this.name = n;
	}

	public String getName() {
		return this.name;
	}

}
