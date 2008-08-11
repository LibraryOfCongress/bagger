package gov.loc.repository.transfer.ui.model;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.jbpm.graph.def.Node;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;

public class ProcessDefinitionBean extends AbstractWorkflowBean {

	private ProcessDefinition processDefinition;
	
	public String getId() {
		if (this.processDefinition != null) {
			return this.processDefinition.getName();
		} else {
			return null;
		}
	}

	public String getName() {
		return this.getMessage("processdefinition." + this.getId(), this.getId());
	}
	
	void setProcessDefinition(ProcessDefinition definition) {
		this.processDefinition = definition;
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof ProcessDefinitionBean && 
		    this.getId().equals(((ProcessDefinitionBean)obj).getId())) {
			return true;
		}
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public List<ProcessInstanceBean> getProcessInstanceBeanList()
	{
		List<ProcessInstanceBean> processInstanceBeanList = new ArrayList<ProcessInstanceBean>();
		List<ProcessDefinition> processDefinitionList = 
		    this.jbpmContext.getGraphSession().findAllProcessDefinitionVersions(
		        this.processDefinition.getName()
		    );
		for(ProcessDefinition processDefinition : processDefinitionList) {
			List<ProcessInstance> processInstanceList =
			    this.jbpmContext.getGraphSession().findProcessInstances(
			        processDefinition.getId()
			    );
			for(ProcessInstance processInstance : processInstanceList) {
				if (! processInstance.hasEnded()) {
					
					ProcessInstanceBean processInstanceBean = this.factory.createProcessInstanceBean(processInstance);
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
	
	@SuppressWarnings("unchecked")
	public List<NodeBean> getNodeBeanList()
	{
		List<NodeBean> nodeBeanList = new ArrayList<NodeBean>();
		List<Node> nodeList = this.processDefinition.getNodes();
		Iterator<Node> iter = nodeList.iterator();
		while(iter.hasNext())
		{
			NodeBean nodeBean = this.factory.createNodeBean(iter.next());
			nodeBeanList.add(nodeBean);
		}		
		return nodeBeanList;
	}
	
	public NodeBean getNodeBean(String nodeName)
	{
		Node node = this.processDefinition.findNode(nodeName);
		if (node == null)
		{
			return null;
		}
		return this.factory.createNodeBean(node);
				
	}
}
