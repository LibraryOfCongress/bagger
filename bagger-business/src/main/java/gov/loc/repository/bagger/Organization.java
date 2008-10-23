package gov.loc.repository.bagger;

public class Organization {
	private int id;
	private String name = "";
	private Address address;
	private int addressId;

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
	
	public void setAddressId(int id) {
		this.addressId = id;
	}
	
	public int getAddressId() {
		return this.addressId;
	}
	
	public void setAddress(Address a) {
		this.address = a;
	}
	
	public Address getAddress() {
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
		sb.append(this.getAddress().toString());
		
		return sb.toString();
	}
}
