package gov.loc.repository.utilities.persistence;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.AnnotationConfiguration;
import org.hibernate.connection.ConnectionProvider;
import org.hibernate.connection.ConnectionProviderFactory;
import org.hibernate.mapping.Table;
import org.hibernate.tool.hbm2ddl.SchemaExport;

import gov.loc.repository.packagemodeler.ConfigurationHelper;

import java.io.IOException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

public class HibernateUtil {
	
	public enum DatabaseRole { SUPER_USER, DATA_WRITER, FIXTURE_WRITER, READ_ONLY };
	
	private static Map<DatabaseRole,SessionFactory> sessionFactoryMap = new HashMap<DatabaseRole,SessionFactory>();
	private static Map<DatabaseRole,AnnotationConfiguration> hibernateConfigurationMap = new HashMap<DatabaseRole, AnnotationConfiguration>();
	
	private static final Log log = LogFactory.getLog(HibernateUtil.class);
	
	private static final String CFG_KEY = "hibernate.cfg.xml";
	private static final String PROPS_KEY = "hibernate.properties";
	private static final String CREATE_SCHEMA_SQL = "create schema {0} authorization dba;";
	
	private static AnnotationConfiguration getConfiguration(DatabaseRole databaseRole) throws ExceptionInInitializerError
	{
		if (! hibernateConfigurationMap.containsKey(databaseRole))
		{
			//Load configuration for modelers
			Configuration modelerConfiguration = ConfigurationHelper.getConfiguration();
			AnnotationConfiguration hibernateConfiguration = new AnnotationConfiguration();
			if (! modelerConfiguration.containsKey(CFG_KEY))
			{				
				throw new ExceptionInInitializerError("Configuration does not contain key " + CFG_KEY);
			}
			String cfg = modelerConfiguration.getString(CFG_KEY);
			log.debug("hibernate.cfg.xml has value " + cfg);
			hibernateConfiguration.configure(cfg);
			String propertyKey = databaseRole.toString().toLowerCase() + "." + PROPS_KEY;
			String props = modelerConfiguration.getString(propertyKey);
			if (props == null)
			{
				log.debug(MessageFormat.format("Propery with key {0} not found.  Defaulting to key {1}.", propertyKey, PROPS_KEY));
				props = modelerConfiguration.getString(PROPS_KEY);
			}
			
			log.debug("hibernate.properties is " + props);
			Properties properties = new Properties();
			try
			{
				properties.load(HibernateUtil.class.getClassLoader().getResourceAsStream(props));
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
	
	public static SessionFactory getSessionFactory(DatabaseRole databaseRole) throws ExceptionInInitializerError
	{
		if (! sessionFactoryMap.containsKey(databaseRole))
		{
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
		
	public static Set<String> findSchemaDefinitions(AnnotationConfiguration hibernateConfiguration)
	{
		Set<String> schemas = new HashSet<String>();
		Iterator each = hibernateConfiguration.getTableMappings();
		while ( each.hasNext() )
		{
			Table t = (Table)each.next();
			if ( t.isPhysicalTable() )
			{
				String schema = t.getQuotedSchema( );
				if ( schema != null ) schemas.add( schema );
			}
		}
		return schemas;
	}
	
}
