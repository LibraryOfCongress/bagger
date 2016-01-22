package gov.loc.repository.bagger.bag;

import java.io.Serializable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Jon Steinbach
 */
public class BaggerFetch implements Serializable {
  private static final long serialVersionUID = 1L;

  protected static final Logger log = LoggerFactory.getLogger(BaggerFetch.class);

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
