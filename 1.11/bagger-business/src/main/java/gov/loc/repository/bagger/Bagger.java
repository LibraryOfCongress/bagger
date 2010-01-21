package gov.loc.repository.bagger;

import java.util.Collection;

import gov.loc.repository.bagger.Project;
import gov.loc.repository.bagger.Profile;
import gov.loc.repository.bagger.Organization;

import org.springframework.dao.DataAccessException;

public interface Bagger {

	Collection<Organization> getOrganizations() throws DataAccessException;

	String loadProfiles() throws DataAccessException;
	
	Collection<Project> getProjects() throws DataAccessException;

	Collection<ProjectProfile> getProjectProfiles() throws DataAccessException;

	Collection<Organization> findOrganizations(String name) throws DataAccessException;

	Collection<Profile> findProfiles(String name) throws DataAccessException;
	
	Collection<Project> findProjects(int personId) throws DataAccessException;

	Profile loadProfile(int id) throws DataAccessException;
	
	Contact loadContact(int id) throws DataAccessException;
	
	Person loadPerson(int id) throws DataAccessException;

	Organization loadOrganization(int id) throws DataAccessException;
	
	Project loadProject(int id) throws DataAccessException;

	ProjectProfile loadProjectProfile(int id) throws DataAccessException;

	Collection<ProjectProfile> loadProjectProfiles(int id) throws DataAccessException;

	ProjectBagInfo loadProjectBagInfo(int id) throws DataAccessException;

	void storeProject(Project project) throws DataAccessException;
	
	void storeProjectProfile(ProjectProfile prof) throws DataAccessException;

	void storeProfile(Profile prof) throws DataAccessException;
	
	void storeOrganization(Organization org) throws DataAccessException;
	
	String storeProjectBagInfo(ProjectBagInfo profileBagInfo) throws DataAccessException;

	String storeBaggerUpdates(Collection<Profile> profiles, Collection<Project> projects, Collection<ProjectProfile> projectProfiles, ProjectBagInfo projectBagInfo, String homeDir) throws DataAccessException;

}
