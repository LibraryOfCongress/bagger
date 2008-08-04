package gov.loc.repository.transfer.components;

import gov.loc.repository.packagemodeler.agents.Agent;

public interface ReportingAgentAware {

	public String getReportingAgentId() throws Exception;
	
	public Agent getReportingAgent() throws Exception;
	
}
