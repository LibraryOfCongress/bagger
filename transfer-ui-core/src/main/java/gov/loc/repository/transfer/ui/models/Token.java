package gov.loc.repository.transfer.ui.models;

import java.util.List;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class Token extends Base<Long>{
    
    protected Long processIdRef;
    protected Node parent;//Previous 'step'
    protected List<Node> children;//possible next 'steps'
    
    public Long getProcessIdRef(){
        return this.processIdRef;
    }
    public void setProcessIdRef(Long processIdRef){
        this.processIdRef = processIdRef;
    }
    public Node getParent(){
        return this.parent;
    }
    public void setParent(Node parent){
        this.parent = parent;
    }
    public List<Node> getChildren(){
        return this.children;
    }
    public void setChildren(List<Node> children){
        this.children = children;
    }
}
