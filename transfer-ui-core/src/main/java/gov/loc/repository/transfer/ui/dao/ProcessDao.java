package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Process;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.graph.exe.ProcessInstance;
import org.springframework.stereotype.Repository;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Repository
public class ProcessDao extends JbpmDao<Process, Long>{
    
	protected static final Log log = LogFactory.getLog(ProcessDao.class);	
	
    public ProcessDao() { }
    
    public Process find(Long id){
        this.log.debug("Looking for user with id "+ id);
        Process process = null;
        try{
            org.jbpm.graph.exe.ProcessInstance jbpmProcess = 
                this.getJbpmContext().getProcessInstance(id);
            process = this.toProcess(jbpmProcess);
        }catch(Exception e){
            this.log.error(e.getMessage());
        }
        return process;
    }
    
    public List<Process> findAll(){
        return null;
    }
    
    public void save(Process process){
        this.log.debug("Saving process");
        this.getJbpmContext().save(
            this.toJbpmProcess(process)
        );
        return;
    }
    
    public Process merge(Process process){
        this.log.debug("Merging process");
        this.getJbpmContext().save(
            this.toJbpmProcess(process)
        );
        return null;//FIX -finish implementation
    }
    
    public void remove(Process process){
        this.log.debug("Removing process "+ process.getId());
        return;//FIX - finish implementing
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Process.class;
    };
    
    //NAMED QUERIES
    /*public Process findByUserNameAndProcessType(Map params){//param map is {name:"name", processeType:"organisation"}
        this.log.debug("Search for processs by username and process type "+ params.toString());
        List processs = this.getIdentitySession().getProcessNamesByUserAndProcessType(
            params.get("name").toString(),
            params.get("processType").toString()
        );
        return null;//FIX - finish implementation
    }*/
    
    //HELPERS
    private Process toProcess(org.jbpm.graph.exe.ProcessInstance jbpmProcess){
        Process process = new Process();
        process.setId(jbpmProcess.getId());
        return process;
    }
    
    private org.jbpm.graph.exe.ProcessInstance toJbpmProcess(Process process){
        org.jbpm.graph.exe.ProcessInstance jbpmProcess = 
            new org.jbpm.graph.exe.ProcessInstance();
        return jbpmProcess;
    }
}

