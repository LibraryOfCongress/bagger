package gov.loc.repository.transfer.ui.models;

import java.util.List;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class User extends Base<Long>{ 
    
    protected List<String> groupNames;
    
    public void setGroupNames(List<String> groupNames){
        this.groupNames = groupNames;
    }
    public List<String> getGroupNames(){
        return this.groupNames;
    }
}
