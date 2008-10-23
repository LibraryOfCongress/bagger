package gov.loc.repository.bagger;

public class Project {
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
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id: ");
		sb.append(this.getId());
		sb.append('\n');
		sb.append("Project Name: ");
		sb.append(this.getName());
		sb.append('\n');
		
		return sb.toString();
	}
}
