package gov.loc.repository.packagemodeler.dao;

import java.util.List;
import java.util.Map;

import org.hibernate.SessionFactory;

import gov.loc.repository.exceptions.RequiredEntityNotFound;
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
import gov.loc.repository.results.ResultList;

public interface PackageModelDAO {

	public SessionFactory getSessionFactory();
	
	//Package		
	public <T> T findPackage(Class<T> packageType, Repository repository, String packageId);

	public <T> T findPackage(Class<T> packageType, String repositoryId, String packageId);
	
	public <T> T findPackage(Class<T> packageType, long processInstanceId);
	
	public <T> T findRequiredPackage(Class<T> packageType, Repository repository, String packageId) throws RequiredEntityNotFound;

	public <T> T findRequiredPackage(Class<T> packageType, String repositoryId, String packageId) throws RequiredEntityNotFound;
		
	public List<Package> findPackages(Class<?> packageType);
	
	public abstract Long calculatePackageSize(Package packge);	

	public abstract ResultList findPackagesWithFileCount(Class<?> packageType, String extension);
	
	public Package loadRequiredPackage(Long key) throws RequiredEntityNotFound;
	
	//CanonicalFile
	public abstract Map<String,Long> countCanonicalFilesByExtension(Package packge);	

	public abstract CanonicalFile findCanonicalFile(String repositoryId, String packageId, String filename);
	
	public abstract void deleteCanonicalFiles(Package packge);

	//FileLocation
	public abstract FileLocation loadRequiredFileLocation(Long key) throws RequiredEntityNotFound;
	
	
	//FileInstance
	public abstract FileInstance findFileInstance(String repositoryId, String packageId, String storageServiceId, String basePath, String filename);
	
	public abstract FileInstance findFileInstance(FileLocation fileLocation, FileName fileName);
				
	public abstract List<FileInstance> findFileInstances(Repository repository, String relativePath, String baseName, String extension);	

	public abstract void deleteFileInstances(FileLocation fileLocation);
		
	//FileExamination
	public abstract FileExamination findFileExamination(FileExaminationGroup fileExaminationGroup, FileName fileName);

	
	//Repository
	public List<Repository> findRepositories();

	public Repository findRepository(String repositoryId);
	
	public Repository findRequiredRepository(String repositoryId) throws RequiredEntityNotFound;
					
	//Agent
	public <T extends Agent> T findAgent(Class<T> agentType, String agentId);
	
	public <T extends Agent> T findRequiredAgent(Class<T> agentType, String agentId) throws RequiredEntityNotFound;

	//Role
	public abstract Role findRole(String roleId);

	public abstract Role findRequiredRole(String roleId) throws RequiredEntityNotFound;	
						
	//Other
	public abstract void save(Object object);
	
	public abstract void delete(Object object);
	
	//Comparison
	public abstract FileListComparisonResult compare(FileLocation fileLocation, FileExaminationGroup fileExaminationGroup);
	
	public abstract FileListComparisonResult compare(Package packge, FileLocation fileLocation);

	
}