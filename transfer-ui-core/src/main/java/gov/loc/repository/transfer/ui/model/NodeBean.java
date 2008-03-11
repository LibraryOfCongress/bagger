package gov.loc.repository.transfer.ui.model;

import org.jbpm.graph.def.Node;

public class NodeBean extends AbstractWorkflowBean {
	private Node node;
	
	public void setNode(Node node)
	{
		this.node = node;
	}
	
	public Node getNode()
	{
		return this.node;
	}
	
	public String getName()
	{
		return this.node.getName();
	}
}
