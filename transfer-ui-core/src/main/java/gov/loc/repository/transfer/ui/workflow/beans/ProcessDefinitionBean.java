package gov.loc.repository.transfer.ui.workflow.beans;

import java.util.ArrayList;
import java.util.List;

import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

public class ProcessDefinitionBean extends AbstractWorkflowBean {

	private ProcessDefinition processDefinition;
	
	public String getId() {
		return this.processDefinition.getName();
	}

	public void setProcessDefinition(ProcessDefinition definition)
	{
		this.processDefinition = definition;
	}

	public ProcessInstanceBean newInstance()
	{
		log.debug("Creating process instance for " + this.getId());
		ProcessInstance processInstance = jbpmContext.newProcessInstance(this.getId());
		processInstance.getRootToken().signal();
		//processInstance.getTaskMgmtInstance().createStartTaskInstance().end("continue");
		ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
		processInstanceBean.setProcessInstance(processInstance);
		processInstanceBean.setJbpmContext(jbpmContext);
		return processInstanceBean;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessDefinitionBean && this.getId().equals(((ProcessDefinitionBean)obj).getId()))
		{
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessInstanceBean> getProcessInstanceBeanList()
	{
		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List<ProcessDefinition> processDefinitionList = this.jbpmContext.getGraphSession().findAllProcessDefinitionVersions(this.processDefinition.getName());
		for(ProcessDefinition processDefinition : processDefinitionList)
		{
			List<ProcessInstance> processInstanceList = this.jbpmContext.getGraphSession().findProcessInstances(processDefinition.getId());
			for(ProcessInstance processInstance : processInstanceList)
			{
				if (! processInstance.hasEnded())
				{
					ProcessInstanceBean processInstanceBean = new ProcessInstanceBean();
					processInstanceBean.setJbpmContext(this.jbpmContext);
					processInstanceBean.setProcessInstance(processInstance);
					processInstanceBeanList.add(processInstanceBean);
				}
			}
		}
		return processInstanceBeanList;
	}
	
	/*
	public String getRepository() {
		//The repository is determined by taking the name up to a number and replacing underscores with spaces
		StringBuffer repositoryBuf = new StringBuffer();
		char[] charArray = this.getId().toCharArray();
		for(int i=0 ; i < charArray.length; i++)
		{
			if (Character.isDigit(charArray[i]))
			{
				break;
			}
			else if (charArray[i] == '_')
			{
				repositoryBuf.append(' ');
			}
			else
			{
				repositoryBuf.append(charArray[i]);
			}
		}
		return repositoryBuf.toString();

	}
	*/
	
	/*
	public List<NodeBean> getNodeList()
	{
		List<NodeBean> nodeBeanList = new ArrayList<NodeBean>();
		List nodeList = this.processDefinition.getNodes();
		Iterator iter = nodeList.iterator();
		while(iter.hasNext())
		{
			Node node = (Node)iter.next();
			NodeBean nodeBean = new NodeBean();
			nodeBean.setNode(node);
			nodeBeanList.add(nodeBean);
		}		
		return nodeBeanList;
	}
	
	public Map<String, NodeBean> getNodeMap()
	{
		Map<String, NodeBean> nodeBeanMap = new HashMap<String, NodeBean>();
		List nodeList = this.processDefinition.getNodes();
		Iterator iter = nodeList.iterator();
		while(iter.hasNext())
		{
			Node node = (Node)iter.next();
			NodeBean nodeBean = new NodeBean();
			nodeBean.setNode(node);
			nodeBeanMap.put(nodeBean.getName(), nodeBean);
		}		
		return nodeBeanMap;
		
	}
	*/
}
