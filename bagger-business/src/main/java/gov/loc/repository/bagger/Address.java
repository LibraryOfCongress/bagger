package gov.loc.repository.bagger;

public class Address {
	private int id;
	private String address1;
	private String address2;
	private String city;
	private String state;
	private String country;
	private String postalCode;
	
	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public void setAddress1(String s) {
		this.address1 = s;
	}
	
	public String getAddress1() {
		return this.address1;
	}
	
	public void setAddress2(String s) {
		this.address2 = s;
	}
	
	public String getAddress2() {
		return this.address2;
	}
	
	public void setCity(String c) {
		this.city = c;
	}
	
	public String getCity() {
		return this.city;
	}
	
	public void setState(String s) {
		this.state = s;
	}
	
	public String getState() {
		return this.state;
	}
	
	public void setCountry(String c) {
		this.country = c;
	}
	
	public String getCountry() {
		return this.country;
	}
	
	public void setPostalCode(String p) {
		this.postalCode = p;
	}
	
	public String getPostalCode() {
		return this.postalCode;
	}
	
	public String toString() {
		return toString(false);
	}
	
	public String toString(boolean inline) {
		String delim;
		if (inline) delim = ", ";
		else delim = "\n";
		StringBuffer sb = new StringBuffer();
		sb.append(this.getAddress1());
		sb.append(delim);
		if (!this.getAddress2().isEmpty()) {
			sb.append(this.getAddress2());
			sb.append(delim);
		}
		sb.append(this.getCity());
		sb.append(", ");
		sb.append(this.getState());
		sb.append(' ');
		sb.append(this.getPostalCode());
		sb.append(delim);
		sb.append(this.getCountry());
		
		return sb.toString();
	}
}
