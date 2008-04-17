package gov.loc.repository.utilities.persistence;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import gov.loc.repository.utilities.ResourceResolver;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HibernateUtil {
	
	public enum DatabaseRole { SUPER_USER, DATA_WRITER, FIXTURE_WRITER, READ_ONLY };
	
	private static Map<DatabaseRole,SessionFactory> sessionFactoryMap = Collections.synchronizedMap(new HashMap<DatabaseRole,SessionFactory>());
	private static Map<DatabaseRole,AnnotationConfiguration> hibernateConfigurationMap = Collections.synchronizedMap(new HashMap<DatabaseRole, AnnotationConfiguration>());
	
	private static final Log log = LogFactory.getLog(HibernateUtil.class);

	public static final String PROPS_RESOURCE = "packagemodeler.hibernate.properties";
	
	private static final String CREATE_SCHEMA_SQL = "create schema {0} authorization dba;";
	
	private static AnnotationConfiguration getConfiguration(DatabaseRole databaseRole) throws ExceptionInInitializerError
	{
		if (! hibernateConfigurationMap.containsKey(databaseRole))
		{
			AnnotationConfiguration hibernateConfiguration = new AnnotationConfiguration();
			//Find packagemodeler.*.hibernate.cfg.xml
			List<URL> resourceList = ResourceResolver.findWildcardResourceList("conf/packagemodeler.*.hibernate.cfg.xml");
			if (resourceList.isEmpty())
			{
				throw new ExceptionInInitializerError("No hibernate.cfg.xml files found");
			}
			for(URL resource : resourceList)
			{
				log.debug("Using hibernate.cfg.xml: " + resource.toString());
				hibernateConfiguration.configure(resource);
			}

			//Find packagemodeler.hibernate.properties
			//First try databaserole.packagemodeler.hibernate.properties
			String resourceString = "conf/" + databaseRole.toString().toLowerCase() + "." + PROPS_RESOURCE;
			if (HibernateUtil.class.getClassLoader().getResource(resourceString) == null)
			{
				log.debug(MessageFormat.format("Since resource {0} not found, trying {1}", resourceString, PROPS_RESOURCE));
				resourceString = "conf/" + PROPS_RESOURCE;
			}
			log.debug("Using hibernate.properties: " + resourceString);
			Properties properties = new Properties();
			try
			{
				properties.load(HibernateUtil.class.getClassLoader().getResourceAsStream(resourceString));
			}
			catch(IOException ex)
			{
				throw new ExceptionInInitializerError(ex);
			}
			hibernateConfiguration.mergeProperties(properties);
			hibernateConfiguration.setInterceptor(new TimestampedInterceptor());
			hibernateConfigurationMap.put(databaseRole, hibernateConfiguration);
		}
		return hibernateConfigurationMap.get(databaseRole);
		
	}
	
	public synchronized static SessionFactory getSessionFactory(DatabaseRole databaseRole) throws ExceptionInInitializerError
	{
		if (! sessionFactoryMap.containsKey(databaseRole))
		{
			log.debug("Adding sessionFactory for " + databaseRole);
			sessionFactoryMap.put(databaseRole, getConfiguration(databaseRole).buildSessionFactory());
		}
		return sessionFactoryMap.get(databaseRole);
	}
		
	public static void shutdown()
	{
		for(SessionFactory factory : sessionFactoryMap.values())
		{
			factory.close();
		}

	}
	
	private static Connection prepareConnection(DatabaseRole databaseRole) throws Exception {
		log.info("Preparing connection for " + databaseRole.toString());
		Properties cfgProperties = getConfiguration(databaseRole).getProperties();
		ConnectionProvider connectionProvider = ConnectionProviderFactory.newConnectionProvider( cfgProperties );
		Connection connection = connectionProvider.getConnection();
		if ( !connection.getAutoCommit() ) {
			connection.commit();
			connection.setAutoCommit( true );
		}
		return connection;
	}	
	
	private static boolean schemaExists(Connection connection, String schema) throws Exception
	{
		ResultSet schemaResultSet = connection.getMetaData().getSchemas();
		while(schemaResultSet.next())
		{
			if (schema.equalsIgnoreCase(schemaResultSet.getString("TABLE_SCHEM")))
			{
				log.debug(MessageFormat.format("Schema {0} exists already", schema));
				return true;
			}
		}
		log.debug(MessageFormat.format("Schema {0} doesn't already exist", schema));
		return false;
	}
	
	private static void executeSchemaCreateSql(Connection connection, AnnotationConfiguration hibernateConfiguration) throws Exception
	{
		Statement statement = connection.createStatement();
		Set<String> schemas = findSchemaDefinitions(hibernateConfiguration);
		for(String schema : schemas)
		{
			if (! schemaExists(connection, schema))
			{
				String sql = MessageFormat.format(CREATE_SCHEMA_SQL, schema);
				log.debug(sql);
				statement.executeUpdate(sql);
			}
		}
		
	}
	
	
	public static void createDatabase() throws Exception
	{
		
		Connection connection = prepareConnection(DatabaseRole.SUPER_USER);
		AnnotationConfiguration hibernateConfiguration = getConfiguration(DatabaseRole.SUPER_USER); 
		SchemaExport export = new SchemaExport(hibernateConfiguration);
		log.info("Creating schemas");
		executeSchemaCreateSql(connection, hibernateConfiguration);
		
		//Drop
		log.info("Dropping database");
		export.execute(false, true, true, false);

		//Create
		log.info("Creating database");		
		export.execute(false, true, false, true);
	}
		
	@SuppressWarnings("unchecked")
	public static Set<String> findSchemaDefinitions(AnnotationConfiguration hibernateConfiguration)
	{
		Set<String> schemas = new HashSet<String>();
		Iterator<Table> each = hibernateConfiguration.getTableMappings();
		while ( each.hasNext() )
		{
			Table t = each.next();
			if ( t.isPhysicalTable() )
			{
				String schema = t.getQuotedSchema( );
				if ( schema != null ) schemas.add( schema );
			}
		}
		return schemas;
	}
	
}
