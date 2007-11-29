package gov.loc.repository.workflow;

import static gov.loc.repository.constants.Agents.*;
import static gov.loc.repository.workflow.constants.FixtureConstants.*;
import gov.loc.repository.packagemodeler.ModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.ModelerFactoryImpl;
import gov.loc.repository.utilities.persistence.TestFixtureHelper;
import gov.loc.repository.utilities.ResourceHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.workflow.utilities.HandlerHelper;
import gov.loc.repository.packagemodeler.packge.Package;

import java.io.File;
import java.net.URL;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

public abstract class BaseHandlerTest {
	protected TestFixtureHelper fixtureHelper = new TestFixtureHelper();
	private boolean isSetup = false;
	protected Session session;
	protected SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	protected static int testCounter = 0;
	protected PackageModelDAO dao = new PackageModelDAOImpl();
	protected ModelerFactory factory = new ModelerFactoryImpl();

	protected static Configuration configuration;
	static
	{
		try
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			URL url = BaseHandlerTest.class.getClassLoader().getResource("workflow.core.cfg.xml");
			builder.setURL(url);
			configuration = builder.getConfiguration(true);
		}
		catch(ConfigurationException ex)
		{
			throw new RuntimeException();
		}
	}	
	
	
	@Before
	public void baseSetup() throws Exception
	{
		testCounter++;
		
		if (! isSetup)
		{
			HandlerHelper helper = new HandlerHelper(null, configuration, null);

			HibernateUtil.createDatabase();
			session = sessionFactory.openSession();
			session.beginTransaction();
			fixtureHelper.setSession(session);
			
			fixtureHelper.createRepository(REPOSITORY_ID);
			fixtureHelper.createStorageSystem(RS25);
			fixtureHelper.createStorageSystem(RDC);
			fixtureHelper.createSystem(helper.getRequiredConfigString("agent.workflow.id"));
			fixtureHelper.createPerson(PERSON_ID1, PERSON_FIRSTNAME1, PERSON_SURNAME1);

			this.createFixtures();
			
			session.getTransaction().commit();
			
			this.setupOnce();
			
			isSetup = true;
		}

		session = sessionFactory.openSession();
		session.beginTransaction();
		dao.setSession(session);
		fixtureHelper.setSession(session);
		Package packge = factory.createPackage(Package.class, dao.findRequiredRepository(REPOSITORY_ID), PACKAGE_ID1 + testCounter);
		dao.save(packge);
		this.setup();
	}
	
	public void createFixtures() throws Exception
	{		
	}
	
	public void setup() throws Exception
	{		
	}
	
	public void setupOnce() throws Exception
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
		
		session = sessionFactory.openSession();
		session.beginTransaction();
		dao.setSession(session);
		session.clear();		
	}
		
	protected File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this.getClass(), filename);
	}
	
	protected static File getFile(String filename, Class clazz) throws Exception
	{
		return ResourceHelper.getFile(clazz, filename);
	}
	
		
}
