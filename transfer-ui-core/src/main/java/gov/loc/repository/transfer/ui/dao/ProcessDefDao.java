package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.ProcessDef;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.def.ProcessDefinition;
import org.springframework.stereotype.Repository;
/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Repository
public class ProcessDefDao extends JbpmDao<ProcessDef, Long>{
    
    protected static final Log log = LogFactory.getLog(ProcessDefDao.class);	
	
    public ProcessDefDao() { }
    
    public ProcessDef find(Long id){
        this.log.debug("Looking for process definition with id "+ id);
        ProcessDef processDef = null;
        try{//TODO FIXME: this shouldnt' be id.toString it should be the name of the processDef OR we
            //need a different method from jbpm to get it by its hibernate id
            org.jbpm.graph.def.ProcessDefinition jbpmProcessDef = 
                getJbpmContext().getGraphSession().findLatestProcessDefinition(id.toString()); 
            processDef = toProcessDef(jbpmProcessDef);
        }catch(Exception e){
            log.error(e.getMessage());
        }
        return processDef;
    }
    
    public List<ProcessDef> findAll(){
        log.warn("Not Implemented");
        return null;//FIX - finish implementing
    }
    
    public void save(ProcessDef processDef){
        log.warn("Not Implemented");
        return;//FIX - finish implementing
    }
    
    public ProcessDef merge(ProcessDef processDef){
        log.warn("Not Implemented");
        return null;//FIX - finish implementing
    }
    
    public void remove(ProcessDef processDef){
        log.warn("Not Implemented");
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.ProcessDef.class;
    };
    
    //NAMED QUERIES
    public ProcessDef findByProcessId(Map params){//param map is {processId:Long}
        this.log.debug("Search for processs definition by processId "+ params.toString());
        ProcessDefinition jbpmProcessDef = null;
        ProcessDef processDef = null;
        try{
            jbpmProcessDef = getJbpmContext().getProcessInstance(
                Long.parseLong(params.get("processId").toString())
            ).getProcessDefinition();
            processDef = toProcessDef(jbpmProcessDef);
        }catch(Throwable t){
            log.error("Error locating process definition from processId", t);
        } return processDef;
    }
    
    //HELPERS
    private ProcessDef toProcessDef(org.jbpm.graph.def.ProcessDefinition jbpmProcessDef){
        ProcessDef processDef = new ProcessDef();
        processDef.setId(jbpmProcessDef.getId());
        return processDef;
    }
    
    private org.jbpm.graph.def.ProcessDefinition toJbpmProcessDef(ProcessDef processDef){
        org.jbpm.graph.def.ProcessDefinition jbpmProcessDef = 
            new org.jbpm.graph.def.ProcessDefinition();
        return jbpmProcessDef;
    }
}
