package gov.loc.repository.transfer.ui.dao;

import gov.loc.repository.transfer.ui.models.Task;
import gov.loc.repository.transfer.ui.models.Variable;
import java.util.ArrayList;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.context.def.VariableAccess;
import org.jbpm.graph.def.Transition;
import org.jbpm.taskmgmt.exe.PooledActor;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * <<Class summary>>
 *
 * @author Chris Thatcher &lt;&gt;
 * @version $Rev$
 */
@Repository
public class TaskDao extends JbpmDao<Task, Long>{
    
	protected static final Log log = LogFactory.getLog(TaskDao.class);	
	
    public TaskDao() { }
    
    //BASIC DATA ACCESS INTERFACE
    public Task find(Long id){
        this.log.debug("Looking for task with id "+ id);
        Task task = null;
        try{
            org.jbpm.taskmgmt.exe.TaskInstance jbpmTask = 
                this.getJbpmContext().getTaskInstance(id);
            task = this.toTask(jbpmTask);
        }catch(Exception e){
            this.log.error(e.getMessage());
        }
        return task;
    }
    
    public List<Task> findAll(){
        return null;
    }
    
    public void save(Task task){
        log.info("Saving task: " + task.getId());
		if(task.isEnded()) { 
		    log.warn("Task is already marked as ended.  Not saving...");
		    return; 
		}
		org.jbpm.taskmgmt.exe.TaskInstance jbpmTask = 
		    this.getJbpmContext().getTaskInstance(task.getId());
		if (jbpmTask.getTask().getTaskController() != null) {		
			jbpmTask.getTask().getTaskController().submitParameters(jbpmTask);		
		}
		
		if (task.getActualTransitionName() != null){
		    jbpmTask.end(task.getActualTransitionName());
		}
		this.jbpmContext.save(jbpmTask);
    }
    
    public Task merge(Task task){
        this.log.warn("This Dao is read-only");
        return null;
    }
    
    public void remove(Task task){
        this.log.warn("This Dao is read-only");
    };
    
    public Class getType(){
        return gov.loc.repository.transfer.ui.models.Task.class;
    };
    
    //NAMED QUERIES
    //@Transactional(readOnly=true)
    public List<Task> findCurrentByUserName(Map params){//param map is {name:"name"}
        this.log.debug("Search for tasks by username"+ params.toString());
        List<Task> tasks = new ArrayList<Task>();
        List<TaskInstance> jbpmTasks = getJbpmContext().getTaskList(
            params.get("name").toString()
        );
        for(TaskInstance jbpmTask:jbpmTasks){
            if(!jbpmTask.hasEnded() && !jbpmTask.getProcessInstance().hasEnded()){
                tasks.add( toTask(jbpmTask) );
            }
        }
        return tasks;
    }
    
    //@Transactional(readOnly=true)
    public List<Task> findCurrentByGroupNames(Map params){//param map is {groupNames:List}
        this.log.debug("Search for tasks by group name"+ params.toString());
        List<Task> tasks = new ArrayList<Task>();
        List<TaskInstance> jbpmTasks = getJbpmContext().getGroupTaskList(
            (List)params.get("groupNames")
        );
        for(TaskInstance jbpmTask:jbpmTasks){
            if(!jbpmTask.hasEnded() && !jbpmTask.getProcessInstance().hasEnded()){
                tasks.add( toTask(jbpmTask) );
            }
        }
        return tasks;
    }
    
    //@Transactional(readOnly=true)
    public List<Task> findByTokenId(Map params){//param map is {tokenId:long}
        this.log.debug("Search for tasks by group name"+ params.toString());
        List<Task> tasks = new ArrayList<Task>();
        List<TaskInstance> jbpmTasks = getJbpmContext().getTaskMgmtSession().findTaskInstancesByToken(
            Long.parseLong(params.get("tokenId").toString())
        );
        for(TaskInstance jbpmTask:jbpmTasks){
            tasks.add( toTask(jbpmTask) );
        }
        return tasks;
    }
    
    //HELPERS
    //@Transactional(readOnly=true)
    private Task toTask(org.jbpm.taskmgmt.exe.TaskInstance jbpmTask){
        Task task = new Task();
        task.setId(jbpmTask.getId());
        task.setName(jbpmTask.getName());
        task.setDateCreated(jbpmTask.getCreate());
        task.setDateEnded(jbpmTask.getEnd());
        task.setEnded(new Boolean(jbpmTask.hasEnded()));
        task.setAssignedUserName(jbpmTask.getActorId());
        task.setUpdateable(
            new Boolean(
                !jbpmTask.hasEnded() && 
                !jbpmTask.getProcessInstance().isSuspended() && 
                !jbpmTask.getProcessInstance().hasEnded()
            )
        );
        task.setProcessIdRef(
            jbpmTask.getProcessInstance().getId()
        );
        task.setProcessName(
            jbpmTask.getProcessInstance()
                .getKey()
        );
        task.setPackageName(
            (String)
            jbpmTask.getProcessInstance()
                .getContextInstance()
                .getVariable("packageId")
        );
        task.setReassignable(
            new Boolean(
                !jbpmTask.hasEnded() 
                && !jbpmTask.getProcessInstance().hasEnded()
            )
        );
        //Add transition
        List<String> transitionNames = new ArrayList<String>();
		List<Transition> jbpmTransitions= jbpmTask.getTask().getTaskNode().getLeavingTransitions();
		for (Transition jbpmTransition:jbpmTransitions){
			transitionNames.add(jbpmTransition.getName());
		}
		task.setPossibleTransitionNames(transitionNames);
		//Add variables
		if(jbpmTask.getTask().getTaskController()!=null){
		    try{
		        jbpmTask.getTask().getTaskController().initializeVariables(jbpmTask);
                List<Variable> variables = new ArrayList<Variable>();
                List variableAccessRules = jbpmTask.getTask().getTaskController().getVariableAccesses();
        		Map<Object, Object> jbpmVariables = jbpmTask.getVariablesLocally();
        		for (Object variableAccessRule:variableAccessRules){
        		    try{
            		    VariableAccess accessRule = (VariableAccess)variableAccessRule;
            		    Variable variable = new Variable();
            		    variable.setName(accessRule.getVariableName());
            		    variable.setReadable(new Boolean(accessRule.isReadable()));
            		    variable.setWritable(new Boolean(accessRule.isWritable()));
            		    variable.setRequired(new Boolean(accessRule.isRequired()));
            		    Object value = jbpmVariables.get(variable.getName());
            		    variable.setValue(value);
            			variables.add(variable);
    			    }catch(Throwable tt){
        			    log.error("Error adding variables: ", tt);
        			}
        		}
        		task.setVariables(variables);
    		}catch(Throwable t){
    		    log.error("Error adding variables: ", t);
    		}
		}
		//Add group names for groups that can view this task
		List<String> groupNames = new ArrayList<String>();
		Set<PooledActor> jbpmGroups = jbpmTask.getPooledActors();
		for(PooledActor jbpmGroup:jbpmGroups){
		    groupNames.add(jbpmGroup.getActorId());
		}
		task.setGroupNames(groupNames);
        return task;
    }
    
    private void toJbpmTask( Task task ){
        org.jbpm.taskmgmt.exe.TaskInstance jbpmTask = 
		    this.getJbpmContext().getTaskInstance(task.getId());
    }
}