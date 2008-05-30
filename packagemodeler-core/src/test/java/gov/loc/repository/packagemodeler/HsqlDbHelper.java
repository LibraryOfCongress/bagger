package gov.loc.repository.packagemodeler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.mapping.Table;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DataSourceUtils;
import org.springframework.orm.hibernate3.LocalSessionFactoryBean;

import java.sql.ResultSet;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class HsqlDbHelper {
		
	private LocalSessionFactoryBean factoryBean;
	private JdbcTemplate template;
	
	private static final Log log = LogFactory.getLog(HsqlDbHelper.class);
	
	private static final String CREATE_SCHEMA_SQL = "create schema {0} authorization dba;";
	private static final String DROP_SCHEMA_SQL = "drop schema {0} cascade;";
	
	public HsqlDbHelper(LocalSessionFactoryBean factoryBean) throws Exception {
		this.factoryBean = factoryBean;
		this.template = new JdbcTemplate(factoryBean.getDataSource());
	}
			
	private boolean schemaExists(String schema) throws Exception
	{
		ResultSet schemaResultSet = DataSourceUtils.getConnection(factoryBean.getDataSource()).getMetaData().getSchemas();
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
	
	private void executeSchemaCreateSql() throws Exception
	{
		Set<String> schemas = findSchemaDefinitions();
		for(String schema : schemas)
		{
			if (! this.schemaExists(schema))
			{
				log.debug("Creating schema " + schema);
				this.template.execute(MessageFormat.format(CREATE_SCHEMA_SQL, schema));
			}
		}
		
	}

	private void executeSchemaDropSql() throws Exception
	{
		Set<String> schemas = findSchemaDefinitions();
		for(String schema : schemas)
		{
			if (this.schemaExists(schema))
			{
				log.debug("Dropping schema " + schema);
				this.template.execute(MessageFormat.format(DROP_SCHEMA_SQL, schema));
			}
		}
		
	}
	
	
	public void createDatabase() throws Exception
	{
				
		log.info("Creating schemas");
		executeSchemaCreateSql();
		
		//Create
		log.info("Creating database");
		this.factoryBean.createDatabaseSchema();
		//export.execute(false, true, false, true);
	}
	
	public void dropDatabase() throws Exception
	{
		//log.info("Dropping database");
		//export.execute(false, true, true, false);
		//this.factoryBean.dropDatabaseSchema();
		
		log.info("Dropping schemas");
		executeSchemaDropSql();
		
	}
	
	@SuppressWarnings("unchecked")
	private Set<String> findSchemaDefinitions()
	{
		Set<String> schemas = new HashSet<String>();
		Iterator<Table> each = this.factoryBean.getConfiguration().getTableMappings();
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
