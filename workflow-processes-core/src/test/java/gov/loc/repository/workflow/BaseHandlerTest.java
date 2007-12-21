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
import gov.loc.repository.workflow.utilities.ConfigurationHelper;
import gov.loc.repository.workflow.utilities.HandlerHelper;
import gov.loc.repository.packagemodeler.packge.Package;

import java.io.File;

import org.apache.commons.configuration.Configuration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;

public abstract class BaseHandlerTest {
	protected TestFixtureHelper fixtureHelper = new TestFixtureHelper();
	protected static boolean isSetup = false;
	protected Session session;
	protected SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	protected static int testCounter = 0;
	protected PackageModelDAO dao = new PackageModelDAOImpl();
	protected ModelerFactory factory = new ModelerFactoryImpl();
	
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
			HandlerHelper helper = new HandlerHelper(null, getConfiguration(), null);

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
	
	protected Configuration getConfiguration()
	{
		return ConfigurationHelper.getConfiguration();
	}
		
}
