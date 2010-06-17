package gov.loc.repository.bagger;

public class Project {
	private int id = -1;
	private boolean isDefault = false;
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
	
	public void setIsDefault(boolean b) {
		this.isDefault = b;
	}
	
	public boolean getIsDefault() {
		return this.isDefault;
	}
	
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("Id: ");
		sb.append(this.getId());
		sb.append('\n');
		sb.append("Project Name: ");
		sb.append(this.getName());
		sb.append('\n');
		sb.append("Default?: ");
		if (this.getIsDefault()) sb.append("true");
		else sb.append("false");
		sb.append('\n');
		
		return sb.toString();
	}
}
