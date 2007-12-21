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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

public class HibernateUtil {
	private static final Log log = LogFactory.getLog(HibernateUtil.class);
	private static SessionFactory sessionFactory;
	private static AnnotationConfiguration hibernateConfiguration;
	private static SchemaExport export;
	
	private static final String CFG_KEY = "hibernate.cfg.xml";
	private static final String PROPS_KEY = "hibernate.properties";
	private static final String CREATE_SCHEMA_SQL = "create schema {0} authorization dba;";
	
	static
	{
		try
		{
			//Load configuration for modelers
			Configuration modelerConfiguration = ConfigurationHelper.getConfiguration();
			hibernateConfiguration = new AnnotationConfiguration();
			if (modelerConfiguration.containsKey(CFG_KEY))
			{
				String cfg = modelerConfiguration.getString(CFG_KEY);
				log.debug("hibernate.cfg.xml has value " + cfg);
				hibernateConfiguration.configure(cfg);
				if (modelerConfiguration.containsKey(PROPS_KEY))
				{
					String props = modelerConfiguration.getString(PROPS_KEY);
					log.debug("hibernate.properties is " + props);
					Properties properties = new Properties();
					properties.load(HibernateUtil.class.getClassLoader().getResourceAsStream(props));
					hibernateConfiguration.mergeProperties(properties);
				}
				
			}
			else
			{
				//Default
				hibernateConfiguration.configure();
			}
			hibernateConfiguration.setInterceptor(new TimestampedInterceptor());
			sessionFactory = hibernateConfiguration.buildSessionFactory();
			export = new SchemaExport(hibernateConfiguration);
		}
		catch (Throwable ex)
		{
			log.error(ex);
			throw new ExceptionInInitializerError(ex);
		}
		
	}
	
	public static SessionFactory getSessionFactory()
	{
		return sessionFactory;
	}
		
	public static void shutdown()
	{
		sessionFactory.close();
	}
	
	private static Connection prepareConnection() throws SQLException {
		log.info("Preparing connection");
		Properties cfgProperties = hibernateConfiguration.getProperties();
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
	
	private static void executeSchemaCreateSql(Connection connection) throws Exception
	{
		Statement statement = connection.createStatement();
		Set<String> schemas = findSchemaDefinitions();
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
		
		Connection connection = prepareConnection();
		
		log.info("Creating schemas");
		executeSchemaCreateSql(connection);
		
		//Drop
		log.info("Dropping database");
		export.execute(false, true, true, false);
		//executeSchemaDropSql(connection, DROP_SCHEMA_SQL);

		//Create
		log.info("Creating database");		
		export.execute(false, true, false, true);
	}
		
	public static Set<String> findSchemaDefinitions()
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
