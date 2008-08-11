package gov.loc.repository.transfer.components;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

public abstract class AbstractPackageModelerAwareComponent extends AbstractComponent implements ReportingAgentAware {
	
	protected ModelerFactory factory;
	protected PackageModelDAO dao;
	protected String reportingAgentId;

	public AbstractPackageModelerAwareComponent(ModelerFactory factory, PackageModelDAO dao, String reportingAgentId) {
		this.factory = factory;
		this.dao = dao;
		this.reportingAgentId = reportingAgentId;
	}	
		
	public Agent getReportingAgent() throws Exception
	{
		return this.dao.findRequiredAgent(Agent.class, this.getReportingAgentId());
	}
	
	public String getReportingAgentId() throws Exception
	{
		return this.reportingAgentId;
	}
	
	
}
