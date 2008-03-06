package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

public class ProcessInstanceHelper {

	private static final Log log = LogFactory.getLog(ProcessInstanceHelper.class);	

	/*
	public static List<ProcessInstanceBean> getProcessInstanceList(JbpmContext jbpmContext)
	{
		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List processDefinitionList = jbpmContext.getGraphSession().findAllProcessDefinitions();
		Iterator definitionIter = processDefinitionList.iterator();
		while(definitionIter.hasNext())
		{
			ProcessDefinition definition = (ProcessDefinition)definitionIter.next();
			List processInstanceList = jbpmContext.getGraphSession().findProcessInstances(definition.getId());
			Iterator instanceIter = processInstanceList.iterator();
			while (instanceIter.hasNext())
			{
				ProcessInstance processInstance = (ProcessInstance)instanceIter.next();
				ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
				processInstanceBean.setJbpmContext(jbpmContext);
				processInstanceBean.setProcessInstance(processInstance);
				processInstanceBeanList.add(processInstanceBean);
			}
		}
		return processInstanceBeanList;
		
	}
			
	public static List<ProcessInstanceBean> getRoleLimitedProcessInstanceList(JbpmContext jbpmContext, HttpServletRequest req, SecurityRoleType roleType, boolean includeEnded)
	{

		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List processDefinitionList = jbpmContext.getGraphSession().findAllProcessDefinitions();
		Iterator definitionIter = processDefinitionList.iterator();
		while(definitionIter.hasNext())
		{
			ProcessDefinition definition = (ProcessDefinition)definitionIter.next();
			ProcessBean processBean = new ProcessBean();
			processBean.setContext(jbpmContext);
			processBean.setProcessDefinition(definition);
			String role = processBean.getRepository() + "-" + roleType.toString();
			if (req.isUserInRole(role))
			{
				List processInstanceList = jbpmContext.getGraphSession().findProcessInstances(definition.getId());
				Iterator instanceIter = processInstanceList.iterator();
				while (instanceIter.hasNext())
				{
					ProcessInstance processInstance = (ProcessInstance)instanceIter.next();
					if (includeEnded || ! processInstance.hasEnded())
					{
						ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
						processInstanceBean.setJbpmContext(jbpmContext);
						processInstanceBean.setProcessInstance(processInstance);
						processInstanceBeanList.add(processInstanceBean);
					}
				}
			}
		}
		return processInstanceBeanList;
		
	}

	public static List<ProcessInstanceBean> getRoleLimitedSuspendedProcessInstanceList(JbpmContext jbpmContext, HttpServletRequest req, SecurityRoleType roleType)
	{
		log.debug("Getting role limited, suspended process instance list");
		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List processDefinitionList = jbpmContext.getGraphSession().findAllProcessDefinitions();
		Iterator definitionIter = processDefinitionList.iterator();
		while(definitionIter.hasNext())
		{
			ProcessDefinition definition = (ProcessDefinition)definitionIter.next();
			log.debug("Checking process definition " + definition.getName());
			ProcessBean processBean = new ProcessBean();
			processBean.setContext(jbpmContext);
			processBean.setProcessDefinition(definition);
			String role = processBean.getRepository() + "-" + roleType.toString();
			if (req.isUserInRole(role))
			{
				log.debug("User is in " + role);
				List processInstanceList = jbpmContext.getGraphSession().findProcessInstances(definition.getId());
				log.debug("Process instance list has " + processInstanceList.size() + " items");
				Iterator instanceIter = processInstanceList.iterator();
				while (instanceIter.hasNext())
				{
					ProcessInstance processInstance = (ProcessInstance)instanceIter.next();
					if (processInstance.isSuspended())
					{
						ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
						processInstanceBean.setJbpmContext(jbpmContext);
						processInstanceBean.setProcessInstance(processInstance);
						processInstanceBeanList.add(processInstanceBean);
					}
				}
			}
		}
		return processInstanceBeanList;
		
	}
	*/
	
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
