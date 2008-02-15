package gov.loc.repository.transfer.ui.workflow.beans;

import org.jbpm.graph.exe.ProcessInstance;

public class ProcessInstanceBean extends AbstractWorkflowBean {
	
	private ProcessInstance processInstance;
	
	public void setProcessInstance(ProcessInstance processInstance) {
		this.processInstance = processInstance;
	}

	public ProcessInstance getProcessInstance() {
		return processInstance;
	}

	public String getPackageName()
	{		
		return (String)this.processInstance.getContextInstance().getVariable("packageId");
	}
	
	public ProcessDefinitionBean getProcessDefinitionBean()
	{
		ProcessDefinitionBean processDefinitionBean = new ProcessDefinitionBean();
		processDefinitionBean.setJbpmContext(this.jbpmContext);
		processDefinitionBean.setProcessDefinition(this.processInstance.getProcessDefinition());
		return processDefinitionBean;
	}
	
	public long getId()
	{
		return this.processInstance.getId();
	}
	
	/*
	public Map getVariableMap()
	{
		return this.processInstance.getContextInstance().getVariables();
	}
	
	public boolean isVariableChange(String name, String value)
	{
		if (value.equals(this.processInstance.getContextInstance().getVariable(name)))
		{
			return false;
		}
		return true;
	}
	
	public void setVariable(String name, String value)
	{
		if (this.isVariableChange(name, value))
		{
			log.debug(MessageFormat.format("Setting variable {0} to {1}", name, value));
			this.processInstance.getContextInstance().setVariable(name, value);
		}
	}
	*/
	/*
	public List<TokenBean> getTokenList()
	{
		List<TokenBean> tokenBeanList = new ArrayList<TokenBean>();
		List tokenList = this.processInstance.findAllTokens();
		log.debug("TokenList has " + tokenList.size());
		Iterator iter = tokenList.iterator();
		while(iter.hasNext())
		{			
			TokenBean tokenBean = new TokenBean();
			tokenBean.setToken((Token)iter.next());
			tokenBean.setJbpmContext(jbpmContext);
			tokenBeanList.add(tokenBean);
		}
		log.debug("TokenBeanList has " + tokenBeanList.size());
		return tokenBeanList;
	}
	*/
	
	public void save()
	{
		this.jbpmContext.save(this.processInstance);
	}
	
	/*	
	public boolean isSuspended()
	{
		return this.processInstance.isSuspended();
	}
	
	public void resume()
	{
		if (this.isSuspended())
		{
			this.processInstance.resume();
		}
	}
	*/
	public boolean isEnded()
	{
		return this.processInstance.hasEnded();
	}
	/*
	public void end()
	{
		this.processInstance.end();
	}
	
	public String getMessage()
	{
		return (String)this.processInstance.getContextInstance().getVariable("message");
	}
	*/
}
