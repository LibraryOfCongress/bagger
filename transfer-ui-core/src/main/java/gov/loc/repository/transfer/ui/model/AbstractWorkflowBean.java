package gov.loc.repository.transfer.ui.model;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jbpm.JbpmContext;

public class AbstractWorkflowBean {
	protected JbpmContext jbpmContext;

	protected static final Log log = LogFactory.getLog(AbstractWorkflowBean.class);	
	
	public void setJbpmContext(JbpmContext jbpmContext)
	{
		this.jbpmContext = jbpmContext;
	}
	
}
