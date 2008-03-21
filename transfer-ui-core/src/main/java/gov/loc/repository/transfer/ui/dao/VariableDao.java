package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Variable;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;

import org.jbpm.context.def.VariableAccess;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
public class VariableDao extends JbpmDao<Variable, Long>{
    
	protected static final Log log = LogFactory.getLog(VariableDao.class);	
	
    public VariableDao() { }
    
    public Variable find(Long id){
        this.log.debug("Looking for user with id "+ id);
        Variable variable = null;
        try{
            org.jbpm.context.def.VariableAccess jbpmVariable = null;
                //this.getJbpmContext().getVariableInstance(id);
            variable = this.toVariable(jbpmVariable);
        }catch(Exception e){
            this.log.error(e.getMessage());
        }
        return variable;
    }
    
    public List<Variable> findAll(){
        return null;
    }
    
    public void save(Variable variable){
        this.log.debug("Saving variable");
        return;
    }
    
    public Variable merge(Variable variable){
        this.log.debug("Merging variable");
        return null;//FIX -finish implementation
    }
    
    public void remove(Variable variable){
        this.log.debug("Removing variable "+ variable.getId());
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Variable.class;
    };
    
    //NAMED QUERIES
    /*public Variable findByUserNameAndVariableType(Map params){//param map is {name:"name", variableeType:"organisation"}
        this.log.debug("Search for variables by username and variable type "+ params.toString());
        List variables = this.getIdentitySession().getVariableNamesByUserAndVariableType(
            params.get("name").toString(),
            params.get("variableType").toString()
        );
        return null;//FIX - finish implementation
    }*/
    
    //HELPERS
    private Variable toVariable(org.jbpm.context.def.VariableAccess jbpmVariable){
        Variable variable = new Variable();
        variable.setName(jbpmVariable.getVariableName());
        return variable;
    }
    
    private org.jbpm.context.def.VariableAccess toJbpmVariable(Variable variable){
        org.jbpm.context.def.VariableAccess jbpmVariable = 
            new org.jbpm.context.def.VariableAccess();
        return jbpmVariable;
    }
}