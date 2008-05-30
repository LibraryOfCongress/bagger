package gov.loc.repository.transfer.components;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

public abstract class AbstractPackageModelerAwareComponent extends AbstractComponent{
	
	protected ModelerFactory factory;
	protected PackageModelDAO dao;	

	public AbstractPackageModelerAwareComponent(ModelerFactory factory, PackageModelDAO dao) {
		this.factory = factory;
		this.dao = dao;
	}	
		
	protected Agent getReportingAgent() throws Exception
	{
		return this.dao.findRequiredAgent(Agent.class, this.getReportingAgentId());
	}
	
}
