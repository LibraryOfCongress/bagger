package gov.loc.repository.packagemodeler;

import gov.loc.repository.packagemodeler.DaoAwareModelerFactory;
import gov.loc.repository.packagemodeler.dao.PackageModelDAO;
import gov.loc.repository.packagemodeler.dao.impl.PackageModelDAOImpl;
import gov.loc.repository.packagemodeler.impl.DaoAwareModelerFactoryImpl;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.TestFixtureHelper;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

public abstract class AbstractModelersTest {

	protected TestFixtureHelper fixtureHelper = new TestFixtureHelper();
	protected DaoAwareModelerFactory modelerFactory = new DaoAwareModelerFactoryImpl();
	protected PackageModelDAO dao = new PackageModelDAOImpl();
	private boolean isSetup = false;
	protected Session session;
	protected SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	protected static int testCounter = 0;
	
	@Before
	public void baseSetup() throws Exception
	{
		modelerFactory.setPackageModelerDao(this.dao);
		testCounter++;
		
		if (! isSetup)
		{
			HibernateUtil.createDatabase();
			session = sessionFactory.getCurrentSession();
			session.beginTransaction();
	
			this.createFixtures();
			
			session.getTransaction().commit();
			isSetup = true;
		}

		session = sessionFactory.getCurrentSession();
		session.beginTransaction();
		
		this.setup();
	}
	
	public abstract void createFixtures() throws Exception;
	
	public abstract void setup() throws Exception;
	
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
	
	
	
}
