package gov.loc.repository.transfer.components;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.configuration.Configuration;
import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;

import gov.loc.repository.packagemodeler.HsqlDbHelper;
import gov.loc.repository.packagemodeler.TestFixtureHelper;
import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.ResourceHelper;

public abstract class AbstractComponentTest {

	protected static boolean isSetup = false;
	protected static int testCounter = 0;
	
	private static final String REPORTING_AGENT_KEY = "components.agent.id";
	
	protected static Agent reportingAgent; 
	
	@Autowired
	public TestFixtureHelper fixtureHelper;
	
	@Autowired
	public HsqlDbHelper dbHelper;		
	
	@BeforeClass
	public static void beforeClassSetup() throws Exception
	{
		isSetup = false;
		testCounter = 0;
	}
	
	@Before
	public void baseSetup() throws Exception
	{		
		testCounter++;

		if (! isSetup)
		{
			dbHelper.dropDatabase();
			dbHelper.createDatabase();
	
			reportingAgent = this.fixtureHelper.createSoftware(this.getReportingAgentId());
			this.createFixtures();
			
			isSetup = true;
		}
						
		this.setup();
	}
	
	public void createFixtures() throws Exception
	{		
	}
	
	public void setup() throws Exception
	{		
	}
			
	protected File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this.getClass(), filename);
	}
	
	protected Configuration getConfiguration() throws Exception
	{
		return ConfigurationFactory.getConfiguration(ComponentConstants.PROPERTIES_NAME);
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
