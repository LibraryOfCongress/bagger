package gov.loc.repository.bagger.jdbc;

import java.util.Collection;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.Organization;

import org.springframework.dao.DataAccessException;

/**
 * Interface that defines a cache refresh operation.
 * To be exposed for management via JMX.
 * 
 * @author Rob Harrop
 * @author Juergen Hoeller
 * @see JdbcBagger
 */
public interface JdbcBaggerMBean {

	void refreshCache();

	/* */
	Collection<Organization> getOrganizations() throws DataAccessException;

	Collection<Project> getProjects() throws DataAccessException;

	Collection<Organization> findOrganizations(String name) throws DataAccessException;

	Organization loadOrganization(int id) throws DataAccessException;

	void storeProfile(Profile prof) throws DataAccessException;
	
	void storeOrganization(Organization org) throws DataAccessException;
/* */
}
