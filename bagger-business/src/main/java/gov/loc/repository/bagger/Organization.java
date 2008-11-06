package gov.loc.repository.bagger;

public class Organization {
	private int id = -1;
	private String name = "";
	private String address = "";

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
	
	public void setAddress(String a) {
		this.address = a;
	}
	
	public String getAddress() {
		return this.address;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean inline) {
		String delim;
		if (inline) delim = ", ";
		else delim = "\n";
		StringBuffer sb = new StringBuffer();
		sb.append(this.getName());
		sb.append(delim);
		sb.append(this.getAddress());
		
		return sb.toString();
	}
}
