package gov.loc.repository.packagemodeler;

import gov.loc.repository.constants.Roles;
import gov.loc.repository.packagemodeler.agents.Role;

import org.junit.Before;
import org.junit.BeforeClass;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class AbstractModelersTest {
	
	private static boolean isSetup = false;
	protected static int testCounter = 0;
	public static Role storageSystemRole;
	
	@Autowired
	public TestFixtureHelper fixtureHelper;
	
	@Autowired
	public HsqlDbHelper dbHelper;	
		
	@BeforeClass
	public static void beforeClassSetup() throws Exception
	{
		testCounter = 0;
		isSetup = false;		
	}
	
	@Before
	public void baseSetup() throws Exception
	{
		testCounter++;
		
		if (! isSetup)
		{				
			dbHelper.dropDatabase();
			dbHelper.createDatabase();
			storageSystemRole = fixtureHelper.createRole(Roles.STORAGE_SYSTEM);
			
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
	
}
