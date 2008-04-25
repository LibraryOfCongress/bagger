package gov.loc.repository.commons.dbcp;

import java.util.Properties;

public class BasicDataSource extends org.apache.commons.dbcp.BasicDataSource {

	public void setHibernateProperties(Properties props)
	{
		this.setUrl(props.getProperty("hibernate.connection.url"));
		this.setUsername(props.getProperty("hibernate.connection.username"));
		this.setPassword(props.getProperty("hibernate.connection.password"));
		this.setDriverClassName(props.getProperty("hibernate.connection.driver_class"));
	}
	
}
