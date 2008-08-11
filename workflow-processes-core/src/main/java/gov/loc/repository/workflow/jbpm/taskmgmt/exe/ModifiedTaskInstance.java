package gov.loc.repository.workflow.jbpm.taskmgmt.exe;

import java.util.Date;
import java.util.Iterator;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmException;
import org.jbpm.context.exe.ContextInstance;
import org.jbpm.context.exe.VariableInstance;
import org.jbpm.graph.def.Event;
import org.jbpm.graph.def.Transition;
import org.jbpm.graph.exe.ExecutionContext;
import org.jbpm.taskmgmt.def.TaskController;
import org.jbpm.taskmgmt.exe.TaskInstance;
import org.jbpm.taskmgmt.exe.TaskMgmtInstance;
import org.jbpm.taskmgmt.log.TaskEndLog;

public class ModifiedTaskInstance extends org.jbpm.taskmgmt.exe.TaskInstance
{
	private static final Log log = LogFactory.getLog(ModifiedTaskInstance.class);
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public void end(Transition transition) {
	    if (this.end!=null){
	      throw new IllegalStateException("task instance is already ended");
	    }
	    if (this.isSuspended) {
	      throw new JbpmException("task instance is suspended");
	    }

		//Modification:  Add the transition as a transient variable, so have access to it
		if (task!=null && token!=null)
		{
			ExecutionContext executionContext = new ExecutionContext(token);
			executionContext.getContextInstance().setTransientVariable("transition", transition.getName());
		}
		//Modification:  Variables aren't submitted before the event is called, so call earlier
		submitVariablesx();
	    
	    
	    // mark the end of this task instance
	    this.end = new Date();
	    this.isOpen = false;

	    // fire the task instance end event
	    if ( (task!=null)
	         && (token!=null)
	       ) {
	      ExecutionContext executionContext = new ExecutionContext(token);
	      executionContext.setTask(task);
	      executionContext.setTaskInstance(this);
	      task.fireEvent(Event.EVENTTYPE_TASK_END, executionContext);
	    }
	    
	    // log this assignment
	    if (token!=null) {
	      token.addLog(new TaskEndLog(this));
	    }
	    
	    // submit the variables
	    //submitVariables();
	    	      
	    // verify if the end of this task triggers continuation of execution
	    if (isSignalling) {
	      this.isSignalling = false;
	      
	      
	      
	      if ( this.isStartTaskInstance() // ending start tasks always leads to a signal
	           || ( (task!=null)
	                && (token!=null)
	      //Modification:  Make sure isn't suspended
	                && (! token.isSuspended())
	                && (task.getTaskNode()!=null)
	                && (task.getTaskNode().completionTriggersSignal(this))
	              )
	         ) {
	        
	        if (transition==null) {
	          log.debug("completion of task '"+task.getName()+"' results in taking the default transition");
	          token.signal();
	        } else {
	          log.debug("completion of task '"+task.getName()+"' results in taking transition '"+transition+"'");
	          token.signal(transition);
	        }
	      }
	    }
	  }
	

	//Copied directly from org.jbpm.taskmgmt.exe.TaskInstance
	@SuppressWarnings("unchecked")
	private void submitVariablesx() {
		    
		TaskController taskController = (task!=null ? task.getTaskController() : null);
		    // if there is a task controller, 
		    if (taskController!=null) {
		    	// the task controller is responsible for copying variables back into the process
		      taskController.submitParameters(this);
		      
		    // if there is no task controller
		    } else if ( (token!=null)
		                && (token.getProcessInstance()!=null)
		              ) {
		      // the default behaviour is that all task-local variables are flushed to the process
		      if (variableInstances!=null) {
		        ContextInstance contextInstance = token.getProcessInstance().getContextInstance();
		        Iterator iter = variableInstances.values().iterator();
		        while(iter.hasNext()) {
		          VariableInstance variableInstance = (VariableInstance) iter.next();
		          // This might be optimized, but this was the simplest way to make a clone of the variable instance.
		          contextInstance.setVariable(variableInstance.getName(), variableInstance.getValue(), token);
		        }
		      }
		    }
		  }	
}
