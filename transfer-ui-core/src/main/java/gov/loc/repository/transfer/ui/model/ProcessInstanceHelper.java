package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

public class ProcessInstanceHelper {

	@SuppressWarnings("unchecked")
	public static List<ProcessInstanceBean> getProcessInstanceBeanList(JbpmContext jbpmContext)
	{
		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List<ProcessDefinition> processDefinitionList = jbpmContext.getGraphSession().findAllProcessDefinitions();
		Iterator<ProcessDefinition> definitionIter = processDefinitionList.iterator();
		while(definitionIter.hasNext())
		{
			ProcessDefinition definition = definitionIter.next();
			List<ProcessInstance> processInstanceList = jbpmContext.getGraphSession().findProcessInstances(definition.getId());
			Iterator<ProcessInstance> instanceIter = processInstanceList.iterator();
			while (instanceIter.hasNext())
			{
				ProcessInstance processInstance = instanceIter.next();
				ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
				processInstanceBean.setJbpmContext(jbpmContext);
				processInstanceBean.setProcessInstance(processInstance);
				processInstanceBeanList.add(processInstanceBean);
			}
		}
		return processInstanceBeanList;
		
	}
			
	
	public static List<ProcessInstanceBean> getSuspendedProcessInstanceBeanList(JbpmContext jbpmContext)
	{
		List<ProcessInstanceBean> processInstanceBeanList = getProcessInstanceBeanList(jbpmContext);
		List<ProcessInstanceBean> suspendedProcessInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		for(ProcessInstanceBean processInstanceBean : processInstanceBeanList)
		{
			if (processInstanceBean.isSuspended())
			{
				suspendedProcessInstanceBeanList.add(processInstanceBean);
			}
		}
		return suspendedProcessInstanceBeanList;
	}
	
	public static final boolean hasProcessInstance(long processInstanceId, JbpmContext jbpmContext)
	{
		if (jbpmContext.getProcessInstance(processInstanceId) == null)
		{
			return false;
		}
		return true;
	}
	
	public static ProcessInstanceBean getProcessInstanceBean(long processInstanceId, JbpmContext jbpmContext)
	{
		ProcessInstance processInstance = jbpmContext.getProcessInstance(processInstanceId);
		if (processInstance == null)
		{
			return null;
		}
		ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
		processInstanceBean.setJbpmContext(jbpmContext);
		processInstanceBean.setProcessInstance(processInstance);
		return processInstanceBean;
	}
}
