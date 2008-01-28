package gov.loc.repository.workflow.processdefinitions;

import org.apache.commons.configuration.Configuration;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.jbpm.JbpmConfiguration;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;
import org.jbpm.JbpmContext;
import org.jbpm.identity.Entity;
import org.jbpm.identity.xml.IdentityXmlParser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.InputStream;
import java.io.ByteArrayInputStream;

import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.TestFixtureHelper;
import gov.loc.repository.utilities.persistence.HibernateUtil.DatabaseRole;
import gov.loc.repository.workflow.WorkflowConstants;
import gov.loc.repository.workflow.utilities.HandlerHelper;

/**
 * An abstract class used to simplify testing of ProcessDefinitions.
 */
public abstract class AbstractProcessDefinitionTest {

	protected static JbpmConfiguration jbpmConfiguration = JbpmConfiguration.getInstance();

	protected TestFixtureHelper fixtureHelper = new TestFixtureHelper();
	protected Session session;
	protected SessionFactory sessionFactory = HibernateUtil.getSessionFactory(DatabaseRole.SUPER_USER);
	protected HandlerHelper helper;
		
	@Before
	public void baseSetup() throws Exception
	{
		setupJbpm();
		HibernateUtil.createDatabase();
					
		helper = new HandlerHelper(null, getConfiguration(), null);			
		session = sessionFactory.openSession();
		session.beginTransaction();
		fixtureHelper.setSession(session);
		
		this.createFixtures();
		
		session.getTransaction().commit();
		
		session = sessionFactory.openSession();
		session.beginTransaction();
		fixtureHelper.setSession(session);
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
		session.clear();		
		
		session = sessionFactory.openSession();
		session.beginTransaction();
	}
	
	
	public void setupJbpm() throws Exception
	{		
		DbPersistenceServiceFactory dbPersistenceServiceFactory = (DbPersistenceServiceFactory)jbpmConfiguration.getServiceFactory(Services.SERVICENAME_PERSISTENCE);
		dbPersistenceServiceFactory.createSchema();
	}

	/*
	@AfterClass
	public static void tearDownAfterClass() throws Exception
	{
		DbPersistenceServiceFactory dbPersistenceServiceFactory = (DbPersistenceServiceFactory)jbpmConfiguration.getServiceFactory(Services.SERVICENAME_PERSISTENCE);
		dbPersistenceServiceFactory.dropSchema();		
	}
	*/
				
	protected void loadIdentities(String identities) throws Exception
	{
		InputStream stream = new ByteArrayInputStream(identities.getBytes());
		Entity[] entities = IdentityXmlParser.parseEntitiesResource(stream);

		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
	    try {
	      Session session = jbpmContext.getSession();
	      for (int i=0; i<entities.length; i++) {
	        session.save(entities[i]);
	      }
	    } finally {
	      jbpmContext.close();
	    }		
		
	}
	
	protected Configuration getConfiguration() throws Exception
	{
		return ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
}
