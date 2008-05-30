package gov.loc.repository.transfer.components;

import gov.loc.repository.utilities.ConfigurationFactory;

import java.text.MessageFormat;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractComponent {

	private static final String REPORTING_AGENT_KEY = "components.agent.id";
	
	private Log reportingLog;
	private Log log;	
	
	protected Configuration getConfiguration()
	{
		return ConfigurationFactory.getConfiguration(ComponentConstants.PROPERTIES_NAME);
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
		
}
