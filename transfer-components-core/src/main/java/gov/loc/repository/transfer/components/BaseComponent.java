package gov.loc.repository.transfer.components;

import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;

import java.text.MessageFormat;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class BaseComponent implements ModelerAware {

	private static final String REPORTING_AGENT_KEY = "components.agentid";
	
	private Log reportingLog;
	private Log log;	
	
	protected ModelerFactory factory;
	protected PackageModelDAO dao;	
	

	protected Configuration getConfiguration()
	{
		return ConfigurationHelper.getConfiguration();
	}
	
	protected Log getLog()
	{
		if (this.log == null)
		{
			this.log = LogFactory.getLog(this.getClass());		
		}
		return this.log;
	}
	
	protected abstract String getComponentName();
	
	protected Log getReportingLog()
	{
		if (this.reportingLog == null)
		{
			this.reportingLog = LogFactory.getLog("components." + this.getComponentName());
		}
		return this.reportingLog;
	}
	
	protected String getReportingAgentId() throws Exception
	{
		String reportingAgent = this.getConfiguration().getString(REPORTING_AGENT_KEY);
		if (reportingAgent == null)
		{
			throw new Exception(MessageFormat.format("Property {0} is missing", REPORTING_AGENT_KEY));
		}
		return reportingAgent;
	}
	
	protected Agent getReportingAgent() throws Exception
	{
		return this.dao.findRequiredAgent(Agent.class, this.getReportingAgentId());
	}
	
	public void setModelerFactory(ModelerFactory factory) {
		this.factory = factory;
		
	}
	
	public void setPackageModelDao(PackageModelDAO dao) {
		this.dao = dao;
		
	}
	
}
