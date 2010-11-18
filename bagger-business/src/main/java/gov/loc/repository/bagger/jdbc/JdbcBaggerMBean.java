package gov.loc.repository.bagger.jdbc;

import java.util.Collection;

import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.Organization;
import gov.loc.repository.bagger.ProjectBagInfo;
import gov.loc.repository.bagger.ProjectProfile;

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
	
	Collection<ProjectProfile> getProjectProfiles() throws DataAccessException;

	Collection<Organization> findOrganizations(String name) throws DataAccessException;

	Organization loadOrganization(int id) throws DataAccessException;

	ProjectProfile loadProjectProfile(int id) throws DataAccessException;

	Collection<ProjectProfile> loadProjectProfiles(int id) throws DataAccessException;

	ProjectBagInfo loadProjectBagInfo(int id) throws DataAccessException;

	void storeProject(Project project) throws DataAccessException;
	
	void storeProjectProfile(ProjectProfile prof) throws DataAccessException;

	void storeProfile(Profile prof) throws DataAccessException;
	
	void storeOrganization(Organization org) throws DataAccessException;

	String storeProjectBagInfo(ProjectBagInfo profileBagInfo) throws DataAccessException;

	String storeBaggerUpdates(Collection<Profile> profiles, Collection<Project> projects, Collection<ProjectProfile> projectProfiles, ProjectBagInfo projectBagInfo, String homeDir) throws DataAccessException;
	/* */
}
