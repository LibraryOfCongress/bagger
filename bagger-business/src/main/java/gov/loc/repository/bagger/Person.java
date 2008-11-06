package gov.loc.repository.bagger;

import java.util.StringTokenizer;
import java.util.ArrayList;

/**
 * The high-level Bagger Profile business interface.
 *
 * <p>This is basically a data access object.
 * Bagger doesn't have a dedicated business facade.
 *
 * @author Jon Steinbach
 */
public class Person {
	private int id = -1;
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
	
	public void parse(String name) {
		ArrayList<String> tokens = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(name, " ");
		while (st.hasMoreTokens()) {
			  String s=st.nextToken();
			  tokens.add(s);
		}
		if (tokens != null && !tokens.isEmpty()) {
			this.firstName = tokens.get(0);
			if (tokens.size() > 1) this.lastName = tokens.get(tokens.size()-1);
			if (tokens.size() > 2) this.middleInit = tokens.get(1);			
		}
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
