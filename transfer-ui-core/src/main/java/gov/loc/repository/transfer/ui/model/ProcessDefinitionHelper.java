package gov.loc.repository.transfer.ui.model;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.jbpm.JbpmContext;
import org.jbpm.graph.def.ProcessDefinition;

public class ProcessDefinitionHelper {

	public static List<ProcessDefinitionBean> getProcessDefinitionBeanList(JbpmContext jbpmContext)
	{
		List<ProcessDefinitionBean> processDefinitionList = new ArrayList<ProcessDefinitionBean>();
		Iterator iter = jbpmContext.getGraphSession().findLatestProcessDefinitions().iterator();
		while(iter.hasNext())
		{
			ProcessDefinition definition = (ProcessDefinition)iter.next();
			ProcessDefinitionBean processDefinitionBean = new ProcessDefinitionBean();
			processDefinitionBean.setProcessDefinition(definition);
			processDefinitionBean.setJbpmContext(jbpmContext);
			processDefinitionList.add(processDefinitionBean);
		}
		return processDefinitionList;
		
	}
	
	public static ProcessDefinitionBean getProcessDefinitionBean(String id, JbpmContext jbpmContext) throws Exception
	{
		ProcessDefinition definition = jbpmContext.getGraphSession().findLatestProcessDefinition(id);
		if (definition == null)
		{
			throw new Exception(MessageFormat.format("Process definition {0} not found", id));
		}
		ProcessDefinitionBean processDefinitionBean = new ProcessDefinitionBean();
		processDefinitionBean.setProcessDefinition(definition);
		processDefinitionBean.setJbpmContext(jbpmContext);
		return processDefinitionBean;
		
	}

	public static boolean hasProcessDefinition(String id, JbpmContext jbpmContext)
	{
		if (jbpmContext.getGraphSession().findLatestProcessDefinition(id) == null)
		{
			return false;
		}
		return true;
	}
	/*
	public static List<ProcessBean> getRoleLimitedProcessList(JbpmContext jbpmContext, HttpServletRequest req, ProcessRoleMapper processRoleMapper)
	{
		List<ProcessBean> availableProcessBeanList = new ArrayList<ProcessBean>();
		List<ProcessBean> processBeanList = getProcessList(jbpmContext);
		ActorBean actorBean = new ActorBean();
		actorBean.setJbpmContext(jbpmContext);
		actorBean.setId(req.getRemoteUser());
		for(ProcessBean processBean : processBeanList)
		{
			log.debug(MessageFormat.format("Check user {0} to process {1}", actorBean.getId(), processBean.getName()));
			for(String role : processRoleMapper.getRoles(processBean.getName()))
			{
				log.debug(MessageFormat.format("Checking user {0} for role {1}", actorBean.getId(), role));
				if (actorBean.getGroupNameList().contains(role) && ! availableProcessBeanList.contains(processBean))
				{
					log.debug(MessageFormat.format("Since user is in role {0}, adding process {1}",role, processBean.getName()));
					availableProcessBeanList.add(processBean);
				}
			}
			
		}		
		return availableProcessBeanList;
	}
	*/
}
