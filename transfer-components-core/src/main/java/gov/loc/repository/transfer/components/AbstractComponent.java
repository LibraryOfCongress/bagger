package gov.loc.repository.transfer.components;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public abstract class AbstractComponent {

	private Log reportingLog;
	private Log log;	
		
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
			
}
