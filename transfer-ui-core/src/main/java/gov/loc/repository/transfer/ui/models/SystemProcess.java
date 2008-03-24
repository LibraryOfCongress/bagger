package gov.loc.repository.transfer.ui.models;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class SystemProcess extends Base<String> {
    protected Boolean suspended;
    protected String description;
    
    public Boolean getSuspended(){
        return this.suspended;
    }
    public void setSuspended(Boolean suspended){
        this.suspended = suspended;
    }
    public boolean isSuspended(){
        return Boolean.valueOf(this.suspended);
    }
    public String getDescription(){
        return this.description;
    }
    public void setDescription(String description){
        this.description = description;
    }
}
