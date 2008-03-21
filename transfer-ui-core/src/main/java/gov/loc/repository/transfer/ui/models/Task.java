package gov.loc.repository.transfer.ui.models;

import java.util.List;
import java.util.Date;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Task extends Base<Long> {
    protected Date dateCreated;
    protected Date dateEnded;
    protected List<String> possibleTransitionNames;
    protected String actualTransitionName;
    protected Boolean ended;
    protected List<Variable> variables;
    protected Long processIdRef;
    protected String processName;
    protected String packageName;
    protected Boolean reassignable;
    protected String assignedUserName;
    protected List<String> groupNames;
    protected Boolean updateable;
    
    public Date getDateCreated(){
        return this.dateCreated;
    }
    public void setDateCreated(Date dateCreated){
        this.dateCreated = dateCreated;
    }
    public Date getDateEnded(){
        return this.dateEnded;
    }
    public void setDateEnded(Date dateEnded){
        this.dateEnded = dateEnded;
    }
    public List<String> getPossibleTransitionNames(){
        return this.possibleTransitionNames;
    }
    public void setPossibleTransitionNames(List<String> possibleTransitionNames){
        this.possibleTransitionNames = possibleTransitionNames;
    }
    public String getActualTransitionName(){
        return this.actualTransitionName;
    }
    public void setActualTransitionName(String actualTransitionName){
        this.actualTransitionName = actualTransitionName;
    }
    public Boolean getEnded(){
        return this.ended;
    }
    public void setEnded(Boolean ended){
        this.ended = ended;
    }
    public boolean isEnded(){
        return Boolean.valueOf(this.ended);
    }
    public List<Variable> getVariables(){
        return this.variables;
    }
    public void setVariables(List<Variable> variables){
        this.variables = variables;
    }
    public Long getProcessIdRef(){
        return this.processIdRef;
    }
    public void setProcessIdRef(Long processIdRef){
        this.processIdRef = processIdRef;
    }
    public String getProcessName(){
        return this.processName;
    }
    public void setProcessName(String processName){
        this.processName = processName;
    }
    public String getPackageName(){
        return this.packageName;
    }
    public void setPackageName(String packageName){
        this.packageName = packageName;
    }
    public Boolean getReassignable(){
        return this.reassignable;
    }
    public void setReassignable(Boolean reassignable){
        this.reassignable = reassignable;
    }
    public boolean isReassignable(){
        return Boolean.valueOf(this.reassignable);
    }
    public String getAssignedUserName(){
        return this.assignedUserName;
    }
    public void setAssignedUserName(String assignedUserName){
        this.assignedUserName = assignedUserName;
    }
    public List<String> getGroupNames(){
        return this.groupNames;
    }
    public void setGroupNames(List<String> groupNames){
        this.groupNames = groupNames;
    }
    public Boolean getUpdateable(){
        return this.updateable;
    }
    public void setUpdateable(Boolean updateable){
        this.updateable = updateable;
    }
    public boolean isUpdateable(){
        return Boolean.valueOf(this.updateable);
    }
}
