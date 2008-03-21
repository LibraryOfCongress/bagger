package gov.loc.repository.transfer.ui.models;

import java.util.Date;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Comment extends Base<Long>{
    //id is the dateCreated (as  millisecond) the 
    protected String message;
    protected Date dateCreated;
    protected String userName;
    protected Long processIdRef;
    
    public String getMessage() {
		return this.message;
	}
    public void setMessage(String message){
        this.message = message;
    }
	public Date getDateCreated(){
		return this.dateCreated;
	}
	public void setDateCreated(Date dateCreated){
	    this.dateCreated = dateCreated;
	}
	public String getUserName(){
		return this.userName;
	}
	public void setUserName(String userName){
	    this.userName = userName;
	}
    public Long getProcessIdRef(){
        return this.processIdRef;
    }
    public void setProcessIdRef(Long processIdRef){
        this.processIdRef = processIdRef;
    }
	
}

