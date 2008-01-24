package gov.loc.repository.console.taskInstance;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import gov.loc.repository.console.workflow.beans.TaskInstanceBean;

import org.jbpm.JbpmContext;
import org.springframework.web.servlet.ModelAndView;

public interface TaskInstanceUpdateCommand {
	public ModelAndView prepareForm() throws Exception;
	
	public ModelAndView prepareInstruction() throws Exception;
	
	public void preprocessPut() throws Exception;
	
	public void bindPut() throws Exception;
	
	public String validatePut() throws Exception;
	
	public void setTaskInstanceBean(TaskInstanceBean taskInstanceBean);
	
	public void setRequest(HttpServletRequest request);
	
	public void setResponse(HttpServletResponse response);
	
	public void setJbpmContext(JbpmContext jbpmContext);
	
	public boolean canUpdateUser();
	
	public boolean canUpdateTaskInstance();
	
	
}
