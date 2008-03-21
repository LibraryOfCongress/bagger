package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Node;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class NodeDao extends JbpmDao<Node, Long>{
    
	protected static final Log log = LogFactory.getLog(NodeDao.class);	
	
    public NodeDao() { }
    
    public Node find(Long id){
        this.log.debug("Looking for user with id "+ id);
        Node node = null;
        try{
            org.jbpm.graph.def.Node jbpmNode = null;
                //this.getJbpmContext().getNodeInstance(id);
            node = this.toNode(jbpmNode);
        }catch(Exception e){
            this.log.error(e.getMessage());
        }
        return node;
    }
    
    public List<Node> findAll(){
        return null;
    }
    
    public void save(Node node){
        this.log.debug("Saving node");
        return;
    }
    
    public Node merge(Node node){
        this.log.debug("Merging node");
        return null;//FIX -finish implementation
    }
    
    public void remove(Node node){
        this.log.debug("Removing node "+ node.getId());
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Node.class;
    };
    
    //NAMED QUERIES
    /*public Node findByUserNameAndNodeType(Map params){//param map is {name:"name", nodeeType:"organisation"}
        this.log.debug("Search for nodes by username and node type "+ params.toString());
        List nodes = this.getIdentitySession().getNodeNamesByUserAndNodeType(
            params.get("name").toString(),
            params.get("nodeType").toString()
        );
        return null;//FIX - finish implementation
    }*/
    
    //HELPERS
    private Node toNode(org.jbpm.graph.def.Node jbpmNode){
        Node node = new Node();
        node.setId(jbpmNode.getId());
        return node;
    }
    
    private org.jbpm.graph.def.Node toJbpmNode(Node node){
        org.jbpm.graph.def.Node jbpmNode = 
            new org.jbpm.graph.def.Node();
        return jbpmNode;
    }
}

