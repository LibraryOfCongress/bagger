package gov.loc.repository.workflow;

import gov.loc.repository.packagemodeler.HsqlDbHelper;
import gov.loc.repository.packagemodeler.TestFixtureHelper;
import gov.loc.repository.utilities.ConfigurationFactory;
import gov.loc.repository.utilities.ResourceHelper;
import gov.loc.repository.workflow.jbpm.spring.LocalSessionFactoryBean;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;

import javax.annotation.Resource;

import org.apache.commons.configuration.Configuration;
import org.hibernate.Session;
import org.jbpm.JbpmConfiguration;
import org.jbpm.JbpmContext;
import org.jbpm.db.hibernate.HibernateHelper;
import org.jbpm.graph.def.ProcessDefinition;
import org.jbpm.graph.exe.ProcessInstance;
import org.jbpm.identity.Entity;
import org.jbpm.identity.xml.IdentityXmlParser;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath:conf/workflow-core-context.xml","classpath:conf/packagemodeler-core-test-context.xml"})
public abstract class AbstractHandlerTest {

	private static boolean isSetup = false;
	public static int testCounter = 0;
	
	@Autowired
	public TestFixtureHelper fixtureHelper;
	
	@Autowired
	public HsqlDbHelper dbHelper;	
	
	@Resource(name="&jbpmSessionFactory")
	protected LocalSessionFactoryBean sessionFactoryBean;
	
	@Autowired
	protected JbpmConfiguration jbpmConfiguration;
		
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

		
		sessionFactoryBean.dropDatabaseSchema();		
		sessionFactoryBean.createDatabaseSchema();
		
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();
		try
		{
			HibernateHelper.clearHibernateCache(jbpmContext.getSessionFactory());
		}
		finally
		{
			jbpmContext.close();
		}
		
		if (! isSetup)
		{
			dbHelper.dropDatabase();
			dbHelper.createDatabase();
			
			this.createCommonFixtures();
			this.createFixtures();
			
			this.setupOnce();
			
			isSetup = true;
		}
		
		this.setup();
	}
	
	public void createCommonFixtures() throws Exception
	{
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
	
			
	protected File getFile(String filename) throws Exception
	{
		return ResourceHelper.getFile(this.getClass(), filename);
	}
	
	protected static File getFile(String filename, Class<?> clazz) throws Exception
	{
		return ResourceHelper.getFile(clazz, filename);
	}
	
	protected Configuration getConfiguration() throws Exception
	{
		return ConfigurationFactory.getConfiguration(WorkflowConstants.PROPERTIES_NAME);
	}
	
	
	@After
	public void teardown() throws Exception
	{
		sessionFactoryBean.dropDatabaseSchema();
	}
	
	public Long deployAndCreateProcessInstance(String processDefinitionString) throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlString(processDefinitionString);
		
		return this.deployAndCreateProcessInstance(processDefinition);
	}

	public Long deployAndCreateProcessInstance(ProcessDefinition processDefinition) throws Exception
	{
		JbpmContext jbpmContext = jbpmConfiguration.createJbpmContext();		
		try
		{
			jbpmContext.deployProcessDefinition(processDefinition);
			ProcessInstance processInstance = jbpmContext.newProcessInstance(processDefinition.getName());
			return processInstance.getId();
		}
		finally
		{
			jbpmContext.close();
		}	    		
	}
	
	
	public Long deployResourceAndCreateProcessInstance(String processDefinitionResource) throws Exception
	{
		ProcessDefinition processDefinition = ProcessDefinition.parseXmlResource(processDefinitionResource);
		return this.deployAndCreateProcessInstance(processDefinition);
	}
	
	
	public void loadIdentities(String identities) throws Exception
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
