package gov.loc.repository.transfer.ui.model;

import org.jbpm.JbpmContext;

public class TaskInstanceHelper {
	public static boolean exists(String taskInstanceId, JbpmContext jbpmContext)
	{
		try
		{
			if (jbpmContext.getTaskInstance(Long.parseLong(taskInstanceId)) != null)
			{
				return true;
			}
		}
		catch(Exception ex)
		{			
		}
		return false;
	}
	
	public static TaskInstanceBean getTaskInstanceBean(String taskInstanceId, JbpmContext jbpmContext)
	{
		try
		{
			TaskInstanceBean taskInstanceBean = new TaskInstanceBean();
			taskInstanceBean.setTaskInstance(jbpmContext.getTaskInstance(Long.parseLong(taskInstanceId)));
			taskInstanceBean.setJbpmContext(jbpmContext);
			return taskInstanceBean;
		}
		catch(Exception ex)
		{			
		}
		return null;
	}
	
}
