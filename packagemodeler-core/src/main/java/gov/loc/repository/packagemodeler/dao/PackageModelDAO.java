package gov.loc.repository.packagemodeler.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.Session;

import gov.loc.repository.packagemodeler.agents.Agent;
import gov.loc.repository.packagemodeler.agents.Role;
import gov.loc.repository.packagemodeler.packge.CanonicalFile;
import gov.loc.repository.packagemodeler.packge.FileExamination;
import gov.loc.repository.packagemodeler.packge.FileExaminationGroup;
import gov.loc.repository.packagemodeler.packge.FileInstance;
import gov.loc.repository.packagemodeler.packge.FileLocation;
import gov.loc.repository.packagemodeler.packge.FileName;
import gov.loc.repository.packagemodeler.packge.Package;
import gov.loc.repository.packagemodeler.packge.Repository;
import gov.loc.repository.utilities.results.ResultIterator;

public interface PackageModelDAO {

	public void setSession(Session session);
	
	//Package		
	public <T> T findPackage(Class<T> packageType, Repository repository, String packageId) throws Exception;

	public <T> T findPackage(Class<T> packageType, String repositoryId, String packageId) throws Exception;	
	
	public <T> T findRequiredPackage(Class<T> packageType, Repository repository, String packageId) throws Exception;

	public <T> T findRequiredPackage(Class<T> packageType, String repositoryId, String packageId) throws Exception;
		
	public List<Package> findPackages(Class packageType) throws Exception;
	
	public abstract Long calculatePackageSize(Package packge) throws Exception;	

	public abstract ResultIterator findPackagesWithFileCount(Class packageType, String extension) throws Exception;	
	
	//CanonicalFile
	public abstract Map<String,Long> countCanonicalFilesByExtension(Package packge) throws Exception;	

	public abstract CanonicalFile findCanonicalFile(String repositoryId, String packageId, String filename);
	
	//FileInstance
	public abstract FileInstance findFileInstance(String repositoryId, String packageId, String storageServiceId, String basePath, String filename) throws Exception;
	
	public abstract FileInstance findFileInstance(FileLocation fileLocation, FileName fileName) throws Exception;
				
	public abstract List<FileInstance> findFileInstances(Repository repository, String relativePath, String baseName, String extension) throws Exception;	

	//FileExamination
	public abstract FileExamination findFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName) throws Exception;

	
	//Repository
	public List<Repository> findRepositories() throws Exception;

	public Repository findRepository(String repositoryId) throws Exception;
	
	public Repository findRequiredRepository(String repositoryId) throws Exception;
					
	//Agent
	public <T extends Agent> T findAgent(Class<T> agentType, String agentId) throws Exception;
	
	public <T extends Agent> T findRequiredAgent(Class<T> agentType, String agentId) throws Exception;

	//Role
	public abstract Role findRole(String roleId) throws Exception;

	public abstract Role findRequiredRole(String roleId) throws Exception;	
						
	//Other
	public abstract void save(Object object) throws Exception;
	
	public abstract void delete(Object object) throws Exception;

	//Comparison
	public abstract FileListComparisonResult compare(FileLocation fileLocation, FileExaminationGroup fileExaminationGroup) throws Exception;
	
	public abstract FileListComparisonResult compare(Package packge, FileLocation fileLocation) throws Exception;

	
}