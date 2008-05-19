package gov.loc.repository.transfer.ui.model;

import org.jbpm.graph.def.Node;

public class NodeBean extends AbstractWorkflowBean {
	private Node node;

	void setNode(Node node)
	{
		this.node = node;
	}
		
	public String getName()
	{
		return this.node.getName();
	}
	
	public String getId() {
		return this.node.getName();
	}
	
	Node getNode()
	{
		return this.node;
	}
}
