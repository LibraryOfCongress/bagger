package gov.loc.repository.bagger.bag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author Jon Steinbach
 */
public class BaggerFetch {
	private static final long serialVersionUID = 1L;

	private static final Log log = LogFactory.getLog(BaggerFetch.class);

	private String baseURL;
	private String userName;
	private String userPassword;

	public BaggerFetch() {
		log.debug("BaggerFetch");
	}

	public void setBaseURL(String url) {
		this.baseURL = url;
	}
	
	public String getBaseURL() {
		return this.baseURL;
	}
	
	public void setUserName(String username) {
		this.userName = username;
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	public void setUserPassword(String password) {
		this.userPassword = password;
	}
	
	public String getUserPassword() {
		return this.userPassword;
	}
	
}
