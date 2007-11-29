package gov.loc.repository.workflow.processdefinitions;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.DefaultConfigurationBuilder;
import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.jbpm.JbpmConfiguration;
import org.jbpm.persistence.db.DbPersistenceServiceFactory;
import org.jbpm.svc.Services;
import org.jbpm.JbpmContext;
import org.jbpm.configuration.ObjectFactoryParser;
import org.jbpm.configuration.ObjectFactoryImpl;
import org.jbpm.util.ClassLoaderUtil;
import org.jbpm.configuration.ObjectInfo;
import org.jbpm.configuration.ValueInfo;
import org.jbpm.identity.Entity;
import org.jbpm.identity.xml.IdentityXmlParser;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.net.URL;

import gov.loc.repository.utilities.persistence.HibernateUtil;
import gov.loc.repository.utilities.persistence.TestFixtureHelper;
import gov.loc.repository.workflow.actionhandlers.BaseActionHandler;
import gov.loc.repository.workflow.jbpm.configuration.DelegatingObjectFactoryImpl;
import gov.loc.repository.workflow.utilities.HandlerHelper;

/**
 * An abstract class used to simplify testing of ProcessDefinitions.
 */
public abstract class AbstractProcessDefinitionTest {

	protected static JbpmConfiguration jbpmConfiguration;
	protected DelegatingObjectFactoryImpl objectFactory;

	protected TestFixtureHelper fixtureHelper = new TestFixtureHelper();
	private boolean isSetup = false;
	protected Session session;
	protected SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
	protected static int testCounter = 0;		
	protected HandlerHelper helper;
	
	protected static Configuration configuration;
	static
	{
		try
		{
			DefaultConfigurationBuilder builder = new DefaultConfigurationBuilder();
			URL url = BaseActionHandler.class.getClassLoader().getResource("workflow.core.cfg.xml");
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
			this.setupJbpm();
			
			helper = new HandlerHelper(null, configuration, null);
			
			HibernateUtil.createDatabase();
			session = sessionFactory.openSession();
			session.beginTransaction();
			fixtureHelper.setSession(session);
			
			this.createFixtures();
			
			session.getTransaction().commit();
			isSetup = true;
		}
		objectFactory.clear();
		
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
		//DbPersistenceServiceFactory dbPersistenceServiceFactory = (DbPersistenceServiceFactory)jbpmConfiguration.getServiceFactory(Services.SERVICENAME_PERSISTENCE);
		//dbPersistenceServiceFactory.dropSchema();	
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
		ObjectFactoryParser objectFactoryParser = new ObjectFactoryParser();
	    ObjectFactoryImpl objectFactoryImpl = new ObjectFactoryImpl();
	    objectFactoryParser.parseElementsFromResource("org/jbpm/default.jbpm.cfg.xml", objectFactoryImpl);
	    
	    InputStream jbpmCfgXmlStream = ClassLoaderUtil.getStream("jbpm.cfg.xml");
	    objectFactoryParser.parseElementsStream(jbpmCfgXmlStream, objectFactoryImpl);
	    
		objectFactory = new DelegatingObjectFactoryImpl(objectFactoryImpl);
		JbpmConfiguration.Configs.setDefaultObjectFactory(objectFactory);
		jbpmConfiguration = JbpmConfiguration.getInstance();
		ProcessDefinitionHelper.setJbpmConfiguration(jbpmConfiguration);

		// now we make the bean jbpm.configuration always availble 
		ObjectInfo jbpmConfigurationInfo = new ValueInfo("jbpmConfiguration", jbpmConfiguration);
	    objectFactoryImpl.addObjectInfo(jbpmConfigurationInfo);
		
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
			
	/**
	 * Provide an object to be returned by jBPM's ObjectFactory.
	 * <p>This can be used to provide mock objects.
	 * <p>Note, however, that the ObjectFactory is not used to create ActionHandlers.
	 * See jbpm.cfg.xml. 
	 * @param name
	 * @param mock
	 */
	public void registerObject(String name, Object obj)
	{
		objectFactory.registerObject(name, obj);
	}
	
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
	
}
