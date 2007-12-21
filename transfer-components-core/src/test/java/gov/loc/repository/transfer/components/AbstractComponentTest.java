package gov.loc.repository.transfer.components;

import java.io.File;
import java.text.MessageFormat;

import org.apache.commons.configuration.Configuration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

import gov.loc.repository.packagemodeler.DaoAwareModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.DaoAwareModelerFactoryImpl;
import gov.loc.repository.transfer.components.utilities.ConfigurationHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.ResourceHelper;
import gov.loc.repository.utilities.persistence.TestFixtureHelper;

public abstract class AbstractComponentTest {

	protected TestFixtureHelper fixtureHelper = new TestFixtureHelper();
	protected static boolean isSetup = false;
	protected Session session;
	protected SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	protected static int testCounter = 0;
	protected DaoAwareModelerFactory modelerFactory = new DaoAwareModelerFactoryImpl();
	protected PackageModelDAO packageModelDao = new PackageModelDAOImpl();
	
	private static final String REPORTING_AGENT_KEY = "components.agentid";
			
	public AbstractComponentTest() {
		this.modelerFactory.setPackageModelerDao(this.packageModelDao);
	}
	
	@BeforeClass
	public static void beforeClassSetup() throws Exception
	{
		HibernateUtil.createDatabase();
		isSetup = false;
		testCounter = 0;
	}
	
	@Before
	public void baseSetup() throws Exception
	{		
		testCounter++;
		if (! isSetup)
		{
			session = sessionFactory.getCurrentSession();
			session.beginTransaction();
	
			this.fixtureHelper.createSoftware(this.getReportingAgent());
			this.createFixtures();
			
			session.getTransaction().commit();
			isSetup = true;
		}

		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
						
		this.setup();
	}
	
	public void createFixtures() throws Exception
	{		
	}
	
	public void setup() throws Exception
	{		
	}
	
	@After
	public void teardown() throws Exception
	{
		if (session.isOpen())
		{
			session.getTransaction().commit();
		}
	}
	
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		HibernateUtil.shutdown();
	}

	protected void commitAndRestartTransaction() throws Exception
	{
		session.getTransaction().commit();
		
		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		session.clear();		
	}
		
	protected File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this.getClass(), filename);
	}
	
	protected Configuration getConfiguration()
	{
		return ConfigurationHelper.getConfiguration();
	}
	
	protected String getReportingAgent() throws Exception
	{
		String reportingAgent = this.getConfiguration().getString(REPORTING_AGENT_KEY);
		if (reportingAgent == null)
		{
			throw new Exception(MessageFormat.format("Property {0} is missing", REPORTING_AGENT_KEY));
		}
		return reportingAgent;
	}
	

}
