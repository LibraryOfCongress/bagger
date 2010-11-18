package gov.loc.repository.bagger;

/**
 * The high-level Bagger Profile business interface.
 *
 * <p>This is basically a data access object.
 * Bagger doesn't have a dedicated business facade.
 *
 * @author Jon Steinbach
 */
public class FtpProperties {
	private String ipAddress = "";
	private String username = "guest";
	private String password = "";
	
	public void setIpAddress(String ip) {
		this.ipAddress = ip;
	}
	
	public String getIpAddress() {
		return this.ipAddress;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return this.username;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return this.password;
	}
}
